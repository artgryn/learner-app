**Purpose:** the launch screen — show enrolment(only one active list per time), progress, and where to resume; entry point to everything, main part of the screen is the block with current learning list, its progress, resume(New session), ability to navigate to list details and see all words.  [[shared-components]]

**Components:**
- header (greeting/streak), logo, [[shared-components]]
- **Continue card** (the resume pointer — biggest tap target), [[shared-components]]
	- progress bar on top of the container that take full width of the container
	- list name
	- resume(New session) button - if this enrollment's `status` is `completed` (every word mastered), label it "Restart" instead; it calls the same `POST /lists/{listId}/enroll`, which reactivates the row and keeps existing progress (does not reset it)
	- navigate to [[list details]]
- **Navigation**:
	- at the bottom menu with home, list catalog, account [[shared-components]]. Can navigate to: [[home]]*(active), [[list catalog]], [[account settings]]
- (optional) stats strip - not decided how it should look like.  
    
**Data:** `GET /me/home` — single aggregate call returning `user`, `enrollments[]`, `resume{listId}`.  
**States:**
- `loading` (skeleton),
- `empty` (no enrolments → prominent "Browse lists" CTA, hide Continue),
- `loaded` (render enrolments + Continue),
- `error` (retry).
- On launch, also **reconcile**: if device has un-synced results, fire `POST /sessions/{id}/complete` before/after rendering (per the onboarding diagram).

##### API
`GET /me/home` → `user`, `enrollments[]`, `resume{listId}`.  
Reconcile step: `POST /sessions/{sessionId}/complete` if the device buffer holds un-synced results.  
Logout affordance (settings/menu): `POST /auth/logout`. ← _this endpoint had no home before; it belongs on Home's menu._