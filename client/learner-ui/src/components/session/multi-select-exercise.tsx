import { useState } from 'react';
import { StyleSheet, View } from 'react-native';

import { Button } from '@/components/ui/button';
import { OptionButton, type OptionState } from '@/components/session/option-button';
import { Space } from '@/constants/theme';

type MultiSelectExerciseProps = {
  options: string[];
  correctAnswers: string[];
  onSubmit: (isCorrect: boolean) => void;
};

/** Toggle any number of options, then Confirm to grade the whole set at once. */
export function MultiSelectExercise({ options, correctAnswers, onSubmit }: MultiSelectExerciseProps) {
  const [selected, setSelected] = useState<Set<string>>(new Set());
  const [graded, setGraded] = useState(false);

  function toggle(option: string) {
    if (graded) return;
    setSelected((prev) => {
      const next = new Set(prev);
      if (next.has(option)) {
        next.delete(option);
      } else {
        next.add(option);
      }
      return next;
    });
  }

  function stateFor(option: string): OptionState {
    if (graded) {
      if (correctAnswers.includes(option)) return 'correct';
      if (selected.has(option)) return 'wrong';
      return 'neutral';
    }
    return selected.has(option) ? 'selected' : 'neutral';
  }

  function handleConfirm() {
    const isCorrect =
      selected.size === correctAnswers.length && correctAnswers.every((answer) => selected.has(answer));
    setGraded(true);
    onSubmit(isCorrect);
  }

  return (
    <View style={styles.container}>
      <View style={styles.grid}>
        {options.map((option) => (
          <OptionButton
            key={option}
            label={option}
            state={stateFor(option)}
            disabled={graded}
            onPress={() => toggle(option)}
            style={styles.tile}
          />
        ))}
      </View>
      {!graded && (
        <Button variant="primary" disabled={selected.size === 0} onPress={handleConfirm}>
          Confirm
        </Button>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: Space[5],
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Space[3],
  },
  tile: {
    flexBasis: '46%',
    flexGrow: 1,
  },
});
