import type { PropsWithChildren } from 'react';
import { StyleSheet, View } from 'react-native';

import { Card } from '@/components/ui/card';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';

type PromptCardProps = PropsWithChildren<{
  eyebrow: string;
}>;

/** Shared shell for every exercise/intro prompt — small label above, caller controls the prompt itself. */
export function PromptCard({ eyebrow, children }: PromptCardProps) {
  return (
    <Card style={styles.card}>
      <View style={styles.inner}>
        <ThemedText type="footnote" color="textSecondary">
          {eyebrow}
        </ThemedText>
        {children}
      </View>
    </Card>
  );
}

const styles = StyleSheet.create({
  card: {
    paddingVertical: Space[8],
  },
  inner: {
    alignItems: 'center',
    gap: Space[1],
  },
});
