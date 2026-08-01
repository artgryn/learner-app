import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

import { ApiError, completeSession, createSession } from '@/api';
import type { Result, SessionResponse } from '@/api';
import { GreetingsView } from '@/components/session/greetings-view';
import { ExerciseView } from '@/components/session/exercise-view';
import { IntroCardView } from '@/components/session/intro-card-view';
import { Button } from '@/components/ui/button';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { IconButton } from '@/components/ui/icon-button';
import { ProgressBar } from '@/components/ui/progress-bar';
import { Screen } from '@/components/ui/screen';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type Phase =
  | { status: 'loading' }
  | { status: 'active'; session: SessionResponse; index: number; results: Result[] }
  | { status: 'empty' }
  | { status: 'error'; message: string }
  | {
      status: 'summary';
      session: SessionResponse;
      results: Result[];
      submitStatus: 'submitting' | 'done' | 'failed';
    };

type SessionScreenProps = {
  listId: number;
};

export function SessionScreen({ listId }: SessionScreenProps) {
  const theme = useTheme();
  const router = useRouter();
  const [phase, setPhase] = useState<Phase>({ status: 'loading' });
  const [confirmingExit, setConfirmingExit] = useState(false);

  function load() {
    setPhase({ status: 'loading' });
    createSession(listId)
      .then((session) => {
        setPhase(
          session.items.length === 0
            ? { status: 'empty' }
            : { status: 'active', session, index: 0, results: [] }
        );
      })
      .catch((error) =>
        setPhase({
          status: 'error',
          message: error instanceof ApiError ? error.message : 'Could not start this session.',
        })
      );
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [listId]);

  function submitResults(session: SessionResponse, results: Result[]) {
    setPhase({ status: 'summary', session, results, submitStatus: 'submitting' });
    completeSession(session.sessionId, { results })
      .then(() => setPhase({ status: 'summary', session, results, submitStatus: 'done' }))
      .catch(() => setPhase({ status: 'summary', session, results, submitStatus: 'failed' }));
  }

  function advance() {
    if (phase.status !== 'active') return;
    const nextIndex = phase.index + 1;
    if (nextIndex >= phase.session.items.length) {
      submitResults(phase.session, phase.results);
    } else {
      setPhase({ ...phase, index: nextIndex });
    }
  }

  function handleAnswered(result: Result) {
    if (phase.status !== 'active') return;
    setPhase({ ...phase, results: [...phase.results, result] });
  }

  function goHome() {
    router.dismissTo('/');
  }

  const showExit = phase.status === 'loading' || phase.status === 'active' || phase.status === 'error';
  const currentItem = phase.status === 'active' ? phase.session.items[phase.index] : null;
  const progress = phase.status === 'active' ? (phase.index + 1) / phase.session.items.length : 0;

  return (
    <Screen>
      {showExit && (
        <View style={styles.header}>
          <IconButton
            sf="xmark"
            feather="x"
            accessibilityLabel="End session"
            onPress={() => setConfirmingExit(true)}
          />
          {phase.status === 'active' && (
            <ProgressBar progress={progress} style={styles.progressBar} />
          )}
        </View>
      )}

      {phase.status === 'loading' && (
        <View style={styles.center}>
          <ActivityIndicator color={theme.accentPrimary} />
        </View>
      )}

      {phase.status === 'error' && (
        <View style={styles.center}>
          <ThemedText type="footnote" color="textSecondary" style={styles.message}>
            {phase.message}
          </ThemedText>
        </View>
      )}

      {phase.status === 'empty' && (
        <View style={styles.center}>
          <ThemedText type="title3">You&apos;re caught up</ThemedText>
          <ThemedText type="footnote" color="textSecondary" style={styles.message}>
            Nothing due and no new words right now.
          </ThemedText>
          <Button variant="primary" style={styles.emptyButton} onPress={goHome}>
            Back to home
          </Button>
        </View>
      )}

      {phase.status === 'active' && currentItem && (
        <View style={styles.itemArea} key={currentItem.itemId}>
          {currentItem.itemType === 'introduce' ? (
            <IntroCardView card={currentItem.card} onNext={advance} />
          ) : (
            <ExerciseView item={currentItem} onAnswered={handleAnswered} onNext={advance} />
          )}
        </View>
      )}

      {phase.status === 'summary' && (
        <GreetingsView
          submitStatus={phase.submitStatus}
          correctCount={phase.results.filter((r) => r.isCorrect).length}
          totalCount={phase.results.length}
          wordsTouched={new Set(phase.results.map((r) => r.lexemeId)).size}
          onRetry={() => submitResults(phase.session, phase.results)}
          onStartAnother={load}
          onBackHome={goHome}
        />
      )}

      <ConfirmDialog
        visible={confirmingExit}
        title="End this session?"
        message="Progress on words you've already mastered is saved, but this session will restart from the beginning next time."
        confirmLabel="End session"
        cancelLabel="Keep going"
        onConfirm={goHome}
        onCancel={() => setConfirmingExit(false)}
      />
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Space[3],
    paddingTop: Space[3],
  },
  progressBar: {
    flex: 1,
  },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: Space[2],
  },
  message: {
    textAlign: 'center',
  },
  emptyButton: {
    marginTop: Space[4],
  },
  itemArea: {
    flex: 1,
    paddingTop: Space[5],
  },
});
