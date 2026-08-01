import { Feather } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { SymbolView } from 'expo-symbols';
import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

import {
  ApiError,
  deleteMe,
  getLanguagePairs,
  getMe,
  logout,
  requestPasswordReset,
  updateMe,
} from '@/api';
import type { AccountView, LanguagePair } from '@/api';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { ListRow } from '@/components/ui/list-row';
import { NavBar } from '@/components/ui/nav-bar';
import { Screen } from '@/components/ui/screen';
import { TextField } from '@/components/ui/text-field';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type LoadState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; account: AccountView; pairs: LanguagePair[] };

function pairLabel(account: AccountView, pairs: LanguagePair[]): string {
  const match = pairs.find(
    (p) => p.base.code === account.learnBaseLang && p.target.code === account.learnTargetLang
  );
  if (match) return `${match.base.name} → ${match.target.name}`;
  if (account.learnBaseLang && account.learnTargetLang) {
    return `${account.learnBaseLang} → ${account.learnTargetLang}`;
  }
  return 'Not set';
}

function Chevron() {
  const theme = useTheme();
  return <Feather name="chevron-right" size={16} color={theme.textSecondary} />;
}

function Checkmark() {
  const theme = useTheme();
  return (
    <SymbolView
      name="checkmark"
      size={16}
      tintColor={theme.accentPrimary}
      fallback={<Feather name="check" size={16} color={theme.accentPrimary} />}
    />
  );
}

export function AccountSettingsScreen() {
  const theme = useTheme();
  const router = useRouter();
  const [state, setState] = useState<LoadState>({ status: 'loading' });

  const [editingName, setEditingName] = useState(false);
  const [nameDraft, setNameDraft] = useState('');
  const [savingName, setSavingName] = useState(false);

  const [editingPair, setEditingPair] = useState(false);
  const [savingPair, setSavingPair] = useState(false);

  const [notice, setNotice] = useState<string | null>(null);
  const [confirmingDelete, setConfirmingDelete] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    Promise.all([getMe(), getLanguagePairs()])
      .then(([account, pairs]) => setState({ status: 'ready', account, pairs }))
      .catch((error) =>
        setState({
          status: 'error',
          message: error instanceof ApiError ? error.message : 'Could not load your account.',
        })
      );
  }, []);

  async function saveName() {
    if (state.status !== 'ready') return;
    setSavingName(true);
    try {
      const account = await updateMe({ name: nameDraft });
      setState({ ...state, account });
      setEditingName(false);
    } catch {
      // leave the field open so the user can retry
    } finally {
      setSavingName(false);
    }
  }

  async function choosePair(pair: LanguagePair) {
    if (state.status !== 'ready') return;
    setSavingPair(true);
    try {
      const account = await updateMe({
        learnBaseLang: pair.base.code,
        learnTargetLang: pair.target.code,
      });
      setState({ ...state, account });
      setEditingPair(false);
    } catch {
      // leave the picker open so the user can retry
    } finally {
      setSavingPair(false);
    }
  }

  async function handleResetPassword() {
    if (state.status !== 'ready') return;
    setNotice(null);
    try {
      await requestPasswordReset({ email: state.account.email });
      setNotice('Code sent to your email.');
    } catch {
      setNotice('Could not send the code. Please try again.');
    }
  }

  async function handleLogout() {
    // No client-side session/token store exists yet (auth is still stubbed per doc/CLAUDE.md) —
    // there's nothing to clear locally. This just calls the endpoint for parity with the API.
    try {
      await logout();
      setNotice('Logged out.');
    } catch {
      setNotice('Could not log out. Please try again.');
    }
  }

  async function handleDeleteAccount() {
    setDeleting(true);
    try {
      await deleteMe();
      setConfirmingDelete(false);
      setNotice('Account deleted.');
    } catch {
      setNotice('Could not delete your account. Please try again.');
    } finally {
      setDeleting(false);
    }
  }

  return (
    <Screen>
      <NavBar title="Account" />

      {state.status === 'loading' && (
        <ActivityIndicator color={theme.accentPrimary} style={styles.indicator} />
      )}
      {state.status === 'error' && (
        <ThemedText type="footnote" color="textSecondary" style={styles.message}>
          {state.message}
        </ThemedText>
      )}
      {state.status === 'ready' && (
        <View style={styles.content}>
          <Card padding={0} style={styles.card}>
            {editingName ? (
              <View style={styles.editRow}>
                <TextField label="Name" value={nameDraft} onChangeText={setNameDraft} />
                <View style={styles.editActions}>
                  <Button variant="ghost" disabled={savingName} onPress={() => setEditingName(false)}>
                    Cancel
                  </Button>
                  <Button variant="primary" disabled={savingName} onPress={saveName}>
                    Save
                  </Button>
                </View>
              </View>
            ) : (
              <ListRow
                title="Name"
                subtitle={state.account.name ?? 'Add your name'}
                trailing={<Chevron />}
                onPress={() => {
                  setNameDraft(state.account.name ?? '');
                  setEditingName(true);
                }}
              />
            )}
            <ListRow title="Email" subtitle={state.account.email} />
            <ListRow title="UI language" subtitle="English" showDivider={false} />
          </Card>

          <Card padding={0} style={styles.card}>
            {editingPair ? (
              <View style={styles.editRow}>
                <ThemedText type="footnote" color="textSecondary">
                  Language pair
                </ThemedText>
                {state.pairs.map((pair, i) => {
                  const selected =
                    state.account.learnBaseLang === pair.base.code &&
                    state.account.learnTargetLang === pair.target.code;
                  return (
                    <ListRow
                      key={`${pair.base.code}-${pair.target.code}`}
                      title={`${pair.base.name} → ${pair.target.name}`}
                      onPress={() => choosePair(pair)}
                      showDivider={i < state.pairs.length - 1}
                      trailing={selected ? <Checkmark /> : null}
                    />
                  );
                })}
                <Button variant="ghost" disabled={savingPair} onPress={() => setEditingPair(false)}>
                  Cancel
                </Button>
              </View>
            ) : (
              <ListRow
                title="Learning pair"
                subtitle={pairLabel(state.account, state.pairs)}
                trailing={<Chevron />}
                onPress={() => setEditingPair(true)}
                showDivider={false}
              />
            )}
          </Card>

          <Card padding={0} style={styles.card}>
            <ListRow
              title="Stats"
              trailing={<Chevron />}
              onPress={() => router.push('/settings/stats')}
            />
            <ListRow title="Reset password" trailing={<Chevron />} onPress={handleResetPassword} />
            <ListRow title="Log out" onPress={handleLogout} showDivider={false} />
          </Card>

          {notice ? (
            <ThemedText type="footnote" color="textSecondary">
              {notice}
            </ThemedText>
          ) : null}

          <Button variant="destructive" onPress={() => setConfirmingDelete(true)}>
            Delete account
          </Button>
        </View>
      )}

      <ConfirmDialog
        visible={confirmingDelete}
        title="Delete account?"
        message="This permanently deletes your account and all learning progress. This can't be undone."
        confirmLabel={deleting ? 'Deleting…' : 'Delete account'}
        onConfirm={handleDeleteAccount}
        onCancel={() => setConfirmingDelete(false)}
      />
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    gap: Space[4],
    paddingTop: Space[4],
  },
  card: {
    paddingHorizontal: Space[4],
  },
  editRow: {
    paddingVertical: Space[3],
    gap: Space[3],
  },
  editActions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: Space[2],
  },
  message: {
    marginTop: Space[4],
  },
  indicator: {
    marginTop: Space[8],
  },
});
