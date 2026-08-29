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
  // A NaN/Infinity width string (e.g. from a stray 0/0 upstream) is silently ignored by web CSS
  // but can break native layout outright — guard it explicitly rather than relying on that.
  const clamped = Number.isFinite(progress) ? Math.max(0, Math.min(1, progress)) : 0;
  const percent = Math.round(clamped * 100);

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
    width: '100%',
    height: 4,
    borderRadius: Radius.pill,
    overflow: 'hidden',
  },
  trackOnAccent: {
    height: 7,
    backgroundColor: 'rgba(255,255,255,0.25)',
    borderWidth: 1,
    borderColor: 'rgba(0,0,0,0.15)',
  },
  fill: {
    height: '100%',
    borderRadius: Radius.pill,
  },
});
