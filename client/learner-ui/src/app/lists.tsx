import { StyleSheet } from 'react-native';

import { NavBar } from '@/components/nav-bar';
import { Screen } from '@/components/screen';
import { ThemedText } from '@/components/themed-text';
import { Space } from '@/constants/theme';

export default function ListsScreen() {
  return (
    <Screen>
      <NavBar title="Lists" />
      <ThemedText type="footnote" color="textSecondary" style={styles.body}>
        Browsing and creating vocabulary lists is coming soon.
      </ThemedText>
    </Screen>
  );
}

const styles = StyleSheet.create({
  body: {
    marginTop: Space[4],
  },
});
