# Copilot instructions — Finances-API

This file gives concise, repository-specific guidance for GitHub Copilot CLI sessions.

## Build, test, and run (authoritative)
- Use the Maven wrapper (recommended): `./mvnw` (or `mvn` if you prefer). Examples:
  - Full build: `./mvnw clean package` (creates target/*.jar)
  - Run app: `./mvnw spring-boot:run`
  - Run the packaged JAR: `java -jar target/finances-api-<version>.jar` (version in `pom.xml`)
  - Run tests: `./mvnw test`
  - Run a single test class: `./mvnw -Dtest=MyTestClass test`
  - Run a single test method: `./mvnw -Dtest=MyTestClass#myMethod test`

- Docker / local stack:
  - Start full stack (Postgres, Redis, API): `docker compose up` (root folder).
  - To start only the backend container, use `docker compose up <service-name>`.

- Frontend (Angular): located under `app/`.
  - Full-stack via `docker compose up` is the recommended flow.
  - If running the Angular app locally: `cd app && npm install && npm start` (standard Angular workflow).

- Linting: no project-level linter (Checkstyle/SpotBugs/ESLint) is configured in the repository by default.

## High-level architecture
- Monorepo with two primary concerns:
  - Backend: Spring Boot application (Java 24). Sources under `src/main/java`. Build with Maven.
  - Frontend: Angular app under `app/` (served separately or via container). The repo is Docker-ready.
- Runtime services in docker-compose: PostgreSQL (production DB), Redis (session/cache), backend API.
- Security: Spring Security + JWT (jjwt dependencies).
- Persistence: Spring Data JPA -> Postgres (H2 used in tests).
- AI: Spring AI starter is present (openai model starter). Docker-compose exposes an `AI_API_KEY` env var used by AI features (README references GROQ API key as an example).
- Tests: integration/unit tests use H2 (in-memory) and embedded Redis for test runs.

## Key repository conventions and patterns
- Java package root: `br.com.finances` (follow existing package layout).
- Java target: configured to Java 24 via `pom.xml` — use the Maven wrapper to respect that.
- Tests: put unit and integration tests under `src/test/java`. Use the Maven `-Dtest` pattern to run single tests.
- Env and secrets: docker-compose is the primary place for local env overrides. `AI_API_KEY` and DB credentials are set there for local runs.
- Token handling: JWT libraries used (`jjwt-*`) — look for filters/auth classes when adjusting authentication.
- Versioning: project version is in `pom.xml` (`<version>`). Some automation and artifact names (target jar) depend on it.

## Useful files to inspect quickly
- `pom.xml` — build, plugins, dependencies, Java version
- `docker-compose.yml` — local stack services and env vars
- `README.md` / `README.pt-br.md` — run and deployment notes

## AI/assistant config files
- No CLAUDE.md, AGENTS.md, or other assistant rule files were detected. If added, include key rules here.

## When editing this file
- Keep commands that actually work in this repository (avoid generic suggestions).
- If adding linter or CI steps, update the Build/Test section with exact commands and where they live.

