**Purpose:** discover curated lists for the user's language pair. Lists can be shown only for selected lang pair. Current unrolled list should be on top and marked. Each list item should have indicators: unroll, already complete, in progress. If user want to unroll new list and there is in progress list should be warning that user will lose current list progress. 

**Components:**  list of list-cards (name, size, maybe topic), indicators: unroll, already complete, in progress, tap → [[list details]].  

**Data:** `GET /lists?target=sv&base=en` → `ListSummary[]`. (Server returns only pairs with translation coverage.)  

**States:** `loading · loaded · empty (no lists for pair) · error`.  
_MVP: base/target are effectively fixed (en→sv), so the pair selector can be display-only for now — but keep it as a component slot for multi-language later._

**Navigation**:
	- at the bottom menu with home, list catalog, account [[shared-components]]. Can navigate to: [[home]], [[list catalog]](active), [[account settings]]
###### API
`GET /lists?target=sv&base=en` → `ListSummary[]`.  
`GET /language-pairs` → the actual supported (base, target) combos, to populate the pair selector (target/base options) - display-only in MVP but wired here. Prefer this over raw `GET /languages`, which only lists codes/names and doesn't say which combos are valid (same set as the pre-login `GET /public/init` used by [[init account]]).