import type { ReactNode } from 'react';
import { StyleSheet, View } from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { Space } from '@/constants/theme';

type NavBarProps = {
  title: string;
  trailing?: ReactNode;
};

export function NavBar({ title, trailing }: NavBarProps) {
  return (
    <View style={styles.row}>
      <ThemedText type="headline">{title}</ThemedText>
      {trailing}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: Space[3],
  },
});
