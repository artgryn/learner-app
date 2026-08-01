import { ActivityIndicator, StyleSheet, View } from 'react-native';

import { Button } from '@/components/ui/button';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type GreetingsViewProps = {
  correctCount: number;
  totalCount: number;
  wordsTouched: number;
  submitStatus: 'submitting' | 'done' | 'failed';
  onRetry: () => void;
  onStartAnother: () => void;
  onBackHome: () => void;
};

/** Session summary — the client only finds out here (or on the next Home load) whether this
 * completion flipped the list to "completed" server-side; that flag isn't in CompleteResponse. */
export function GreetingsView({
  correctCount,
  totalCount,
  wordsTouched,
  submitStatus,
  onRetry,
  onStartAnother,
  onBackHome,
}: GreetingsViewProps) {
  const theme = useTheme();

  if (submitStatus === 'submitting') {
    return (
      <View style={styles.center}>
        <ActivityIndicator color={theme.accentPrimary} />
      </View>
    );
  }

  if (submitStatus === 'failed') {
    return (
      <View style={styles.container}>
        <View style={styles.summary}>
          <ThemedText type="title2">Couldn&apos;t save this session</ThemedText>
          <ThemedText type="subhead" color="textSecondary" style={styles.summaryText}>
            Your results are still on this device — nothing is lost. Try again when you have a
            connection.
          </ThemedText>
        </View>
        <View style={styles.actions}>
          <Button variant="primary" style={styles.fullWidth} onPress={onRetry}>
            Retry
          </Button>
          <Button variant="ghost" style={styles.fullWidth} onPress={onBackHome}>
            Back to home
          </Button>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.summary}>
        <ThemedText type="largeTitle">Nicely done</ThemedText>
        <ThemedText type="subhead" color="textSecondary" style={styles.summaryText}>
          {correctCount} of {totalCount} correct — {wordsTouched} word{wordsTouched === 1 ? '' : 's'}{' '}
          touched
        </ThemedText>
      </View>
      <View style={styles.actions}>
        <Button variant="primary" style={styles.fullWidth} onPress={onStartAnother}>
          Start another session
        </Button>
        <Button variant="secondary" style={styles.fullWidth} onPress={onBackHome}>
          Back to home
        </Button>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    gap: Space[8],
  },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  summary: {
    alignItems: 'center',
    gap: Space[2],
  },
  summaryText: {
    textAlign: 'center',
  },
  actions: {
    gap: Space[2],
  },
  fullWidth: {
    width: '100%',
  },
});
