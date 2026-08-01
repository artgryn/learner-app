import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';

import { ApiError, getEnrollments, getLists } from '@/api';
import type { Enrollment, ListSummary } from '@/api';
import { Card } from '@/components/ui/card';
import { ListRow } from '@/components/ui/list-row';
import { Screen } from '@/components/ui/screen';
import { Tag } from '@/components/ui/tag';
import { ThemedText } from '@/components/ui/themed-text';
import { Radius, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

// MVP scope: Swedish learned from English is the only supported pair.
const TARGET_LANG = 'sv';
const BASE_LANG = 'en';

type LoadState =
  | { status: 'loading' }
  | { status: 'error'; message: string }
  | { status: 'ready'; lists: ListSummary[]; enrollments: Enrollment[] };

/** Active list first, per doc/app-info/UI/list catalog.md — otherwise keeps the server's order. */
function sortByActiveFirst(lists: ListSummary[], activeListId: number | undefined): ListSummary[] {
  if (!activeListId) return lists;
  return [...lists].sort((a, b) => Number(b.listId === activeListId) - Number(a.listId === activeListId));
}

function statusTag(enrollment: Enrollment | undefined) {
  if (enrollment?.status === 'active') return <Tag tone="success">Learning</Tag>;
  if (enrollment?.status === 'completed') return <Tag tone="highlight">Completed</Tag>;
  return null;
}

export function ListCatalogScreen() {
  const theme = useTheme();
  const router = useRouter();
  const [state, setState] = useState<LoadState>({ status: 'loading' });

  useEffect(() => {
    Promise.all([getLists(TARGET_LANG, BASE_LANG), getEnrollments()])
      .then(([lists, enrollments]) => setState({ status: 'ready', lists, enrollments }))
      .catch((error) =>
        setState({
          status: 'error',
          message: error instanceof ApiError ? error.message : 'Could not load lists.',
        })
      );
  }, []);

  const activeListId =
    state.status === 'ready' ? state.enrollments.find((e) => e.status === 'active')?.listId : undefined;

  return (
    <Screen>
      <View style={styles.content}>
        <ThemedText type="title2">Lists</ThemedText>
        <ThemedText type="footnote" color="textSecondary">
          English → Swedish
        </ThemedText>

        {state.status === 'loading' && (
          <ActivityIndicator color={theme.accentPrimary} style={styles.indicator} />
        )}
        {state.status === 'error' && (
          <ThemedText type="footnote" color="textSecondary">
            {state.message}
          </ThemedText>
        )}
        {state.status === 'ready' && state.lists.length === 0 && (
          <ThemedText type="footnote" color="textSecondary">
            No lists yet for this language pair.
          </ThemedText>
        )}
        {state.status === 'ready' && state.lists.length > 0 && (
          <Card padding={0} style={styles.listCard}>
            {sortByActiveFirst(state.lists, activeListId).map((list, i, sorted) => {
              const enrollment = state.enrollments.find((e) => e.listId === list.listId);
              return (
                <ListRow
                  key={list.listId}
                  title={list.name}
                  subtitle={`${list.totalWords} words`}
                  onPress={() => router.push(`/lists/${list.listId}`)}
                  showDivider={i < sorted.length - 1}
                  trailing={statusTag(enrollment)}
                  style={
                    enrollment?.status === 'active'
                      ? {
                          backgroundColor: theme.accentPrimaryTint,
                          borderRadius: Radius.lg,
                          // The card insets every row by Space[4] on each side (`listCard` below) —
                          // cancel that out so the highlight reaches the card's true edges instead
                          // of floating short of them, then restore the same inset as padding so
                          // the text lines up with the other rows.
                          marginHorizontal: -Space[4],
                          paddingHorizontal: Space[4],
                        }
                      : undefined
                  }
                />
              );
            })}
          </Card>
        )}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    gap: Space[4],
    paddingTop: Space[6],
  },
  listCard: {
    paddingHorizontal: Space[4],
  },
  indicator: {
    marginTop: Space[8],
  },
});
