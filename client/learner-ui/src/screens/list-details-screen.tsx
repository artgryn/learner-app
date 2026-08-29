import { Feather } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, ScrollView, StyleSheet, View } from 'react-native';

import { ApiError, enrollInList, getEnrollments, getListDetail, getListProgress, getListWords } from '@/api';
import type { Enrollment, ListDetail, ListWord, WordProgress } from '@/api';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { IconButton } from '@/components/ui/icon-button';
import { ListRow } from '@/components/ui/list-row';
import { NavBar } from '@/components/ui/nav-bar';
import { Screen } from '@/components/ui/screen';
import { Tag } from '@/components/ui/tag';
import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

// MVP scope: Swedish learned from English is the only supported pair.
const BASE_LANG = 'en';

type ListStatus = 'not-enrolled' | 'active' | 'completed';

type LoadState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; list: ListDetail; enrollments: Enrollment[]; words: ListWord[] };

type ListDetailsScreenProps = {
  listId: number;
};

export function ListDetailsScreen({ listId }: ListDetailsScreenProps) {
  const theme = useTheme();
  const router = useRouter();
  const [state, setState] = useState<LoadState>({ status: 'loading' });
  const [progressByLexeme, setProgressByLexeme] = useState<Map<number, WordProgress>>(new Map());
  const [enrolling, setEnrolling] = useState(false);
  const [confirmingSwitch, setConfirmingSwitch] = useState(false);

  useEffect(() => {
    Promise.all([getListDetail(listId), getEnrollments(), getListWords(listId, BASE_LANG)])
      .then(([list, enrollments, words]) => setState({ status: 'ready', list, enrollments, words }))
      .catch((error) =>
        setState({
          status: 'error',
          message: error instanceof ApiError ? error.message : 'Could not load this list.',
        })
      );
  }, [listId]);

  const ready = state.status === 'ready' ? state : null;
  const thisEnrollment = ready?.enrollments.find((e) => e.listId === listId);
  const activeEnrollment = ready?.enrollments.find((e) => e.status === 'active');
  const status: ListStatus = thisEnrollment?.status ?? 'not-enrolled';
  // "Start learning" reads as the CTA for something not yet begun — reserve it for lists with no
  // progress. An already-active (in-progress) list gets "Continue" instead.
  const cta = status === 'completed' ? 'Restart' : status === 'active' ? 'Continue' : 'Start learning';

  // Per-word mastery only exists once enrolled — fetched separately from the catalog word list.
  useEffect(() => {
    if (!thisEnrollment) {
      setProgressByLexeme(new Map());
      return;
    }
    getListProgress(listId)
      .then((progress) => setProgressByLexeme(new Map(progress.map((p) => [p.lexemeId, p]))))
      .catch(() => setProgressByLexeme(new Map()));
  }, [listId, thisEnrollment]);

  async function enroll() {
    setConfirmingSwitch(false);
    setEnrolling(true);
    try {
      await enrollInList(listId, BASE_LANG);
      // push('/') alone only swaps the content *under* this page sheet — the sheet itself
      // stays presented since push is additive, not a dismiss. back() closes the sheet; push('/')
      // then crosses from the Lists tab to Home (same mechanism "Browse lists" uses in reverse).
      if (router.canGoBack()) {
        router.back();
      }
      router.push('/');
    } catch {
      setEnrolling(false);
    }
  }

  function handleCtaPress() {
    if (status === 'active') {
      router.push(`/lists/${listId}/session`);
      return;
    }
    if (activeEnrollment && activeEnrollment.listId !== listId) {
      setConfirmingSwitch(true);
      return;
    }
    enroll();
  }

  return (
    <Screen>
      <NavBar
        title={ready?.list.name ?? 'List'}
        leading={
          <IconButton
            sf="chevron.left"
            feather="chevron-left"
            accessibilityLabel="Back"
            onPress={() => {
              // canGoBack() guard per session-screen.tsx/enroll() above — this screen can be
              // reached with no back history in its stack (e.g. a deep link straight to a list),
              // in which case back() throws "GO_BACK was not handled by any navigator". Fall back
              // to the catalog, the natural parent of this screen.
              if (router.canGoBack()) {
                router.back();
                return;
              }
              router.push('/lists');
            }}
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
      {ready && (
        <>
          <View style={styles.headerRow}>
            <ThemedText type="footnote" color="textSecondary">
              {ready.list.totalWords} words
            </ThemedText>
            {status === 'active' && <Tag tone="accent">In progress</Tag>}
            {status === 'completed' && <Tag tone="success">Completed</Tag>}
          </View>

          <ScrollView style={styles.content} contentContainerStyle={styles.contentInner}>
            <Card padding={0} style={styles.wordsCard}>
              {ready.words.map((word, i) => {
                const learned = (progressByLexeme.get(word.lexemeId)?.correct ?? 0) > 0;
                return (
                  <ListRow
                    key={word.lexemeId}
                    title={word.word}
                    subtitle={word.translation ?? undefined}
                    showDivider={i < ready.words.length - 1}
                    trailing={learned ? <Feather name="check" size={16} color={theme.success} /> : null}
                  />
                );
              })}
            </Card>
          </ScrollView>

          <View style={styles.footer}>
            <Button
              variant="primary"
              style={styles.ctaButton}
              disabled={enrolling}
              onPress={handleCtaPress}>
              {enrolling ? 'Enrolling…' : cta}
            </Button>
          </View>

          <ConfirmDialog
            visible={confirmingSwitch}
            title="Switch lists?"
            message={`You're learning "${activeEnrollment?.name}". Starting this list deletes that progress permanently.`}
            confirmLabel="Switch anyway"
            onConfirm={enroll}
            onCancel={() => setConfirmingSwitch(false)}
          />
        </>
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
  headerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: Space[4],
  },
  content: {
    flex: 1,
  },
  contentInner: {
    paddingVertical: Space[4],
  },
  wordsCard: {
    paddingHorizontal: Space[4],
  },
  footer: {
    paddingTop: Space[5],
    // Kept in sync with exercise-view's/intro-card-view's Next button: same distance above the
    // safe area for every screen's bottom-pinned primary action. (The old extra +50 compensated
    // for the web tab bar overlapping this sheet — no longer needed now that app-tabs.web.tsx
    // hides the tab bar on /lists/* routes.)
    paddingBottom: Space[5],
  },
  ctaButton: {
    width: '100%',
  },
});
