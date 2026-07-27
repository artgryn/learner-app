| Attribute   | SQL Type                          | General Type |
| ----------- | --------------------------------- | ------------ |
| `list_id`   | `bigint` NOT NULL, FK → list.id   | number       |
| `lexeme_id` | `bigint` NOT NULL, FK → lexeme.id | number       |
| `position`  | `int` (nullable)                  | number       |

**Primary key:** (`list_id`, `lexeme_id`)