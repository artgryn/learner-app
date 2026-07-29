# curl requests

Manual smoke-test requests for the API as actually implemented (`http://localhost:8080` —
no `/v1` prefix, unlike the `servers:` entry in `swagger.yaml`; no `context-path` is
configured). Run the app locally, then use these directly.

Endpoints marked **real** are backed by the database (Catalog, Progress, Enrollment).
Endpoints marked **stub** return canned data — the shape matches `swagger.yaml`, but
there's no service logic behind them yet.

## Catalog — real, DB-backed

```bash
curl http://localhost:8080/languages

curl "http://localhost:8080/lists?target=sv&base=en"

curl http://localhost:8080/lists/1
```

Try these too, to exercise the translation-coverage filter and the 404 path:

```bash
curl "http://localhost:8080/lists?target=sv&base=ru"   # -> []
curl http://localhost:8080/lists/9999                  # -> 404 NOT_FOUND
```

## Progress — real, DB-backed

Currently hardcoded to the seeded demo user (account id `1`) - no auth yet.

```bash
curl http://localhost:8080/me/home

curl http://localhost:8080/me/stats
```

## Auth — stub

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"dummy-refresh-token"}'

curl -X POST http://localhost:8080/auth/logout
```

## Enrollment — real, DB-backed

Also hardcoded to the seeded demo user (account id `1`). Enroll is idempotent
by (user, list) — enrolling an already-enrolled list returns 200, a new one
returns 201. List 1 ("Swedish Basics") is already seeded as enrolled; list 2
("En/Ett Nouns") is not.

```bash
curl -i -X POST http://localhost:8080/lists/1/enroll \
  -H "Content-Type: application/json" \
  -d '{"baseLang":"en"}'                              # -> 200, already enrolled

curl -i -X POST http://localhost:8080/lists/2/enroll \
  -H "Content-Type: application/json" \
  -d '{"baseLang":"en"}'                              # -> 201, newly created

curl http://localhost:8080/enrollments

curl http://localhost:8080/enrollments/1

curl http://localhost:8080/enrollments/9999            # -> 404 NOT_FOUND

curl -X DELETE http://localhost:8080/enrollments/1

curl http://localhost:8080/enrollments/1/progress
```

## Session — stub

```bash
curl -X POST http://localhost:8080/enrollments/1/sessions

curl -X POST http://localhost:8080/sessions/a3f2c9d1/complete \
  -H "Content-Type: application/json" \
  -d '{"results":[{"exerciseId":"ex_01","lexemeId":1,"exerciseType":"en_ett","formType":null,"isCorrect":true,"elapsedMs":1800}]}'

curl -X POST http://localhost:8080/sessions/a3f2c9d1/answers \
  -H "Content-Type: application/json" \
  -d '{"results":[{"exerciseId":"ex_01","lexemeId":1,"exerciseType":"en_ett","formType":null,"isCorrect":true,"elapsedMs":1800}]}'
```
