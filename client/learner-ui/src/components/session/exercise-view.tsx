import { useEffect, useRef, useState } from 'react';
import { StyleSheet, View } from 'react-native';

import type {
  AssemblePayload,
  EnEttPayload,
  ExerciseItem,
  ExerciseType,
  MultiSelectPayload,
  OptionsPayload,
  Result,
  TranslatePayload,
} from '@/api';
import { AssembleExercise } from '@/components/session/assemble-exercise';
import { MultiSelectExercise } from '@/components/session/multi-select-exercise';
import { PromptCard } from '@/components/session/prompt-card';
import { SingleChoiceExercise } from '@/components/session/single-choice-exercise';
import { AutoAdvanceBar } from '@/components/ui/auto-advance-bar';
import { Button } from '@/components/ui/button';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';

// MVP scope: Swedish learned from English is the only supported pair.
const TARGET_LANG = 'sv';

// Types that auto-advance once graded, instead of waiting on a manual Next: "select one from
// many" grades on tap, and assemble grades on Check — both already have an explicit moment
// where the answer is final, so there's nothing left for a Next button to gate. multi_select
// keeps manual Next: Confirm only locks in the *set* of picks, and letting a wrong multi-select
// flash by after just 1s reads worse when there's more to visually parse (several right/wrong
// tiles at once, not one).
const AUTO_ADVANCE_TYPES = new Set<ExerciseType>([
  'en_ett',
  'translate',
  'base_form',
  'produce_form',
  'assemble',
]);
const DEFAULT_AUTO_ADVANCE_DELAY_MS = 3000;
// assemble reveals the correct answer on a miss (see assemble-exercise.tsx) — that's a shorter,
// simpler read than the multi-option layouts, so it gets a shorter delay.
const AUTO_ADVANCE_DELAY_OVERRIDE_MS: Partial<Record<ExerciseType, number>> = {
  assemble: 3000,
};

function autoAdvanceDelayFor(type: ExerciseType): number {
  return AUTO_ADVANCE_DELAY_OVERRIDE_MS[type] ?? DEFAULT_AUTO_ADVANCE_DELAY_MS;
}

// Swedish form_type -> human label (doc/app-info/Data/word_form.md "full name" column).
// MVP scope is Swedish-target-only (TARGET_LANG), so this catalog isn't parameterized by language.
const SV_FORM_TYPE_LABELS: Record<string, string> = {
  indef_sg: 'indefinite singular',
  def_sg: 'definite singular',
  indef_pl: 'indefinite plural',
  def_pl: 'definite plural',
  infinitive: 'infinitive',
  present: 'present',
  preteritum: 'past',
  supine: 'supine',
  imperative: 'imperative',
  present_participle: 'present participle',
  past_participle: 'past participle',
  subject: 'subject form',
  object: 'object form',
  possessive_c: 'possessive (en-gender)',
  possessive_n: 'possessive (ett-gender)',
  possessive_pl: 'possessive plural',
  base: 'base form',
};

// Instruction labels are constant-per-type and localized client-side — never sent in the
// payload (doc/CLAUDE.md). `translate` depends on direction: which side the prompt is in.
// `produce_form` depends on formType: naming the target form is what makes the exercise have
// a single correct answer at all — sibling forms in the options (e.g. "flickan" vs "flickor"
// vs "flickorna" for "flicka") are otherwise indistinguishable without it.
function instructionFor(item: ExerciseItem): string {
  switch (item.exerciseType) {
    case 'en_ett':
      return 'en or ett?';
    case 'translate':
      return item.prompt.lang === TARGET_LANG ? 'What does this mean?' : 'How do you say this in Swedish?';
    case 'assemble':
      return 'Type this word in Swedish';
    case 'base_form':
      return "Find the base form of the word.";
    case 'produce_form': {
      const label = item.formType ? SV_FORM_TYPE_LABELS[item.formType] : undefined;
      return label ? `Give the ${label} form` : 'Which form fits here?';
    }
    case 'multi_select':
      return 'Select all words that belongs to this.';
  }
}

