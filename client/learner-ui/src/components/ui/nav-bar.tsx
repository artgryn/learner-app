import type { ReactNode } from 'react';
import { StyleSheet, View } from 'react-native';

import { ThemedText } from '@/components/ui/themed-text';
import { Space } from '@/constants/theme';

type NavBarProps = {
  title: string;
  leading?: ReactNode;
  trailing?: ReactNode;
};

export function NavBar({ title, leading, trailing }: NavBarProps) {
  return (
    <View style={styles.row}>
      {leading}
      <ThemedText type="headline" style={styles.title}>
        {title}
      </ThemedText>
      {trailing}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Space[2],
    paddingVertical: Space[3],
  },
  title: {
    flex: 1,
  },
});
