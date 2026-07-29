import { useEffect } from 'react';
import { ActivityIndicator, StyleSheet } from 'react-native';
import * as SplashScreen from 'expo-splash-screen';

import { Screen } from '@/components/screen';
import { ThemedText } from '@/components/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type LoadingScreenProps = {
  onReady: () => void;
};

/** Shown while the app boots. Hands off to the native splash and clears once ready. */
export function LoadingScreen({ onReady }: LoadingScreenProps) {
  const theme = useTheme();

  useEffect(() => {
    SplashScreen.hideAsync().finally(onReady);
  }, [onReady]);

  return (
    <Screen style={styles.center}>
      <ThemedText type="word">Rozymaha</ThemedText>
      <ActivityIndicator color={theme.accentPrimary} style={styles.indicator} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  indicator: {
    marginTop: Space[5],
  },
});
