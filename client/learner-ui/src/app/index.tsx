import { useRouter } from 'expo-router';
import { StyleSheet, View } from 'react-native';

import { Button } from '@/components/button';
import { Card } from '@/components/card';
import { IconButton } from '@/components/icon-button';
import { ListRow } from '@/components/list-row';
import { NavBar } from '@/components/nav-bar';
import { Screen } from '@/components/screen';
import { ThemedText } from '@/components/themed-text';
import { Radius, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

// Mock data until /me/home is wired up — server session generation is still stubbed.
const sessions = [
  { number: 1, topic: 'Everyday basics' },
  { number: 2, topic: 'Everyday basics' },
  { number: 3, topic: 'Travel & transport' },
];
const currentSession = 1;

export default function HomeScreen() {
  const theme = useTheme();
  const router = useRouter();

  return (
    <Screen>
      <NavBar
        title="Home"
        trailing={
          <IconButton
            sf="gearshape"
            feather="settings"
            accessibilityLabel="Settings"
            onPress={() => router.push('/settings')}
          />
        }
      />
      <View style={styles.content}>
        <Card>
          <ThemedText type="title2">{sessions.length} sessions ready</ThemedText>
          <ThemedText type="footnote" color="textSecondary" style={styles.heroSubtitle}>
            Spaced across the day for better retention
          </ThemedText>
          <View style={styles.dots}>
            {sessions.map((session) => (
              <View
                key={session.number}
                style={[
                  styles.dot,
                  {
                    backgroundColor:
                      session.number <= currentSession ? theme.accentPrimary : theme.surfaceSunk,
                  },
                ]}
              />
            ))}
          </View>
          <Button style={styles.startButton}>Start · session {currentSession}</Button>
        </Card>

        <Card padding={0} style={styles.listCard}>
          {sessions.map((session, i) => (
            <ListRow
              key={session.number}
              title={`${session.number} · ${session.topic}`}
              showDivider={i < sessions.length - 1}
              trailing={
                <View
                  style={[
                    styles.badge,
                    {
                      backgroundColor:
                        session.number < currentSession ? theme.accentPrimary : theme.surfaceSunk,
                    },
                  ]}>
                  <ThemedText
                    type="caption1"
                    color={session.number <= currentSession ? 'textOnAccent' : 'textPlaceholder'}>
                    {session.number}
                  </ThemedText>
                </View>
              }
            />
          ))}
        </Card>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    gap: Space[4],
    paddingTop: Space[4],
  },
  heroSubtitle: {
    marginTop: 4,
  },
  dots: {
    flexDirection: 'row',
    gap: Space[2],
    marginTop: Space[4],
  },
  dot: {
    flex: 1,
    height: 4,
    borderRadius: Radius.pill,
  },
  startButton: {
    marginTop: Space[4],
  },
  listCard: {
    paddingHorizontal: Space[4],
  },
  badge: {
    width: 22,
    height: 22,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
