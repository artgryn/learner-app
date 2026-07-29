import type { PropsWithChildren } from 'react';
import { StyleSheet, type ViewStyle } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedView } from '@/components/themed-view';
import { MaxContentWidth, Space } from '@/constants/theme';

type ScreenProps = PropsWithChildren<{
  style?: ViewStyle;
}>;

/** Paper background + safe area, centered to a max content width for large/web screens. */
export function Screen({ children, style }: ScreenProps) {
  return (
    <ThemedView style={styles.outer}>
      <SafeAreaView style={[styles.inner, style]}>{children}</SafeAreaView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  outer: {
    flex: 1,
    alignItems: 'center',
  },
  inner: {
    flex: 1,
    width: '100%',
    maxWidth: MaxContentWidth,
    paddingHorizontal: Space[5],
  },
});
