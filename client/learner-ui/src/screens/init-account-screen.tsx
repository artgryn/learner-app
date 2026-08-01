import { Feather } from '@expo/vector-icons';
import { SymbolView } from 'expo-symbols';
import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

import { ApiError, getPublicInit, updateMe, type LanguagePair } from '@/api';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ListRow } from '@/components/ui/list-row';
import { Screen } from '@/components/ui/screen';
import { TextField } from '@/components/ui/text-field';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type InitAccountScreenProps = {
  onDone: () => void;
};

type LoadState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; pairs: LanguagePair[] };

function pairLabel(pair: LanguagePair): string {
  return `${pair.base.name} → ${pair.target.name}`;
}

export function InitAccountScreen({ onDone }: InitAccountScreenProps) {
  const theme = useTheme();
  const [state, setState] = useState<LoadState>({ status: 'loading' });
  const [name, setName] = useState('');
  const [selected, setSelected] = useState<LanguagePair | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getPublicInit()
      .then(({ languagePairs }) => {
        setState({ status: 'ready', pairs: languagePairs });
        setSelected(languagePairs[0] ?? null);
      })
      .catch((err) =>
        setState({
          status: 'error',
          message: err instanceof ApiError ? err.message : 'Could not load language pairs.',
        })
      );
  }, []);

  async function handleContinue() {
    if (!selected) return;
    setSubmitting(true);
    setError(null);
    try {
      await updateMe({
        name,
        learnBaseLang: selected.base.code,
        learnTargetLang: selected.target.code,
      });
      onDone();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Could not save. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Screen>
      <View style={styles.header}>
        <ThemedText type="largeTitle">Set up your learning</ThemedText>
        <ThemedText type="subhead" color="textSecondary" style={styles.subtitle}>
          You can change this later in settings
        </ThemedText>
      </View>

      <View style={styles.content}>
        <TextField label="Your name" value={name} onChangeText={setName} placeholder="Alex" />

        {state.status === 'loading' && (
          <ActivityIndicator color={theme.accentPrimary} style={styles.indicator} />
        )}
        {state.status === 'error' && (
          <ThemedText type="footnote" color="textSecondary">
            {state.message}
          </ThemedText>
        )}
        {state.status === 'ready' && (
          <View>
            <ThemedText type="footnote" color="textSecondary" style={styles.pairLabel}>
              Language pair
            </ThemedText>
            <Card padding={0} style={styles.listCard}>
              {state.pairs.map((pair, i) => {
                const isSelected = selected ? pairLabel(selected) === pairLabel(pair) : false;
                return (
                  <ListRow
                    key={pairLabel(pair)}
                    title={pairLabel(pair)}
                    onPress={() => setSelected(pair)}
                    showDivider={i < state.pairs.length - 1}
                    trailing={
                      isSelected ? (
                        <SymbolView
                          name="checkmark"
                          size={18}
                          tintColor={theme.accentPrimary}
                          fallback={<Feather name="check" size={18} color={theme.accentPrimary} />}
                        />
                      ) : null
                    }
                  />
                );
              })}
            </Card>
          </View>
        )}

        {error ? (
          <ThemedText type="footnote" color="error">
            {error}
          </ThemedText>
        ) : null}
      </View>

      <Button
        disabled={!selected || submitting}
        style={styles.continueButton}
        onPress={handleContinue}>
        Continue
      </Button>
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: {
    paddingTop: Space[8],
  },
  subtitle: {
    marginTop: Space[1],
  },
  content: {
    flex: 1,
    paddingTop: Space[5],
    gap: Space[5],
  },
  pairLabel: {
    marginBottom: Space[2],
  },
  listCard: {
    paddingHorizontal: Space[4],
  },
  indicator: {
    marginTop: Space[8],
  },
  continueButton: {
    marginBottom: Space[5],
  },
});
