| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `lexeme_a` | `bigint` NOT NULL, FK → lexeme.id | number |
| `lexeme_b` | `bigint` NOT NULL, FK → lexeme.id | number |

**Primary key:** (`lexeme_a`, `lexeme_b`)
**Check:** `lexeme_a < lexeme_b`