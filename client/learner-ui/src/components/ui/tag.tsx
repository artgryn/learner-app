import type { PropsWithChildren } from 'react';
import { Pressable, StyleSheet } from 'react-native';

import { ThemedText } from '@/components/ui/themed-text';
import { Radius, Space, type ThemeColor } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type TagTone = 'default' | 'success' | 'highlight' | 'accent';

type TagProps = PropsWithChildren<{
  tone?: TagTone;
  onPress?: () => void;
  disabled?: boolean;
}>;

const TAG_COLORS: Record<TagTone, { background: ThemeColor; text: ThemeColor }> = {
  default: { background: 'tagFill', text: 'text' },
  success: { background: 'successTint', text: 'success' },
  highlight: { background: 'highlightTint', text: 'highlight' },
  accent: { background: 'accentPrimaryTint', text: 'accentPrimary' },
};

/** Small pill label/action — same press-dim convention as Button, no color inversion. */
export function Tag({ children, tone = 'default', onPress, disabled }: TagProps) {
  const theme = useTheme();
  const colors = TAG_COLORS[tone];

  return (
    <Pressable
      onPress={onPress}
      disabled={disabled}
      style={({ pressed }) => [
        styles.tag,
        { backgroundColor: theme[colors.background] },
        disabled && styles.disabled,
        pressed && styles.pressed,
      ]}>
      <ThemedText type="caption1" color={colors.text}>
        {children}
      </ThemedText>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  tag: {
    borderRadius: Radius.pill,
    paddingVertical: Space[1],
    paddingHorizontal: Space[3],
  },
  pressed: {
    opacity: 0.75,
  },
  disabled: {
    opacity: 0.6,
  },
});
