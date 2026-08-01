import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, View } from 'react-native';

import { ApiError, getHome } from '@/api';
import type { Enrollment, HomeResponse } from '@/api';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ProgressBar } from '@/components/ui/progress-bar';
import { Screen } from '@/components/ui/screen';
import { ThemedText } from '@/components/ui/themed-text';
import { Radius, Shadow, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type LoadState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; home: HomeResponse };

function greetingPeriod(hour: number): string {
  if (hour < 12) return 'morning';
  if (hour < 18) return 'afternoon';
  return 'evening';
}

export function HomeScreen() {
  const theme = useTheme();
  const router = useRouter();
  const [state, setState] = useState<LoadState>({ status: 'loading' });

  useEffect(() => {
    getHome()
      .then((home) => setState({ status: 'ready', home }))
      .catch((error) =>
        setState({
          status: 'error',
          message: error instanceof ApiError ? error.message : 'Could not load your home screen.',
        })
      );
  }, []);

  const name = state.status === 'ready' ? state.home.user?.name : null;
  const greeting = `Good ${greetingPeriod(new Date().getHours())}${name ? `, ${name}` : ''}`;
  const activeEnrollment =
    state.status === 'ready' ? state.home.enrollments?.find((e) => e.status === 'active') : undefined;

  return (
    <Screen>
      <View style={styles.content}>
        <ThemedText type="title2">{greeting}</ThemedText>

        {state.status === 'loading' && (
          <ActivityIndicator color={theme.accentPrimary} style={styles.indicator} />
        )}
        {state.status === 'error' && (
          <ThemedText type="footnote" color="textSecondary">
            {state.message}
          </ThemedText>
        )}
        {state.status === 'ready' && !activeEnrollment && (
          <Card>
            <View style={styles.empty}>
              <ThemedText type="body" color="textSecondary">
                You&apos;re not learning a list yet
              </ThemedText>
              <Button variant="primary" onPress={() => router.push('/lists')}>
                Browse lists
              </Button>
            </View>
          </Card>
        )}
        {state.status === 'ready' && activeEnrollment && <ContinueCard enrollment={activeEnrollment} />}
      </View>
    </Screen>
  );
}

function ContinueCard({ enrollment }: { enrollment: Enrollment }) {
  const theme = useTheme();
  const router = useRouter();
  const mastered = enrollment.wordsMastered ?? 0;
  const total = enrollment.totalWords ?? 0;
  const progress = total > 0 ? mastered / total : 0;
  const sessionsDone = enrollment.sessions?.done ?? 0;
  const sessionsEstimate = enrollment.sessions?.estimatedTotal;

  function startSession() {
    router.push(`/lists/${enrollment.listId}/session`);
  }

  return (
    <Pressable style={[styles.hero, { backgroundColor: theme.accentPrimary }]} onPress={startSession}>
      <ProgressBar progress={progress} variant="onAccent" style={styles.heroProgress} />
      <View style={styles.heroBody}>
        <View>
          <ThemedText type="footnote" style={styles.heroMuted}>
            Continue learning
          </ThemedText>
          <ThemedText type="title1" style={styles.heroTitle}>
            {enrollment.name}
          </ThemedText>
          <ThemedText type="footnote" style={styles.heroMuted}>
            {mastered}/{total} words
            {sessionsEstimate ? ` · session ${sessionsDone + 1} of ~${sessionsEstimate}` : ''}
          </ThemedText>
        </View>
        <Button variant="secondary" style={styles.heroButton} onPress={startSession}>
          New session
        </Button>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  content: {
    gap: Space[4],
    paddingTop: Space[6],
  },
  indicator: {
    marginTop: Space[8],
  },
  empty: {
    alignItems: 'center',
    gap: Space[3],
  },
  hero: {
    borderRadius: Radius.md,
    overflow: 'hidden',
    ...Shadow.sheet,
  },
  heroProgress: {
    borderRadius: 0,
  },
  heroBody: {
    padding: Space[6],
    gap: Space[4],
  },
  heroMuted: {
    color: 'rgba(255,255,255,0.85)',
  },
  heroTitle: {
    color: '#FFFFFF',
    marginTop: 2,
  },
  heroButton: {
    width: '100%',
  },
});
