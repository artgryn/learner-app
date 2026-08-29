import { useState } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';

import { Button } from '@/components/ui/button';
import { ThemedText } from '@/components/ui/themed-text';
import { Radius, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type AssembleExerciseProps = {
  /** Pre-shuffled, longer than the answer, positional (may contain duplicates). */
  letters: string[];
  answerLength: number;
  correctAnswer: string;
  onSubmit: (isCorrect: boolean) => void;
};

/** Tap bank letters into slots in order; tap a filled slot to undo. Check once every slot is full. */
export function AssembleExercise({ letters, answerLength, correctAnswer, onSubmit }: AssembleExerciseProps) {
  const theme = useTheme();
  const [slots, setSlots] = useState<(number | null)[]>(() => Array(answerLength).fill(null));
  const [usedIndices, setUsedIndices] = useState<Set<number>>(new Set());
  const [graded, setGraded] = useState<boolean | null>(null);

  function tapBankLetter(bankIndex: number) {
    if (graded !== null || usedIndices.has(bankIndex)) return;
    const nextEmptySlot = slots.indexOf(null);
    if (nextEmptySlot === -1) return;
    const nextSlots = [...slots];
    nextSlots[nextEmptySlot] = bankIndex;
    setSlots(nextSlots);
    setUsedIndices((prev) => new Set(prev).add(bankIndex));
  }

  function tapSlot(slotIndex: number) {
    if (graded !== null) return;
    const bankIndex = slots[slotIndex];
    if (bankIndex === null) return;
    const nextSlots = [...slots];
    nextSlots[slotIndex] = null;
    setSlots(nextSlots);
    setUsedIndices((prev) => {
      const next = new Set(prev);
      next.delete(bankIndex);
      return next;
    });
  }

  const isFull = slots.every((s) => s !== null);

  function handleCheck() {
    const assembled = slots.map((i) => (i !== null ? letters[i] : '')).join('');
    const isCorrect = assembled === correctAnswer;
    setGraded(isCorrect);
    onSubmit(isCorrect);
  }

  const slotStyle =
    graded === true
      ? { borderColor: theme.success, backgroundColor: theme.successTint }
      : graded === false
        ? { borderColor: theme.error, backgroundColor: theme.errorTint }
        : { borderColor: theme.accentPrimary, backgroundColor: theme.accentPrimaryTint };

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        {slots.map((bankIndex, i) => (
          <Pressable key={i} onPress={() => tapSlot(i)} style={[styles.slot, slotStyle]}>
            <ThemedText type="word" style={styles.letter}>
              {bankIndex !== null ? letters[bankIndex] : ''}
            </ThemedText>
          </Pressable>
        ))}
      </View>
      <View style={styles.row}>
        {letters.map((letter, i) => {
          const spent = usedIndices.has(i);
          return (
            <Pressable
              key={i}
              onPress={() => tapBankLetter(i)}
              disabled={spent || graded !== null}
              style={[
                styles.bankTile,
                { borderColor: theme.borderHairline, backgroundColor: theme.surface },
                spent && styles.bankTileSpent,
              ]}>
              <ThemedText type="word" style={[styles.letter, spent && { color: theme.textPlaceholder }]}>
                {letter}
              </ThemedText>
            </Pressable>
          );
        })}
      </View>
      {graded === null && (
        <Button variant="primary" disabled={!isFull} onPress={handleCheck}>
          Check
        </Button>
      )}
      {graded === false && (
        <View style={[styles.correctAnswerCard, { borderColor: theme.error, backgroundColor: theme.errorTint }]}>
          <ThemedText type="caption1" color="error" style={styles.correctAnswerLabel}>
            CORRECT ANSWER
          </ThemedText>
          <ThemedText type="word" color="error" style={styles.correctAnswerWord}>
            {correctAnswer}
          </ThemedText>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: Space[5],
  },
  row: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: Space[2],
  },
  slot: {
    width: 42,
    height: 48,
    borderWidth: 1.5,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  bankTile: {
    width: 42,
    height: 48,
    borderWidth: 1,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  bankTileSpent: {
    opacity: 0.4,
  },
  letter: {
    fontSize: 20,
  },
  correctAnswerCard: {
    borderWidth: 1.5,
    borderRadius: Radius.md,
    paddingVertical: Space[3],
    paddingHorizontal: Space[4],
    alignItems: 'center',
    gap: Space[1],
  },
  correctAnswerLabel: {
    letterSpacing: 0.5,
  },
  correctAnswerWord: {
    fontSize: 22,
  },
});
