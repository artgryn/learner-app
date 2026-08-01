**Purpose:** show account details: name, email, UI lang(now preselected "English"). Also option to edit account name, reset password, delete, change language pair.


**Components:** button, edit text, app logo, bottom navigation bar [[shared-components]].
**Data:** `GET /me` → account view (name, email, uiLang, learnBaseLang, learnTargetLang, status - email and status are read-only here). `PATCH /me` → update name / uiLang / learnBaseLang / learnTargetLang (partial; omit a field to leave it unchanged). Changing the language pair reuses the same picker source as [[init account]] (`GET /language-pairs`) and rejects an unlisted pair with `UNSUPPORTED_PAIR`. "Reset password" and "Delete account" are their own confirmations, not inline saves.

**Navigation**:
	- at the bottom menu with home, list catalog, account [[shared-components]]. Can navigate to: [[home]], [[list catalog]], [[account settings]](active)
##### API
`GET /me` → `AccountView`.
`PATCH /me` → `AccountView` (name / uiLang / learnBaseLang / learnTargetLang only; email and status are not settable here).
`DELETE /me` → 204 (deletes the account and cascades ALL user data - enrollments, progress, sessions, attempts. Needs its own confirmation dialog, this is irreversible).
Language-pair picker source: `GET /language-pairs` (secured; same set as the pre-login `GET /public/init` used by [[init account]]).
"Reset password" from here hands off to the reset flow - see [[login-register-reset]] (`POST /auth/reset/request` or `/link`, then `/confirm`).