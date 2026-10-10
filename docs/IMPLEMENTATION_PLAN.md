# Building Management System Implementation Plan

This plan is ordered by dependency and risk. Each task follows: implement, run targeted tests, review the diff, commit, then continue.

## Delivery rules

- Preserve existing data and business purpose.
- Do not reset the database or enable destructive Hibernate DDL.
- Use Java 8 for the legacy baseline until a tested framework migration is ready.
- Keep JSP/SiteMesh and the existing server-rendered architecture during this modernization.
- Do not implement payments, invoices, deposits, or contracts beyond fields already present.
- Treat customer management as missing scope, not as a completed feature.

## Dependency graph

```text
Audit
  -> reproducible build and safe configuration
  -> test harness
  -> API/RBAC/CSRF hardening
  -> repository and persistence fixes
  -> building/user/assignment business fixes
  -> centralized validation and errors
  -> shared UI shell and design tokens
  -> dashboard and CRUD screens
  -> browser regression and responsive QA
  -> documentation, CI, dependency scan, final report
```

## Tasks

### Task 01 - Audit and remediation plan (P0)

Deliverables: `docs/AUDIT_REPORT.md`, this plan, verified baseline commands.

Acceptance: findings distinguish verified defects from missing/unverified features.

### Task 02 - Reproducible build and configuration (P0)

- Remove duplicate/misaligned dependencies and unused Log4j 1.x.
- Add test dependencies and Maven Wrapper.
- Move datasource settings to environment variables.
- Add `.gitignore`; stop tracking generated build and IDE output while preserving local files.
- Standardize artifact name and encoding.

Acceptance: clean `mvn validate`, Java 8 `mvn test` and `mvn package`, no committed secret in active configuration.

### Task 03 - Security regression harness and RBAC (P0)

- Add Spring MVC/Security tests.
- Protect all APIs; ADMIN-only user administration.
- Self-or-admin profile/password rules.
- Restore CSRF for session-authenticated mutations and wire JSP AJAX requests to the token.
- Prevent password serialization and remove shared-password reset behavior.
- Add safe session/logout behavior and STAFF redirect.

Acceptance: anonymous mutations fail; STAFF cannot administer users; authorized CSRF requests pass; password fields are absent from responses.

### Task 04 - Repository and database safety (P0/P1)

- Replace string-built building search SQL with bound parameters/Criteria API.
- Remove `RoleRepositoryImpl` and invalid repository contracts.
- Add safe relation delete methods and indexes/migration scripts where justified.
- Create versioned, non-destructive schema/bootstrap documentation aligned to one database name.

Acceptance: injection payloads are literal; repository contract tests pass; migration never drops production data.

### Task 05 - Building CRUD correctness (P1)

- Load managed entities for update.
- Persist the building before rent areas; validate/normalize rent areas and type codes.
- Return complete list projections and support advertised filters.
- Return 404/400 responses consistently.

Acceptance: create, update, find, search, and delete tests cover valid and invalid input.

### Task 06 - Staff assignment correctness (P1)

- Implement transactional assignment replacement.
- Allow an empty staff list to clear assignments.
- Prevent duplicate assignments.
- Correct checked-state calculation and staff ownership filtering.

Acceptance: assignment is idempotent and order-independent.

### Task 07 - User/staff management correctness (P1)

- Make path IDs authoritative.
- Fix disabled-user search precedence and filtered totals.
- Validate unique username/email and role existence.
- Use status-based deactivation safely and prevent invalid self-deactivation.

Acceptance: service/controller tests cover create, update, search, deactivate, profile, and password flows.

### Task 08 - Centralized validation and error handling (P1)

- Add bean validation to request models.
- Add domain not-found/conflict/validation exceptions.
- Add a consistent API error response and safe logging.
- Remove `System.out` and stack-trace leakage.

Acceptance: 400/403/404/409/500 semantics are tested and sensitive details are not exposed.

### Task 09 - Shared admin UI system (P1)

- Introduce one CSS token/design layer and self-hosted/system-safe typography.
- Repair static asset URLs and remove duplicate per-page framework loads.
- Modernize the shared header/sidebar/content shell while preserving JSP/SiteMesh.
- Standardize buttons, fields, cards, rows, modals, badges, empty/loading/error states, and scrollbars.

Acceptance: shared shell renders without console/404 errors at 375, 768, 1280, and 1440px.

### Task 10 - Login and account screens (P1)

- Replace mixed-language, placeholder authentication UI with the real supported login flow.
- Remove fake registration/social/forgot-password actions unless backed by routes.
- Modernize profile/password/user list/edit screens with validation and status feedback.

Acceptance: keyboard labels work; login errors are clear; no dead controls remain.

### Task 11 - Building list/edit/assignment screens (P1)

- Modernize search/filter, responsive table/list, edit form, assignment modal, and confirmations.
- Wire success/error/loading states to real endpoints.
- Add pagination when service/repository support is in place.

Acceptance: browser flow covers search, create, update, assignment, delete, invalid form, and mobile layouts.

### Task 12 - Dashboard (P2)

- Add only metrics derivable from existing data: active buildings, active users/staff, and assignment coverage.
- Avoid invented rental/payment KPIs.

Acceptance: values come from backend queries and an empty database has a deliberate empty state.

### Task 13 - Customer scope decision (P2)

Customer/tenant management is absent, not merely broken. Implement it only if the existing database schema confirms the domain model or the owner supplies the required fields and lifecycle. Otherwise document it as remaining scope and correct README claims.

### Task 14 - CI, dependency scan, documentation, and final regression (P1)

- Add CI for Java 8 baseline build/test and WAR artifact.
- Run dependency/SCA and secret scans; record actionable results.
- Update README with exact JDK, Maven, MySQL, migration, environment, build, run, and test instructions.
- Run full Maven tests, package, Java 8 startup, browser probes, and critical flows.
- Create `docs/FINAL_REPORT.md` with commits, commands, results, and remaining risks.

Acceptance: a clean checkout can be configured and verified without repository secrets or tracked build output.

## UI direction selected for autonomous execution

- Style: flat property-management SaaS dashboard.
- Layout: responsive shared sidebar, top header, page header, and content area.
- Language: Vietnamese for admin and authentication UI.
- Dark mode: not added; the existing project has no coherent dark-mode system.
- Brand: retain the existing green as the single accent, consolidate its shades into tokens.
- Frontend stack: JSP/JSTL, SiteMesh, Bootstrap-compatible HTML/CSS, minimal jQuery where existing flows require it.
