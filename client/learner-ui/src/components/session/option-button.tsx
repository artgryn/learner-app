import { Pressable, StyleSheet, type ViewStyle } from 'react-native';

import { ThemedText } from '@/components/ui/themed-text';
import { Radius, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type OptionState = 'neutral' | 'selected' | 'correct' | 'wrong';

type OptionButtonProps = {
  label: string;
  state: OptionState;
  /** 'center' for grid tiles, 'left' for a vertical option list. Defaults to 'center'. */
  align?: 'center' | 'left';
  onPress?: () => void;
  disabled?: boolean;
  style?: ViewStyle;
};

/** One answer tile/row — grid or vertical-list layout is up to the parent's flexbox container. */
export function OptionButton({
  label,
  state,
  align = 'center',
  onPress,
  disabled,
  style,
}: OptionButtonProps) {
  const theme = useTheme();

  const stateStyle =
    state === 'correct'
      ? { borderColor: theme.success, backgroundColor: theme.successTint }
      : state === 'wrong'
        ? { borderColor: theme.error, backgroundColor: theme.errorTint }
        : state === 'selected'
          ? { borderColor: theme.accentPrimary, backgroundColor: theme.accentPrimaryTint }
          : { borderColor: theme.borderHairline, backgroundColor: theme.surface };

  return (
    <Pressable
      onPress={onPress}
      disabled={disabled}
      style={({ pressed }) => [
        styles.tile,
        align === 'left' && styles.tileLeft,
        stateStyle,
        pressed && !disabled && styles.pressed,
        style,
      ]}>
      <ThemedText type="body" style={align === 'left' ? styles.labelLeft : styles.labelCenter}>
        {label}
      </ThemedText>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  tile: {
    borderWidth: 1.5,
    borderRadius: Radius.md,
    paddingVertical: Space[4],
    paddingHorizontal: Space[3],
    alignItems: 'center',
    justifyContent: 'center',
  },
  tileLeft: {
    alignItems: 'flex-start',
    paddingHorizontal: Space[4],
  },
  labelCenter: {
    textAlign: 'center',
  },
  labelLeft: {
    textAlign: 'left',
  },
  pressed: {
    opacity: 0.75,
  },
});
