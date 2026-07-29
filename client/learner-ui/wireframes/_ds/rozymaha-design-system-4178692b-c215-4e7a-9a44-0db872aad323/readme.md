# Rozymaha Design System

Rozymaha is a vocabulary-learning app: flashcards and spaced-technique review to help a learner reach the 3,000–5,000 word core vocabulary needed for basic fluency in a new language. The core feature is user-created goals and vocab lists, generated or refined with built-in AI tools, then studied with flashcards.

Target platform is native iOS first (SwiftUI or React Native — undecided). Design language is native-iOS: system type sizes, hairline separators, flat surfaces, iOS-standard controls (segmented control, switches, tab bar, nav bar).

**Sources.** This is a from-scratch design system — no codebase, Figma file, or existing brand assets were provided. Everything here (palette, type scale, components, screens) is originated for this project based on the brief below. There is **no logo** — a plain wordmark stands in until one exists.

**Brief given by the founder:**
- Vocabulary learner for other languages; flashcards + other simple techniques.
- Users create their own goals and vocab lists, with built-in AI tools to generate/expand lists.
- Users can reuse/browse existing lists to build vocabulary.
- Goal: reach the 3,000–5,000 words needed for basic communication.
- Design should be simple, use standard (system) fonts, and take a **scientific approach to learning — not gamification**.
- Open at brief time: exact view/flow set and color palette (resolved below).

## Content fundamentals

- **Voice:** calm, direct, second-person. "You" not "the user" — e.g. *"12 words due for review"*, *"Start today's review"*.
- **No hype, no gamified language.** Never "streak on fire 🔥" or "you're crushing it" — say what happened: *"86% retention"*, *"3,482 words known"*.
- **Sentence case** for buttons and headings ("Start today's review", not "Start Today's Review" or "START REVIEW").
- **No emoji.** Progress is communicated with numbers and plain charts, never badges, trophies, or celebratory icons.
- **Precise over cute.** Prefer a real number or unit ("21 day streak", "68% known") over a vague adjective.
- **Word entries read like a dictionary**, not marketing copy: headword, part of speech, one example sentence. E.g. *manzana — apple · noun · fem. — "La manzana está en la mesa."*

## Visual foundations

- **Palette:** warm paper background (`--color-paper` #FAF7F2) with a saturated terracotta primary (`--color-terracotta-600` #D9541F) for CTAs and active states — chosen to be eye-catching without resorting to gamified brights, purples, or gradients. Forest green (`--color-forest-600`) marks "known/correct," brick red (`--color-brick-600`) marks "again/wrong," gold (`--color-gold-600`) is a rare highlight accent. See the Colors cards.
- **Type:** system font stack only (`-apple-system`/SF Pro on iOS) for all UI text — no custom webfonts, per the "standard fonts" brief. The studied foreign word itself is set in the system serif (New York on iOS) at 40px to read like a dictionary headword, visually distinct from UI chrome. Numeric stats use the system monospace for tabular alignment.
- **Spacing:** 4px base grid (4/8/12/16/20/24/32/40/48/64).
- **Radius:** iOS continuous-corner scale — 8 / 12 / 16 / 20px, pill for switches/segmented controls/tags.
- **Surfaces:** flat by default. Cards are a hairline border (`--border-hairline`) plus a nearly-imperceptible shadow (`--shadow-card`) — not a drop shadow look. Real elevation shadow (`--shadow-sheet`) is reserved for sheets/modals only.
- **Backgrounds:** no photography, no illustration, no textures/patterns, no gradients. Flat color fields only.
- **Motion:** standard iOS easing (`cubic-bezier(0.4,0,0.2,1)`), 120–320ms. No bounce/spring, no looping decorative animation — consistent with the "scientific, not gamified" direction.
- **Hover/press:** buttons and icon buttons dim to 75% opacity on press; no color inversion, no scale/bounce.
- **Borders:** 1px hairlines (`--border-hairline` on white surfaces, `--border-hairline-on-paper` on the paper background) separate nav bars, tab bars, and list rows — the dominant separator technique, used far more than shadows.
- **Charts:** a single thin line (`--chart-line`, forest green) with a soft fill (`--chart-fill`, sage) — plain retention/progress curves, not bar-chart infographics.
- **Progress display:** plain numbers and one line chart. No streak flames, no badges, no levels/XP, no confetti.
- **Transparency/blur:** none currently — flat surfaces throughout, no frosted-glass panels.
- **Dark mode:** not designed yet (light mode only per brief); tokens are structured so a dark theme scope could be added later.

## Iconography

No icon set was provided. **Substitution flagged:** the UI kit and component cards use [Lucide](https://lucide.dev) (`lucide-static`, served from unpkg CDN) — a regular-weight outline style close to SF Symbols' default weight, used instead of drawing custom icons. If Rozymaha ships in SwiftUI, SF Symbols should replace Lucide 1:1 for the same glyphs (house, list, layers, settings, plus, x, check, chevron-left/right, sparkles). No emoji, no unicode glyphs are used as icons.

## Components

Standard set authored from scratch (no source library existed to enumerate against):

- **Core** (`components/core/`): `Button`, `IconButton`, `Card`, `Tag`, `ListRow`, `Switch`
- **Forms** (`components/forms/`): `TextField`
- **Navigation** (`components/navigation/`): `NavBar`, `TabBar`, `SegmentedControl`
- **Data** (`components/data/`): `ProgressStat`

## UI kit

`ui_kits/ios_app/` — a click-through iOS app recreation: Home (progress + retention chart + goal bar), Vocab lists (browse + AI-assisted "New list" sheet), Flashcard review (reveal → grade Again/Good/Easy), Settings. Open `ui_kits/ios_app/index.html`.

## Index

- `styles.css` — root stylesheet, imports everything below.
- `tokens/colors.css`, `tokens/typography.css`, `tokens/spacing.css`, `tokens/base.css` — design tokens.
- `guidelines/` — foundation specimen cards (Colors, Type, Spacing, Brand) shown in the Design System tab.
- `components/core/`, `components/forms/`, `components/navigation/`, `components/data/` — reusable primitives, each with `.jsx` + `.d.ts` + `.prompt.md` + a `*.card.html` demo.
- `ui_kits/ios_app/` — the click-through app recreation (`index.html` + screen files).
- `thumbnail.html` — project tile for the homepage.
- `SKILL.md` — portable skill file for use in Claude Code.

## Caveats

- No logo/brand mark exists — wordmark-only until one is supplied.
- No font files were needed/uploaded — this system intentionally uses OS system fonts (SF Pro / New York), not custom webfonts.
- Icons are Lucide (CDN) standing in for SF Symbols — swap if/when the app is built in SwiftUI.
- Color palette, exact copy voice, and screen flows are all originated here, not sourced from an existing brand — treat as a first draft to react to.
