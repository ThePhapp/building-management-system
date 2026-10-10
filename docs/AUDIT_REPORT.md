# Building Management System Audit Report

Audit date: 2026-10-10

Audited revision: `4413849`

Working branch: `codex/modernize-building-management`

## Executive summary

The repository packages successfully as an executable WAR on Java 8, connects to the local MySQL instance, and starts on a non-conflicting port. It does not start on the workstation's default Java 21 runtime. The build contains no automated tests and currently provides no reliable regression signal.

The most urgent defect is broken access control: Spring Security permits every `/api/**` endpoint and disables CSRF, exposing user creation, password reset, user deletion, building mutation, and staff/building data to anonymous callers. A live read-only probe confirmed HTTP 200 from anonymous requests to building staff/type endpoints while `/admin/**` redirected to login.

The implemented product is materially smaller than the README. Building and user/staff management exist. Customer/tenant CRUD, customer assignment, and a meaningful operational dashboard do not. The staff-to-building assignment UI exists, but its backend write endpoint is missing.

## Verified architecture

- Java 8 source target, Spring Boot 2.0.9, Spring MVC, Spring Security, Spring Data JPA, Hibernate, MySQL.
- Server-rendered JSP/JSTL views decorated with SiteMesh.
- Bootstrap 3/Ace Admin assets and jQuery-based interactions.
- Maven WAR packaging with an executable `WarLauncher` artifact.
- Main domains present in source: buildings, rent areas, users/roles, staff-to-building assignments.
- Domains described but not implemented: customers/tenants and customer-to-staff assignments.

## Baseline verification

| Check | Result | Evidence |
| --- | --- | --- |
| `mvn test` | Pass with warning | Build succeeds, but there are zero test sources. Maven reports duplicate JPA dependency declarations. |
| `mvn package -DskipTests` | Pass | Produces `target/spring-boot-1.0.war`. |
| Java 21 startup | Fail | Spring 5.0 CGLIB fails with `InaccessibleObjectException`. |
| Java 8 startup | Pass | Application starts on port 18080 and connects to local MySQL. |
| Public home | Pass | `GET /trang-chu` returns 200. |
| Protected admin page | Pass | Anonymous `GET /admin/home` redirects to `/login`. |
| Protected API | Fail | Anonymous building staff and type-code requests return 200. |
| Login rendering | Fail | Local CSS path is wrong; the browser renders an effectively unstyled form. |
| Database bootstrap | Blocked | Existing database is reachable, but the repository has no complete schema migration. |

## Findings

### P0 - Critical

#### P0-01 Anonymous API access enables account takeover and data mutation

- `WebSecurityConfig` disables CSRF and permits `/api/**` (`src/main/java/com/javaweb/config/WebSecurityConfig.java`).
- `UserAPI` exposes create, update, password reset, profile update, and delete operations.
- `BuildingAPI` exposes create/update/delete and assignment-related data.
- User creation trusts a caller-provided role and assigns the shared default password `111` (`SystemConstant.PASSWORD_DEFAULT`).
- User password reset returns the account to that known default.
- User DTO responses can include password hashes.

Required remediation: authenticate every API, enforce ADMIN-only account administration, enforce self-or-admin profile/password operations, restore CSRF for the session-based UI, and add security regression tests.

#### P0-02 A plaintext database credential is committed

`src/main/resources/application.properties` contains a MySQL root username and plaintext password. Copies also exist in tracked build output and the committed WAR.

Required remediation: rotate the credential if it is used anywhere, move configuration to environment variables, remove generated artifacts from version control, and consider history cleanup if the repository has been shared.

#### P0-03 Building search is SQL injectable

`BuildingRepositoryImpl` concatenates request strings and type codes into native SQL. A crafted name value can alter the `WHERE` clause.

Required remediation: replace string-built SQL with Criteria API/JPQL parameters and test apostrophes and SQL metacharacters as literal input.

### P1 - High

#### P1-01 Staff-to-building assignment is incomplete and reads incorrect state

The JSP posts to `/api/building/assignment`, but no controller or service operation exists. The read path repeatedly resets each staff member's checked state, making the result depend on assignment iteration order.

#### P1-02 Building type persistence is broken

`BuildingDTO` uses a list while `BuildingEntity` stores one string. Default ModelMapper serializes the list using bracket notation. `listTypeCode` has its null check reversed: non-null values mark all types unchecked, while null values are split and throw.

#### P1-03 Building create/update ordering is unsafe

Rent areas are saved before a new building is persisted, and update maps into a replacement entity rather than updating the managed record. Blank or malformed rent-area values cause unhandled exceptions.

#### P1-04 User update can insert or overwrite the wrong record

The service loads the path-ID user but saves the request body's ID. Missing or mismatched IDs can create a new row or update an unrelated row.

#### P1-05 Repository implementation violates Spring Data contracts

