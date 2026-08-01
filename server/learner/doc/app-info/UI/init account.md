**Purpose:** Select learning lang pair. UI lang for now only english. Server must give available language  combos as "Your lang: English -> Learning: Swedish". This is predefined in the server.

**Components:** user name text field, selector - probably picker like a time picker style - [[shared-components]].  

**Data:** Picker source: `GET /public/init` (unauthenticated, safe to call before/without a session) → `languagePairs[]`, each `{base:{code,name}, target:{code,name}}`. Submit: `PATCH /me` with `{name, learnBaseLang, learnTargetLang}` → updated `AccountView`. A pair not in the picker's list is rejected with `UNSUPPORTED_PAIR` (400) - shouldn't happen if the picker is only populated from that same endpoint.

**States:** `idle · submitting · error(code)`. On success → [[Home]].

**TODO:** UI design only. Server side is done: `account` has `learn_base_lang`/`learn_target_lang`/`name`/`ui_lang`/`status` columns, and full account CRUD exists (`POST /auth/register` creates the bare account; this screen's actual save is `PATCH /me`, not register).
##### API
`GET /public/init` → `languagePairs[]` (the picker source, resolved server-side from `app.language-pairs` config).
`PATCH /me` → `AccountView` (init_account's save step - `POST /auth/register`, called by [[login-register-reset]] just before this screen, only creates the account + tokens; it does not carry name/pair).