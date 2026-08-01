import { useState } from 'react';
import { StyleSheet, View } from 'react-native';

import { OptionButton, type OptionState } from '@/components/session/option-button';
import { Space } from '@/constants/theme';

type SingleChoiceExerciseProps = {
  options: string[];
  correctAnswer: string;
  /** 'grid' for a 2-column layout (en_ett), 'list' for a vertical one (translate, base_form, produce_form). */
  layout: 'grid' | 'list';
  onSubmit: (isCorrect: boolean) => void;
};

/** Tap one option to grade instantly — shared by en_ett, translate, base_form, and produce_form. */
export function SingleChoiceExercise({ options, correctAnswer, layout, onSubmit }: SingleChoiceExerciseProps) {
  const [selected, setSelected] = useState<string | null>(null);

  function handlePress(option: string) {
    if (selected) return;
    setSelected(option);
    onSubmit(option === correctAnswer);
  }

  function stateFor(option: string): OptionState {
    if (!selected) return 'neutral';
    if (option === correctAnswer) return 'correct';
    if (option === selected) return 'wrong';
    return 'neutral';
  }

  return (
    <View style={layout === 'grid' ? styles.grid : styles.list}>
      {options.map((option) => (
        <OptionButton
          key={option}
          label={option}
          state={stateFor(option)}
          align={layout === 'list' ? 'left' : 'center'}
          disabled={!!selected}
          onPress={() => handlePress(option)}
          style={layout === 'grid' ? styles.gridTile : undefined}
        />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Space[3],
  },
  gridTile: {
    flexBasis: '46%',
    flexGrow: 1,
  },
  list: {
    gap: Space[3],
  },
});
