/**
 * Types mirroring doc/api/swagger.yaml (server/learner/doc/api/swagger.yaml).
 * Field names match the spec (camelCase) so payloads can be used as-is.
 */

// ---- auth ----
export type RegisterRequest = {
  email: string;
  password: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type RefreshRequest = {
  refreshToken: string;
};

export type TokenPair = {
  accessToken: string;
  refreshToken: string;
  expiresIn?: number;
};

export type ResetCodeRequest = {
  email: string;
};

export type ResetConfirmRequest = {
  email: string;
  /** Supply exactly one of code (from reset/request) or token (from reset/link). */
  code?: string;
  token?: string;
  newPassword: string;
};

export type ResetLinkRequest = {
  email: string;
};

// ---- account ----
/** Closed set of supported language codes. */
export type LanguageCode = 'sv' | 'en' | 'ru' | 'uk';

export type AccountStatus = 'free' | 'paid';

export type AccountView = {
  id: number;
  email: string;
  name?: string | null;
  uiLang?: LanguageCode | null;
  learnBaseLang?: LanguageCode | null;
  learnTargetLang?: LanguageCode | null;
  status: AccountStatus;
};

/** PATCH /me body — partial; omit a field to leave it unchanged. email/status aren't settable here. */
export type AccountUpdate = {
  name?: string;
  uiLang?: LanguageCode;
  learnBaseLang?: LanguageCode;
  learnTargetLang?: LanguageCode;
};

// ---- catalog ----
export type Language = {
  code: LanguageCode;
  name: string;
};

export type LanguagePair = {
  base: Language;
  target: Language;
};

/** GET /public/init response — unauthenticated bootstrap payload. */
export type PublicInitResponse = {
  languagePairs: LanguagePair[];
};

export type ExerciseType =
  | 'en_ett'
  | 'assemble'
  | 'translate'
  | 'base_form'
  | 'produce_form'
  | 'multi_select';

export type ListSummary = {
  listId: number;
  name: string;
  targetLang: string;
  totalWords: number;
};

export type ListDetail = ListSummary & {
  /** Null = all exercise types permitted. */
  allowedExercises: ExerciseType[] | null;
};

/** Catalog data only — no per-user mastery; cross-reference WordProgress (by lexemeId) for that. */
export type ListWord = {
  lexemeId: number;
  /** Citation form, ready to render (e.g. "ett hus"). */
  word: string;
  pos: string;
  gender?: string | null;
  /** Null if no translation exists for the requested base language. */
  translation?: string | null;
};

// ---- enrollment / progress ----
export type EnrollmentStatus = 'active' | 'completed';

export type Enrollment = {
  listId: number;
  name: string;
  baseLang: string;
  targetLang: string;
  status: EnrollmentStatus;
  wordsMastered?: number;
  totalWords?: number;
  sessions?: {
    done?: number;
    estimatedTotal?: number;
  };
  lastActiveAt?: string;
};

export type WordProgress = {
  lexemeId: number;
  timesPracticed: number;
  correct?: number;
  wrong?: number;
  due?: string | null;
};

// ---- session: introduce card ----
export type WordFormEntry = {
  formType: string;
  form: string;
};

export type IntroduceCard = {
  word: string;
  lemma: string;
  pos: string;
  /** en/ett for Swedish nouns. */
  gender?: string | null;
  translation: string;
  forms: WordFormEntry[];
};

export type IntroduceItem = {
  itemId: string;
  itemType: 'introduce';
  lexemeId: number;
  card: IntroduceCard;
};

// ---- session: exercise item ----
export type Prompt = {
  text: string;
  lang: string;
};

export type EnEttPayload = {
  options: string[];
  correctAnswer: string;
};

export type TranslatePayload = {
  /** Language of the options, when it differs from the prompt. */
  optionsLang?: string;
  options: string[];
  correctAnswer: string;
};

export type AssemblePayload = {
  /** Pre-shuffled, longer than the answer, may contain duplicates. Positional, not a set. */
  letters: string[];
  answerLength: number;
  correctAnswer: string;
};

/** Single-answer choice payload (base_form, produce_form). */
export type OptionsPayload = {
  options: string[];
  correctAnswer: string;
};

export type MultiSelectPayload = {
  options: string[];
  correctAnswers: string[];
};

export type ExercisePayload =
  | EnEttPayload
  | TranslatePayload
  | AssemblePayload
  | OptionsPayload
  | MultiSelectPayload;

export type ExerciseItem = {
  itemId: string;
  itemType: 'exercise';
  exerciseType: ExerciseType;
  lexemeId: number;
  /** Which form is tested; null for whole-word exercises. */
  formType?: string | null;
  prompt: Prompt;
  exercise: ExercisePayload;
};

/** Discriminated by itemType: a no-action teaching card, or a graded task. */
export type SessionItem = IntroduceItem | ExerciseItem;

export type SessionResponse = {
  sessionId: string;
  listId: number;
  items: SessionItem[];
};

// ---- result submission ----
export type Result = {
  itemId: string;
  lexemeId: number;
  exerciseType: ExerciseType;
  formType?: string | null;
  isCorrect: boolean;
  elapsedMs?: number;
};

export type ResultsBatch = {
  results: Result[];
};

export type CompleteResponse = {
  sessionId?: string;
  progress?: WordProgress[];
};

// ---- home / stats ----
export type HomeResponse = {
  user?: AccountView;
  enrollments?: Enrollment[];
  resume?: { listId: number } | null;
};

export type Stats = {
  /** Distinct lexemes mastered across all lists. */
  wordsKnown?: number;
  streakDays?: number;
};

// ---- error ----
export type ErrorResponse = {
  error: {
    status: number;
    code: string;
    message: string;
    traceId?: string;
  };
};
