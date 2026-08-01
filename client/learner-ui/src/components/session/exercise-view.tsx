import { useRef, useState } from 'react';
import { StyleSheet, View } from 'react-native';

import { AssembleExercise } from '@/components/session/assemble-exercise';
import { MultiSelectExercise } from '@/components/session/multi-select-exercise';
import { PromptCard } from '@/components/session/prompt-card';
import { SingleChoiceExercise } from '@/components/session/single-choice-exercise';
import { Button } from '@/components/ui/button';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';
import type {
  AssemblePayload,
  EnEttPayload,
  ExerciseItem,
  MultiSelectPayload,
  OptionsPayload,
  Result,
  TranslatePayload,
} from '@/api';

// MVP scope: Swedish learned from English is the only supported pair.
const TARGET_LANG = 'sv';

// Instruction labels are constant-per-type and localized client-side — never sent in the
// payload (doc/CLAUDE.md). `translate` depends on direction: which side the prompt is in.
function instructionFor(item: ExerciseItem): string {
  switch (item.exerciseType) {
    case 'en_ett':
      return 'en or ett?';
    case 'translate':
      return item.prompt.lang === TARGET_LANG ? 'what does this mean?' : 'how do you say this in Swedish?';
    case 'assemble':
      return 'type this word in Swedish';
    case 'base_form':
      return "what's the base form of this word?";
    case 'produce_form':
      return 'which form fits here?';
    case 'multi_select':
      return 'select every correct form';
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

  const isSentence = item.prompt.text.includes(' ');

  return (
    <View style={styles.container}>
      <PromptCard eyebrow={instructionFor(item)}>
        <ThemedText type={isSentence ? 'title3' : 'word'} style={styles.promptText}>
          {item.prompt.text}
        </ThemedText>
      </PromptCard>

      {renderAnswerArea(item, handleSubmit)}

      {answered && (
        <Button variant="primary" style={styles.nextButton} onPress={onNext}>
          Next
        </Button>
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
  nextButton: {
    marginTop: 'auto',
  },
});
