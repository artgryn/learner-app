import type { PropsWithChildren } from 'react';
import { Pressable, StyleSheet, type PressableProps, type ViewStyle } from 'react-native';

import { ThemedText } from '@/components/ui/themed-text';
import { Radius, Space, type ThemeColor } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type ButtonVariant = 'primary' | 'secondary' | 'destructive' | 'ghost';

type ButtonProps = PropsWithChildren<
  Pick<PressableProps, 'onPress' | 'disabled'> & {
    variant?: ButtonVariant;
    style?: ViewStyle;
  }
>;

const FILL_BACKGROUND: Partial<Record<ButtonVariant, ThemeColor>> = {
  primary: 'accentPrimary',
  secondary: 'surface',
  destructive: 'error',
};

const TEXT_COLOR: Record<ButtonVariant, ThemeColor> = {
  primary: 'textOnAccent',
  secondary: 'accentPrimary',
  destructive: 'textOnAccent',
  ghost: 'accentPrimary',
};

/** Buttons dim to 75% opacity on press — no color inversion, no scale/bounce. */
export function Button({ children, onPress, disabled, variant = 'primary', style }: ButtonProps) {
  const theme = useTheme();
  const backgroundKey = FILL_BACKGROUND[variant];

  return (
    <Pressable
      onPress={onPress}
      disabled={disabled}
      style={({ pressed }) => [
        styles.base,
        backgroundKey && { backgroundColor: theme[backgroundKey] },
        disabled && styles.disabled,
        pressed && styles.pressed,
        style,
      ]}>
      <ThemedText type="headline" color={TEXT_COLOR[variant]}>
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
