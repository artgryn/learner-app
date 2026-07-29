import type { PropsWithChildren } from 'react';
import { StyleSheet, type ViewStyle } from 'react-native';

import { ThemedView } from '@/components/themed-view';
import { Radius, Shadow, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type CardProps = PropsWithChildren<{
  padding?: number;
  style?: ViewStyle;
}>;

/** Flat surface: hairline border + a nearly-imperceptible shadow, never a drop-shadow look. */
export function Card({ children, padding = Space[5], style }: CardProps) {
  const theme = useTheme();

  return (
    <ThemedView
      type="surface"
      style={[styles.card, { padding, borderColor: theme.borderHairline }, Shadow.card, style]}>
      {children}
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  card: {
    borderRadius: Radius.lg,
    borderWidth: StyleSheet.hairlineWidth,
  },
});
