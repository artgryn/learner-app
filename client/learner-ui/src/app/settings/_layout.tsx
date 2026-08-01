import { Stack } from 'expo-router';

// Own stack so Account settings can push into Stats while staying inside the Account tab
// (the tab bar stays visible — unlike lists/session, Stats doesn't need to hide it).
export default function SettingsLayout() {
  return <Stack screenOptions={{ headerShown: false }} />;
}
