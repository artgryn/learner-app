import { StyleSheet, Text, type TextProps } from 'react-native';

import { Fonts, Leading, TextSize, ThemeColor, Weight } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type ThemedTextProps = TextProps & {
  type?:
    | 'largeTitle'
    | 'title1'
    | 'title2'
    | 'title3'
    | 'headline'
    | 'body'
    | 'callout'
    | 'subhead'
    | 'footnote'
    | 'caption1'
    | 'caption2'
    | 'word';
  color?: ThemeColor;
};

export function ThemedText({ style, type = 'body', color, ...rest }: ThemedTextProps) {
  const theme = useTheme();

  return (
    <Text style={[{ color: theme[color ?? 'text'] }, styles[type], style]} {...rest} />
  );
}

const styles = StyleSheet.create({
  largeTitle: {
    fontSize: TextSize.largeTitle,
    lineHeight: TextSize.largeTitle * Leading.tight,
    fontWeight: Weight.bold,
  },
  title1: {
    fontSize: TextSize.title1,
    lineHeight: TextSize.title1 * Leading.tight,
    fontWeight: Weight.bold,
  },
  title2: {
    fontSize: TextSize.title2,
    lineHeight: TextSize.title2 * Leading.tight,
    fontWeight: Weight.bold,
  },
  title3: {
    fontSize: TextSize.title3,
    lineHeight: TextSize.title3 * Leading.normal,
    fontWeight: Weight.semibold,
  },
  headline: {
    fontSize: TextSize.headline,
    lineHeight: TextSize.headline * Leading.normal,
    fontWeight: Weight.semibold,
  },
  body: {
    fontSize: TextSize.body,
    lineHeight: TextSize.body * Leading.normal,
    fontWeight: Weight.regular,
  },
  callout: {
    fontSize: TextSize.callout,
    lineHeight: TextSize.callout * Leading.normal,
    fontWeight: Weight.regular,
  },
  subhead: {
    fontSize: TextSize.subhead,
    lineHeight: TextSize.subhead * Leading.normal,
    fontWeight: Weight.regular,
  },
  footnote: {
    fontSize: TextSize.footnote,
    lineHeight: TextSize.footnote * Leading.normal,
    fontWeight: Weight.regular,
  },
  caption1: {
    fontSize: TextSize.caption1,
    lineHeight: TextSize.caption1 * Leading.normal,
    fontWeight: Weight.regular,
  },
  caption2: {
    fontSize: TextSize.caption2,
    lineHeight: TextSize.caption2 * Leading.normal,
    fontWeight: Weight.regular,
  },
  /** The studied foreign word — set in the system serif to read like a dictionary headword. */
  word: {
    fontFamily: Fonts.word,
    fontSize: TextSize.wordDisplay,
    lineHeight: TextSize.wordDisplay * Leading.tight,
    fontWeight: Weight.semibold,
  },
});
