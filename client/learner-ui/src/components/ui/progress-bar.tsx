import { StyleSheet, View, type ViewStyle } from 'react-native';

import { Radius } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type ProgressBarProps = {
  /** 0–1. Clamped. */
  progress: number;
  /** `onAccent`: translucent white track + solid white fill, for use on an accent-colored card. */
  variant?: 'default' | 'onAccent';
  style?: ViewStyle;
};

export function ProgressBar({ progress, variant = 'default', style }: ProgressBarProps) {
  const theme = useTheme();
  const onAccent = variant === 'onAccent';
  const percent = Math.round(Math.max(0, Math.min(1, progress)) * 100);

  return (
    <View
      style={[
        styles.track,
        onAccent ? styles.trackOnAccent : { backgroundColor: theme.surfaceSunk },
        style,
      ]}>
      <View style={[styles.fill, { width: `${percent}%`, backgroundColor: onAccent ? '#FFFFFF' : theme.accentPrimary }]} />
    </View>
  );
}

const styles = StyleSheet.create({
  track: {
    height: 4,
    borderRadius: Radius.pill,
    overflow: 'hidden',
  },
  trackOnAccent: {
    height: 7,
    backgroundColor: 'rgba(255,255,255,0.25)',
  },
  fill: {
    height: '100%',
    borderRadius: Radius.pill,
  },
});
