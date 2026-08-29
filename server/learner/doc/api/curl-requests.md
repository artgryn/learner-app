# curl requests

Manual smoke-test requests for the API as actually implemented (`http://localhost:8080/v1`
— `server.servlet.context-path` is set to `/v1`, matching the `servers:` entry in
`swagger.yaml`). Run the app locally, then use these directly.

Endpoints marked **real** are backed by the database (Catalog, Progress, Enrollment,
Session). Endpoints marked **stub** return canned data — the shape matches
`swagger.yaml`, but there's no service logic behind them yet.

## Catalog — real, DB-backed

```bash
curl http://localhost:8080/v1/languages

curl "http://localhost:8080/v1/lists?target=sv&base=en"

curl http://localhost:8080/v1/lists/1
```

Try these too, to exercise the translation-coverage filter and the 404 path:

```bash
curl "http://localhost:8080/v1/lists?target=sv&base=ru"   # -> []
curl http://localhost:8080/v1/lists/9999                  # -> 404 NOT_FOUND
```

## Progress — real, DB-backed

Currently hardcoded to the seeded demo user (account id `1`) - no auth yet.

```bash
curl http://localhost:8080/v1/me/home

curl http://localhost:8080/v1/me/stats
```

## Auth — stub

```bash
curl -X POST http://localhost:8080/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

curl -X POST http://localhost:8080/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"dummy-refresh-token"}'

curl -X POST http://localhost:8080/v1/auth/logout
```

## Enrollment — real, DB-backed

Also hardcoded to the seeded demo user (account id `1`). Enroll is idempotent
by (user, list) — enrolling an already-enrolled list returns 200, a new one
returns 201. List 1 ("Swedish Basics") is already seeded as enrolled; list 2
("En/Ett Nouns") is not.

```bash
curl -i -X POST http://localhost:8080/v1/lists/1/enroll \
  -H "Content-Type: application/json" \
  -d '{"baseLang":"en"}'                              # -> 200, already enrolled

curl -i -X POST http://localhost:8080/v1/lists/2/enroll \
  -H "Content-Type: application/json" \
  -d '{"baseLang":"en"}'                              # -> 201, newly created

curl http://localhost:8080/v1/enrollments

curl http://localhost:8080/v1/enrollments/1

curl http://localhost:8080/v1/enrollments/9999            # -> 404 NOT_FOUND

curl -X DELETE http://localhost:8080/v1/enrollments/1

curl http://localhost:8080/v1/enrollments/1/progress
```

## Session — real, DB-backed

`POST /enrollments/{listId}/sessions` derives a session in memory (not a DB row
until completed — see `SessionService`) and returns a real `sessionId` (a UUID,
not the placeholder below) plus `items` with real `itemId`s like `it_01`, `it_02`.
Copy those values from the response into the `/complete` call.

```bash
curl -X POST http://localhost:8080/v1/enrollments/1/sessions

# Substitute the sessionId and itemId from the response above.
curl -X POST http://localhost:8080/v1/sessions/<sessionId>/complete \
  -H "Content-Type: application/json" \
  -d '{"results":[{"itemId":"it_01","lexemeId":1,"exerciseType":"en_ett","formType":null,"isCorrect":true,"elapsedMs":1800}]}'

curl http://localhost:8080/v1/sessions/unknown-session-id/complete \
  -X POST -H "Content-Type: application/json" -d '{"results":[]}'   # -> 404 NOT_FOUND
```

`/sessions/{sessionId}/answers` (incremental sync) is not implemented yet —
`SessionController.answers` always returns `501 Not Implemented` with no body,
regardless of session state. Out of scope for now: attempts reference a
`sessions` row that doesn't exist until `/complete`, so there's nowhere to
attach an attempt's `session_id` for a mid-session sync yet (see the comment
on `SessionController.answers`).

```bash
curl -i -X POST http://localhost:8080/v1/sessions/<sessionId>/answers \
  -H "Content-Type: application/json" \
  -d '{"results":[{"itemId":"it_01","lexemeId":1,"exerciseType":"en_ett","formType":null,"isCorrect":true,"elapsedMs":1800}]}'   # -> 501
```
