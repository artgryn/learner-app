/**
 * Rozymaha design tokens — warm paper background, terracotta primary accent,
 * native-iOS type scale and system fonts. Dark mode is not designed yet
 * (light mode only per brief), so `dark` currently mirrors `light`.
 */

import '@/global.css';

import { Platform } from 'react-native';

const palette = {
  paper: '#FAF7F2',
  surface: '#FFFFFF',
  surfaceSunk: '#F1ECE2',

  ink900: '#2B2620',
  ink700: '#4A4339',
  ink500: '#6B6258',
  ink300: '#9C9285',
  ink150: '#D8D1C4',
  ink100: '#E7E1D5',

  terracotta700: '#A83E14',
  terracotta600: '#D9541F',
  terracotta500: '#E2703F',
  terracotta100: '#FBE3D6',

  forest700: '#274A38',
  forest600: '#3F6B52',
  forest100: '#E1EAE1',

  gold600: '#C9A227',
  gold100: '#F5EBC9',

  brick700: '#7E271E',
  brick600: '#B23A2E',
  brick100: '#F5DEDA',

  sand300: '#E7D9BE',
  sage200: '#C9D9C8',
} as const;

const lightColors = {
  background: palette.paper,
  surface: palette.surface,
  surfaceSunk: palette.surfaceSunk,

  text: palette.ink900,
  textSecondary: palette.ink500,
  textPlaceholder: palette.ink300,
  textOnAccent: '#FFFFFF',

  borderHairline: palette.ink100,
  borderHairlineOnPaper: palette.ink150,

  accentPrimary: palette.terracotta600,
  accentPrimaryPress: palette.terracotta700,
  accentPrimaryTint: palette.terracotta100,

  success: palette.forest600,
  successTint: palette.forest100,
  error: palette.brick600,
  errorTint: palette.brick100,
  highlight: palette.gold600,
  highlightTint: palette.gold100,

  tagFill: palette.sand300,
  chartLine: palette.forest600,
  chartFill: palette.sage200,
} as const;

export const Colors = { light: lightColors, dark: lightColors } as const;

export type ThemeColor = keyof typeof Colors.light;

export const Fonts = Platform.select({
  ios: {
    ui: '-apple-system',
    word: 'ui-serif',
    mono: 'ui-monospace',
  },
  default: {
    ui: 'system-ui',
    word: 'serif',
    mono: 'monospace',
  },
  web: {
    ui: '-apple-system, system-ui, "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
    word: 'ui-serif, Georgia, "Times New Roman", serif',
    mono: 'ui-monospace, "SF Mono", Menlo, Consolas, monospace',
  },
})!;

/** iOS Human Interface Guidelines type scale, plus a display size for the studied word. */
export const TextSize = {
  caption2: 11,
  caption1: 12,
  footnote: 13,
  subhead: 15,
  callout: 16,
  body: 17,
  headline: 17,
  title3: 20,
  title2: 22,
  title1: 28,
  largeTitle: 34,
  wordDisplay: 40,
} as const;

export const Leading = {
  tight: 1.15,
  normal: 1.35,
  loose: 1.5,
} as const;

export const Weight = {
  regular: '400',
  medium: '500',
  semibold: '600',
  bold: '700',
} as const;

/** 4px base grid. */
export const Space = {
  1: 4,
  2: 8,
  3: 12,
  4: 16,
  5: 20,
  6: 24,
  8: 32,
  10: 40,
  12: 48,
  16: 64,
} as const;

/** iOS-style continuous corner radii. */
export const Radius = {
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  pill: 999,
} as const;

/** Flat by default — real elevation is reserved for sheets/modals. */
export const Shadow = {
  card: {
    shadowColor: palette.ink900,
    shadowOpacity: 0.06,
    shadowRadius: 2,
    shadowOffset: { width: 0, height: 1 },
    elevation: 1,
  },
  sheet: {
    shadowColor: palette.ink900,
    shadowOpacity: 0.14,
    shadowRadius: 24,
    shadowOffset: { width: 0, height: 8 },
    elevation: 8,
  },
} as const;

export const BottomTabInset = Platform.select({ ios: 50, android: 80 }) ?? 0;
export const MaxContentWidth = 800;
