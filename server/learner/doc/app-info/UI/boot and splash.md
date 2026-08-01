

**Purpose:** decide where to send the user — straight to [[home]] if a valid token exists, else Auth([[login-register-reset]]).  
**Components:** logo/loading indicator, styled background - [[shared-components]].  
**Data:** no call of its own; check stored token, then `GET /me/home` (if token) to warm the cache.  
**States:** `checking` → routes to Home or Login/Registration. `token-invalid` → Login. `no-connection` (can't reach `GET /me/home`) → show a retry affordance, do NOT force Login - a real token may still be valid once connectivity returns.
_Note: since auth is stubbed, this always resolves to the demo user, but build the branch so real auth drops in later. Demo user should be backed in the code as a param for dev reasons. On prod AUTH will be available_
##### API
`GET /me/home` (warm cache if token present). No auth call of its own.