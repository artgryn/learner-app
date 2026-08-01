import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

import { ApiError, getStats } from '@/api';
import type { Stats } from '@/api';
import { Card } from '@/components/ui/card';
import { IconButton } from '@/components/ui/icon-button';
import { NavBar } from '@/components/ui/nav-bar';
import { Screen } from '@/components/ui/screen';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type LoadState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; stats: Stats };

/** Optional for MVP per doc/app-info/UI/stats.md — cross-list totals, kept intentionally basic. */
export function StatsScreen() {
  const theme = useTheme();
  const router = useRouter();
  const [state, setState] = useState<LoadState>({ status: 'loading' });

  useEffect(() => {
    getStats()
      .then((stats) => setState({ status: 'ready', stats }))
      .catch((error) =>
        setState({
          status: 'error',
          message: error instanceof ApiError ? error.message : 'Could not load stats.',
        })
      );
  }, []);

  return (
    <Screen>
      <NavBar
        title="Stats"
        leading={
          <IconButton
            sf="chevron.left"
            feather="chevron-left"
            accessibilityLabel="Back"
            onPress={() => router.back()}
          />
        }
      />

      {state.status === 'loading' && (
        <ActivityIndicator color={theme.accentPrimary} style={styles.indicator} />
      )}
      {state.status === 'error' && (
        <ThemedText type="footnote" color="textSecondary" style={styles.message}>
          {state.message}
        </ThemedText>
      )}
      {state.status === 'ready' && (
        <Card style={styles.card}>
          <View style={styles.row}>
            <View style={styles.stat}>
              <ThemedText type="largeTitle">{state.stats.wordsKnown ?? 0}</ThemedText>
              <ThemedText type="footnote" color="textSecondary">
                words known
              </ThemedText>
            </View>
            <View style={styles.stat}>
              <ThemedText type="largeTitle">{state.stats.streakDays ?? 0}</ThemedText>
              <ThemedText type="footnote" color="textSecondary">
                day streak
              </ThemedText>
            </View>
          </View>
        </Card>
      )}
    </Screen>
  );
}

const styles = StyleSheet.create({
  message: {
    marginTop: Space[4],
  },
  indicator: {
    marginTop: Space[8],
  },
  card: {
    marginTop: Space[4],
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  stat: {
    alignItems: 'center',
    gap: Space[1],
  },
});
