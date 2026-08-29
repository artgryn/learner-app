import { useEffect, useRef } from 'react';
import { Animated, StyleSheet } from 'react-native';

import { Radius } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type AutoAdvanceBarProps = {
  durationMs: number;
};

// Self-driving (unlike ProgressBar, which is externally controlled) — this fills once, on
// mount, over `durationMs`. Visual proof that the auto-advance delay between exercises is
// ticking down, not the app hanging.
export function AutoAdvanceBar({ durationMs }: AutoAdvanceBarProps) {
  const theme = useTheme();
  const anim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    anim.setValue(0);
    Animated.timing(anim, { toValue: 1, duration: durationMs, useNativeDriver: false }).start();
  }, [anim, durationMs]);

  const width = anim.interpolate({ inputRange: [0, 1], outputRange: ['0%', '100%'] });

  return (
    <Animated.View style={[styles.track, { backgroundColor: theme.surfaceSunk }]}>
      <Animated.View style={[styles.fill, { width, backgroundColor: theme.accentPrimary }]} />
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  track: {
    width: '100%',
    height: 4,
    borderRadius: Radius.pill,
    overflow: 'hidden',
  },
  fill: {
    height: '100%',
    borderRadius: Radius.pill,
  },
});