type ExerciseViewProps = {
  item: ExerciseItem;
  onAnswered: (result: Result) => void;
  onNext: () => void;
};

/** One graded task, polymorphic by exerciseType. Rendered from the item's payload; graded locally. */
export function ExerciseView({ item, onAnswered, onNext }: ExerciseViewProps) {
  const [answered, setAnswered] = useState(false);
  const startedAtRef = useRef(Date.now());
  const onNextRef = useRef(onNext);
  onNextRef.current = onNext;

  const autoAdvance = AUTO_ADVANCE_TYPES.has(item.exerciseType);
  const autoAdvanceDelay = autoAdvanceDelayFor(item.exerciseType);

  function handleSubmit(isCorrect: boolean) {
    setAnswered(true);
    onAnswered({
      itemId: item.itemId,
      lexemeId: item.lexemeId,
      exerciseType: item.exerciseType,
      formType: item.formType ?? null,
      isCorrect,
      elapsedMs: Date.now() - startedAtRef.current,
    });
  }

  // Ref (not `onNext` itself) in the dependency array so a parent re-render — which onAnswered
  // triggers — can't reset or double-schedule this timer with a fresh onNext closure.
  useEffect(() => {
    if (!answered || !autoAdvance) return;
    const timeout = setTimeout(() => onNextRef.current(), autoAdvanceDelay);
    return () => clearTimeout(timeout);
  }, [answered, autoAdvance, autoAdvanceDelay]);

  const isSentence = item.prompt.text.includes(' ');

  return (
    <View style={styles.container}>
      <PromptCard eyebrow={instructionFor(item)}>
        <ThemedText type={isSentence ? 'title3' : 'word'} style={styles.promptText}>
          {item.prompt.text}
        </ThemedText>
      </PromptCard>

      {renderAnswerArea(item, handleSubmit)}

      {answered && !autoAdvance && (
        <Button variant="primary" style={styles.nextButton} onPress={onNext}>
          Next
        </Button>
      )}
      {answered && autoAdvance && (
        <View style={styles.autoAdvanceBar}>
          <AutoAdvanceBar durationMs={autoAdvanceDelay} />
        </View>
      )}
    </View>
  );
}

function renderAnswerArea(item: ExerciseItem, onSubmit: (isCorrect: boolean) => void) {
  switch (item.exerciseType) {
    case 'en_ett': {
      const payload = item.exercise as EnEttPayload;
      return (
        <SingleChoiceExercise
          options={payload.options}
          correctAnswer={payload.correctAnswer}
          layout="grid"
          onSubmit={onSubmit}
        />
      );
    }
    case 'translate': {
      const payload = item.exercise as TranslatePayload;
      return (
        <SingleChoiceExercise
          options={payload.options}
          correctAnswer={payload.correctAnswer}
          layout="list"
          onSubmit={onSubmit}
        />
      );
    }
    case 'base_form':
    case 'produce_form': {
      const payload = item.exercise as OptionsPayload;
      return (
        <SingleChoiceExercise
          options={payload.options}
          correctAnswer={payload.correctAnswer}
          layout="list"
          onSubmit={onSubmit}
        />
      );
    }
    case 'assemble': {
      const payload = item.exercise as AssemblePayload;
      return (
        <AssembleExercise
          letters={payload.letters}
          answerLength={payload.answerLength}
          correctAnswer={payload.correctAnswer}
          onSubmit={onSubmit}
        />
      );
    }
    case 'multi_select': {
      const payload = item.exercise as MultiSelectPayload;
      return (
        <MultiSelectExercise options={payload.options} correctAnswers={payload.correctAnswers} onSubmit={onSubmit} />
      );
    }
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    gap: Space[5],
  },
  promptText: {
    marginTop: Space[1],
    textAlign: 'center',
  },
  // marginBottom kept in sync with list-details-screen's CTA and intro-card-view's Next button —
  // every screen's bottom-pinned primary action sits the same distance above the safe area.
  nextButton: {
    marginTop: 'auto',
    marginBottom: Space[5],
  },
  autoAdvanceBar: {
    marginTop: 'auto',
    marginBottom: Space[5],
  },
});
