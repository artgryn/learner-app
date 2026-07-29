import type { ReactNode } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type ListRowProps = {
  title: string;
  subtitle?: string;
  trailing?: ReactNode;
  onPress?: () => void;
  /** Hide the bottom hairline — set false on the last row in a Card. */
  showDivider?: boolean;
};

export function ListRow({ title, subtitle, trailing, onPress, showDivider = true }: ListRowProps) {
  const theme = useTheme();
  const divider = showDivider && {
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: theme.borderHairline,
  };

  const content = (
    <>
      <View style={styles.text}>
        <ThemedText type="body">{title}</ThemedText>
        {subtitle ? (
          <ThemedText type="footnote" color="textSecondary">
            {subtitle}
          </ThemedText>
        ) : null}
      </View>
      {trailing}
    </>
  );

  if (onPress) {
    return (
      <Pressable
        onPress={onPress}
        style={({ pressed }) => [styles.row, divider, pressed && styles.pressed]}>
        {content}
      </Pressable>
    );
  }

  return <View style={[styles.row, divider]}>{content}</View>;
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: Space[3],
    paddingVertical: Space[3],
  },
  text: {
    flex: 1,
    gap: 2,
  },
  pressed: {
    opacity: 0.75,
  },
});
