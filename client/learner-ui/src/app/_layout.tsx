import { DarkTheme, DefaultTheme, ThemeProvider } from '@react-navigation/native';
import * as SplashScreen from 'expo-splash-screen';
import { useState } from 'react';
import { useColorScheme } from 'react-native';

import AppTabs from '@/components/navigation/app-tabs';
import { LoadingScreen } from '@/components/ui/loading-screen';
import { AuthScreen } from '@/screens/auth-screen';
import { InitAccountScreen } from '@/screens/init-account-screen';

SplashScreen.preventAutoHideAsync();

// Auth/init-account are fully wired to the real API (see doc/app-info/UI/login-register-reset.md,
// init account.md) but aren't part of the dev loop yet — flip this to false to actually exercise
// the boot -> auth -> init-account chain instead of skipping straight to the mocked logged-in app.
const MOCK_LOGGED_IN = true;

type Phase = 'auth' | 'initAccount' | 'app';

export default function RootLayout() {
  const colorScheme = useColorScheme();
  const [isReady, setIsReady] = useState(false);
  const [phase, setPhase] = useState<Phase>(MOCK_LOGGED_IN ? 'app' : 'auth');

  let content;
  if (!isReady) {
    content = <LoadingScreen onReady={() => setIsReady(true)} />;
  } else if (phase === 'auth') {
    content = (
      <AuthScreen
        onAuthenticated={(isNewAccount) => setPhase(isNewAccount ? 'initAccount' : 'app')}
      />
    );
  } else if (phase === 'initAccount') {
    content = <InitAccountScreen onDone={() => setPhase('app')} />;
  } else {
    content = <AppTabs />;
  }

  return (
    <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
      {content}
    </ThemeProvider>
  );
}
