import { StyleSheet, TextInput, View, type TextInputProps } from 'react-native';

import { ThemedText } from '@/components/ui/themed-text';
import { Radius, Space } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type TextFieldProps = Pick<
  TextInputProps,
  'placeholder' | 'secureTextEntry' | 'autoCapitalize' | 'keyboardType' | 'multiline'
> & {
  label?: string;
  value: string;
  onChangeText: (text: string) => void;
};

export function TextField({ label, value, onChangeText, ...inputProps }: TextFieldProps) {
  const theme = useTheme();

  return (
    <View style={styles.field}>
      {label ? (
        <ThemedText type="footnote" color="textSecondary">
          {label}
        </ThemedText>
      ) : null}
      <TextInput
        value={value}
        onChangeText={onChangeText}
        placeholderTextColor={theme.textPlaceholder}
        style={[
          styles.input,
          { backgroundColor: theme.surfaceSunk, color: theme.text, borderColor: theme.borderHairline },
        ]}
        {...inputProps}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  field: {
    gap: Space[1],
  },
  input: {
    borderRadius: Radius.md,
    borderWidth: StyleSheet.hairlineWidth,
    paddingVertical: Space[3],
    paddingHorizontal: Space[4],
    fontSize: 17,
  },
});