`RoleRepositoryImpl` directly implements `JpaRepository`; most inherited methods return null/false/zero or silently do nothing. It also concatenates a role code into SQL. Spring Data should implement the repository interface.

#### P1-06 Dependency train is inconsistent and obsolete

- Duplicate `spring-boot-starter-data-jpa` declaration.
- Spring Boot 2.0.9 with a forced Tomcat starter 2.4.4.
- Spring Security taglibs 4.2.16 alongside Security 5.0.12.
- Direct, unused Log4j 1.2.17 dependency.
- Legacy MySQL connector, DisplayTag, SiteMesh, ModelMapper, Commons Lang, iText, BeanUtils, and Collections dependencies require vulnerability review.

A framework migration must remain staged. The current `javax.*` JSP/JPA application should first gain tests and a reproducible Java 8 baseline; a later Java/Jakarta migration must be treated separately.

#### P1-07 Database setup is contradictory and incomplete

Runtime configuration selects `estateadvance`; `database/insert_database.sql` selects `springbootweb`. The SQL file contains seed data only, while Hibernate DDL is disabled. A clean environment cannot create the schema.

#### P1-08 Generated artifacts and IDE metadata are tracked

`target/` accounts for 701 tracked files and contains stale binaries, the executable WAR, and copied credentials. There is no root `.gitignore`; `.idea` metadata and `spring-boot.iml` are tracked.

#### P1-09 Static asset routing is broken

The login decorator references `style.css`, but the stylesheet is located under `static/login/style.css`. Admin decorators use route-relative asset URLs, which resolve under `/admin/` and can return 404. Several JSPs also load duplicate jQuery/Bootstrap copies and remote CDNs.

### P2 - Medium

#### P2-01 Building list response discards data

The response model contains address, manager, area, basement, and price fields, but `BuildingService.findAll` assigns only ID and name. The JSP therefore displays empty or placeholder values.

#### P2-02 User search and totals are incorrect

The derived query's `OR`/`AND` precedence can include disabled users. The controller always reports the unfiltered total even when search is active.

#### P2-03 Error handling and HTTP semantics are inconsistent

Services repeatedly call `Optional.get()`, producing 500 errors for missing IDs. Password mismatch returns HTTP 200. There is no centralized API error schema, input validation, or safe validation feedback.

#### P2-04 STAFF login redirect is missing

STAFF can access `/admin/**`, but the success handler recognizes only USER, ADMIN, and MANAGER and can return an empty redirect target.

#### P2-05 UI integration is unreliable

- Create/update and delete requests log success but do not redirect, refresh, or show a message.
- Delete sends an unnecessary JSON body and does not confirm in the page flow.
- Assignment forbids an empty selection, so all staff cannot be unassigned.
- Placeholder links exist for forgot password, registration, social login, breadcrumbs, and sidebar shortcut buttons.
- Copy is mixed Vietnamese/English and accessibility labels are inconsistent.
- Building pages embed duplicate layouts, inline styles, scripts, and external dependencies.

#### P2-06 No meaningful dashboard

`admin/home.jsp` renders an empty content region. No aggregate service exists for real operational metrics.

#### P2-07 No automated tests or CI

There is no `src/test`, test dependency, Maven wrapper, or CI workflow. Security, persistence, JSP packaging, and business flows have no regression coverage.

### P3 - Low

- Typographical and naming defects (`setDistrist`, `introducceBuiding`, `buidingList`, entity relation fields named `buildingId`/`staffId`).
- Dead controller route `/admin/building-list-{id}` and empty/commented classes.
- `System.out`/`printStackTrace` instead of structured logging.
- README describes the wrong clone URL, artifact type/name, entry route, source tree, and implemented domains.
- Large sets of duplicate and unused static assets increase maintenance and page weight.

## Business-domain reality check

| Domain | Status |
| --- | --- |
| Building CRUD/search | Present but contains persistence, projection, validation, and query defects. |
| Staff/user management | Present but authorization and update semantics are unsafe. |
| Staff-to-building assignment | Read UI present; write endpoint missing; checked-state calculation broken. |
| Customer/tenant management | Not implemented beyond an unused DTO. |
| Customer-to-staff assignment | Not implemented. |
| Rental operations | Rent-area and building rental attributes exist; no separate contract/payment/invoice workflow exists. |
| Dashboard | Route/view exists; content and metrics are not implemented. |

## Security posture

Current posture is unsuitable for network exposure. Until P0 remediation is deployed, the application should be treated as local-development-only and isolated from untrusted networks. Database credentials and the shared default password must be considered exposed.

## Audit limitations

- No production database or deployment was modified.
- MySQL schema contents were not exported or reset.
- Mutating anonymous API probes were intentionally not executed against the existing database; exposure was established from configuration, code, and read-only requests.
- Responsive authenticated admin pages could not be fully exercised because no valid test credentials were available without changing account state.
- Dependency scanning and complete regression results will be added to `docs/FINAL_REPORT.md` after remediation.
