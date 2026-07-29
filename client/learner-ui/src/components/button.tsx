import type { PropsWithChildren } from 'react';
import { Pressable, StyleSheet, type PressableProps, type ViewStyle } from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { Radius, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type ButtonProps = PropsWithChildren<
  Pick<PressableProps, 'onPress' | 'disabled'> & {
    variant?: 'primary' | 'ghost';
    style?: ViewStyle;
  }
>;

/** Buttons dim to 75% opacity on press — no color inversion, no scale/bounce. */
export function Button({ children, onPress, disabled, variant = 'primary', style }: ButtonProps) {
  const theme = useTheme();
  const isPrimary = variant === 'primary';

  return (
    <Pressable
      onPress={onPress}
      disabled={disabled}
      style={({ pressed }) => [
        styles.base,
        isPrimary && { backgroundColor: theme.accentPrimary },
        disabled && styles.disabled,
        pressed && styles.pressed,
        style,
      ]}>
      <ThemedText type="headline" color={isPrimary ? 'textOnAccent' : 'accentPrimary'}>
        {children}
      </ThemedText>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    borderRadius: Radius.md,
    paddingVertical: Space[3],
    paddingHorizontal: Space[5],
    alignItems: 'center',
    justifyContent: 'center',
  },
  pressed: {
    opacity: 0.75,
  },
  disabled: {
    opacity: 0.4,
  },
});
