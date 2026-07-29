import { StyleSheet } from 'react-native';

import { NavBar } from '@/components/nav-bar';
import { Screen } from '@/components/screen';
import { ThemedText } from '@/components/themed-text';
import { Space } from '@/constants/theme';

export default function SettingsScreen() {
  return (
    <Screen>
      <NavBar title="Settings" />
      <ThemedText type="footnote" color="textSecondary" style={styles.body}>
        Account and preferences are coming soon.
      </ThemedText>
    </Screen>
  );
}

const styles = StyleSheet.create({
  body: {
    marginTop: Space[4],
  },
});
