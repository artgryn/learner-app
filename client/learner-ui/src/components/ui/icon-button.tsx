import { Feather } from '@expo/vector-icons';
import { SymbolView, type SymbolViewProps } from 'expo-symbols';
import { Pressable, StyleSheet } from 'react-native';

import { Radius, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type IconButtonProps = {
  /** SF Symbol name, used on iOS. */
  sf: SymbolViewProps['name'];
  /** Feather icon name, used as the Android/web fallback (expo-symbols is iOS-only). */
  feather: keyof typeof Feather.glyphMap;
  onPress?: () => void;
  accessibilityLabel: string;
};

export function IconButton({ sf, feather, onPress, accessibilityLabel }: IconButtonProps) {
  const theme = useTheme();

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel}
      style={({ pressed }) => [styles.base, pressed && styles.pressed]}>
      <SymbolView
        name={sf}
        size={20}
        tintColor={theme.text}
        fallback={<Feather name={feather} size={20} color={theme.text} />}
      />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    width: Space[10],
    height: Space[10],
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
  pressed: {
    opacity: 0.75,
  },
});
