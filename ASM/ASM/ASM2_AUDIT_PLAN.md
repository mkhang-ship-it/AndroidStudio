# ASM2 — Audit & Implementation Plan (report/evidence only)

**Unit 22: Application Development — Assignment Part 2**
**Product:** AI Study Mentor (BrightPath Learning) — Android app in `AI Study/`
**Prepared by:** Pane_AUDIT (independent audit lead)
**Date:** 2026-11-09
**Status:** PLANNING ONLY — no source changes, no report prose, no fabricated evidence.

---

## 0. Scope, rules and evidence policy

| Rule | Meaning in practice |
|---|---|
| App is frozen | `AI Study/` is treated as a completed deliverable. No feature work, no refactor, no bug fixing. |
| Evidence-only audit | Every claim in the report must trace to a file path, a command output, a screenshot, or a commit hash. |
| No invention | Survey participants, responses, measurements, timestamps and screenshots that do not exist are marked **PENDING**, never estimated. |
| Report language | English (British spelling), Calibri 12, 1.5 spacing, Harvard referencing. |
| Target device | **Pixel, API 35** for screenshots and measured runs. Existing captures are from a different AVD (see §7). |

**Status vocabulary used throughout this plan**

| Tag | Meaning |
|---|---|
| `HAVE` | Verified in this session (command run / file read / git inspected). |
| `DOC-ONLY` | Claimed in `docs/*.md` but not re-verified in this session. |
| `PENDING` | Does not exist yet; must be produced by a human. |
| `BLOCKED` | Cannot be produced without an external decision (tutor, group, hardware). |

---

## 1. Verified baseline (re-measured in this session, not copied from docs)

| Fact | Value | How verified |
|---|---|---|
| JVM unit tests | **152 tests, 12 classes, 0 failures, 0 errors, 0 skipped** | `:app:testDebugUnitTest --offline` → BUILD SUCCESSFUL; aggregated `app/build/test-results/testDebugUnitTest/TEST-*.xml` |
| Test annotations in repo | 191 total (`@Test`), of which 152 JVM + 39 instrumented | `Select-String '@Test'` per file |
| Instrumented suite | Compiles (`:app:assembleDebugAndroidTest` BUILD SUCCESSFUL); **execution not re-run** — no device attached (`adb devices` empty) | Gradle + adb |
| Debug APK | Builds, **33.53 MB** | `:app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk` |
| Source size | 98 Kotlin files, 12,103 lines in `app/src/main` | recursive count |
| Room schema | version 1, **7 tables** (users 12 cols, profiles 6, question_answers 21, quiz_items 16, xp_events 5, user_badges 3, notifications 8) | `app/schemas/.../1.json` |
| Git history | 13 commits, 2026-09-30 → 2026-11-02, 4 authors, **no tags, no merge commits, no PR refs** | `git log`, `git shortlog -sne`, `git ls-remote` |
| Remote | `origin` = `github.com/mkhang-ship-it/AndroidStudio` (reachable, `main` = `79c523a`) | `git ls-remote --heads origin` |
| Existing screenshots | 11 PNGs in `docs/screenshots/`, all **1080×2400**, captured from AVD `Medium_Phone` = android-37 / playstore | file metadata + AVD `config.ini` |

**Environment caveat found:** Gradle fails with `SDK location not found` unless `ANDROID_HOME` is exported or `AI Study/local.properties` exists. `local.properties` is gitignored and absent in this worktree. All commands in the checklist therefore set `ANDROID_HOME` explicitly.

**Screenshot caveat found:** the only installed system image is `android-37.1/google_apis_playstore_ps16k`; there is **no API 35 image installed**. Pixel API 35 captures are `BLOCKED` until the image and AVD are created (commands in checklist §2).

---

## 2. Part 1 baseline this report must evaluate against

Extracted from `BC00720_KhangNM_Assignment_Part1.pdf` (31 pages). This is the authoritative design document; ASM2 is evaluated **against it**, so every mismatch is a legitimate finding.

| ID | Part 1 commitment | Report section that must judge it |
|---|---|---|
| FR1 | Email register/login; session; 5-failure lockout | 3.3, 4.2 |
| FR2 | Onboarding: level, subjects, explanation style | 3.3 |
| FR3 | Ask by text **and image** | 2.4.1, 3.3 |
| FR4 | Structured AI answer, subject + difficulty, steps | 2.4.2, 3.3 |
| FR5 | Simplified / alternative / key concepts / mistakes / follow-ups | 2.4.5, 3.3 |
| FR6 | History + search + bookmark + subject filter | 2.4.3, 3.3 |
| FR7 | AI-generated practice questions with feedback | 2.4.5, 3.3 |
| FR8 | Progress: totals, subjects, quiz accuracy, review time | 2.4.4, 3.3 |
| FR9 | XP, levels, badges, leaderboard | 2.4.5, 3.6 |
| FR10 | Reminders, mistake alerts, summaries | 2.4.5, 3.6 |
| NFR1 | UI responds ≤100 ms; progress during AI call | 3.4 |
| NFR2 | Question submittable in ≤3 actions | 3.4 |
| NFR3 | Android 13 (API 33)+ | 3.4 (verified: `minSdk = 33`) |
| NFR4 | Encryption at rest, data minimisation, GDPR/COPPA | 3.4, 2.3.2 |
| NFR5 | Saved answers readable offline | 3.4 |
| NFR6 | Password strength, optional 2FA, lockout | 3.4 |
| Risk R1–R10 | Register with owners; R1 cost, R3 hallucination, R5 privacy | 4.5 |
| Test T1–T10 | Ten planned checks | 4.4 |
| ADR D-1..D-12 | Compose, Room, HttpURLConnection, PBKDF2, AES-GCM, cache policy | 2.2.2, 4.3 |
| 14-week Scrum | One-week sprints, backlog, DoD | 2.2.1, 4.3 |

Part 1 also states the MoSCoW cut explicitly: **FR1–FR4, FR6, FR8 are MVP; FR5, FR7, FR9, FR10 follow after the core loop.** The report must honour that line rather than treating deferred items as failures.

---

## 3. Criterion → section → evidence map

| Criterion | Wording (abridged) | Primary sections | Evidence that must exist |
|---|---|---|---|
| **P4** | Conduct peer review of problem definition, solution, strategy; document feedback | 1.1–1.3, Appendix A | Completed questionnaire (Q1–Q12), participant table with real names/roles, per-response records |
| **M3** | Interpret peer feedback; identify opportunities not previously considered | 1.4, 5.3 | Response analysis + at least one genuinely new opportunity, or an honest statement that the review surfaced none |
| **P5** | Develop a functional business application with support documentation | 2.1–2.7 | Running APK, build log, user guide, setup instructions, demo account procedure |
| **M4** | Develop from a specific design document, with evidence of preferred tools/techniques/methodology | 2.2.2, 2.5, 2.6 | Tool-by-tool activity evidence (commit, screenshot, artefact), sprint log, CI run |
| **P6** | Review performance against problem definition and initial requirements | 3.1–3.6 | Traceability matrix with Met/Partial/Not met/Not tested, 20 test cases, measured results |
| **M5** | Critically review design, development and testing stages **including risks** | 4.1–4.6 | Risk register table with observed evidence, defect log, test-coverage critique |
| **D2** | Justify improvements made because of feedback, **and feedback not acted upon**; opportunities | 5.1–5.5 | Feedback→change table with commit + test, plus justified rejections/deferrals |

**Dependency note:** D2 cannot be written honestly before P4 data exists. M3 cannot be written before D2 decisions are recorded. Order is therefore P4 → M3 → P5/M4 → P6 → M5 → D2 in *evidence* terms, even though the report is assembled in section order.

---

## 4. Report outline (English, ~2,000–2,500 words body + tables/appendices)

Word-budget conflict to resolve with the tutor: the brief's Submission Format says **2,000–2,500 words**, Activity 2 says the development report alone is **2,500–3,000 words**. Recorded as decision **DEC-01** (see §10). Recommended: follow Activity 2 for the development chapter and keep the whole document at 2,500–3,000, noting the overlap in the introduction.

### Front matter
- Assignment front sheet 2 (group) — `FS-01` `PENDING`: group number, member names + codes, class, assessor. Source template: `FRONTSHEET ASM FINAL REPORT (GROUP).docx`.
- Table of contents, list of tables, list of figures.

### 1. Peer Review of Requirements and Proposed Solution (P4 + M3) — ~500 words
| Sub-section | Content | Evidence |
|---|---|---|
| 1.1 Review baseline from Part 1 | Condensed problem definition, target users, requirement groups, architecture + SDLC choice. Not a copy of the Part 1 tables. | `BC00720_KhangNM_Assignment_Part1.pdf` §1–§5; `docs/PART1_M1_SOFTWARE_DESIGN.md` |
| 1.2.1 Objective and instrument | Review objectives (clarity of problem, completeness/priority of requirements, fitness of solution, feasibility of 14-week strategy). Twelve questions Q1–Q12 (9 Likert + 3 open) mapped to those four objectives. | Questionnaire in Appendix A — **template only, not yet administered** |
| 1.2.2 Participants and procedure | Table PR-01…PR-0n: name, role, job/study description, participation date and mode. | `PENDING` — no real participants exist |
| 1.3.1 Response summary | Invitations sent, responses received, valid responses, aggregation method, explicit statement that the sample is not representative. | `PENDING` — no responses exist |
| 1.3.2 Weaknesses identified | Per weakness: evidence → impact on project → priority. | Derived from real responses only |
| 1.4 Previously unconsidered opportunities (M3) | New needs/risks surfaced by review that Part 1 did not cover; why they were missed; practical value. If none emerged, say so and state the review's limits. | Derived from real responses only |
| 1.5 Initial refinement decisions | Table FB-01…: original item → decision (adopt/modify/defer/reject) → trace to §2.x / §5.x | Planned; final outcome belongs in §5 |

### 2. Application Development and Implementation (P5 + M4)
| Sub-section | Content | Evidence |
|---|---|---|
| 2.1.1 Design → implementation mapping | Which Part 1 artefacts were inherited unchanged, which changed after review, which changed during coding. | Part 1 §4.3/§4.5 vs actual entities and files |
| 2.1.2 MVP scope and out-of-scope | Four core functions; status of every remaining requirement as a table. Distinguish "not in MVP" from "not implemented". | §5 audit table in this plan |
| 2.2.1 Schedule, responsibilities, progress | 14-week plan vs actual: phases, owners, dependencies, milestones, slippage and recovery. | `docs/AGILE_SPRINT_LOG.md` (S01–S03 and S14 populated, S04–S13 blank → `PENDING`); git dates |
| 2.2.2 Stack and working practices | IDE, language, UI, DB, AI provider, VCS, CI, testing, deployment — actual versions. | `gradle/libs.versions.toml` (AGP 9.3.3, Kotlin 2.2.10, Compose BOM 2026.02.01, Room 2.7.2, KSP 2.3.12, Navigation 2.10.2, coroutines 1.10.2) |
| 2.3.1 As-built architecture | Four layers, one module, manual DI, local-first storage, single external dependency (Gemini). Complete ask→answer→history flow. | `ui/ domain/ data/ platform/` tree; `platform/ServiceGraph.kt`; 13 PlantUML sources in `docs/uml/` |
| 2.3.2 Data storage, security, AI integration | Room as source of truth; PBKDF2 120k; EncryptedSharedPreferences session; AES-GCM images; hash + Jaccard cache; error taxonomy. State plainly what is **not** encrypted (SQLite body columns). | See §5 gaps: `AbuseFilter` never called; image files never deleted |
| 2.4.1 AI question submission | Text path; image path; validation; error surfacing. | `ui/screens/ask/AskScreen.kt`, `QuestionRepositoryImpl.askInternal` |
| 2.4.2 AI answer generation | Gemini `generateContent`, JSON schema `steps/finalAnswer`, retry on 429/5xx, 20 s timeout, `AiException` taxonomy, LaTeX repair. Must discuss accuracy risk without claiming accuracy. | `domain/ai/GeminiAiClient.kt`, `domain/ai/AiError.kt` |
| 2.4.3 Question history | Persist/retrieve, duplicate collapse by `questionHash`, search (LIKE not FTS), bookmark, subject filter, delete-by-hash. | `QuestionAnswerDao.kt`, `ui/screens/history/HistoryScreen.kt`, `ui/history/HistoryViewModel.kt` |
| 2.4.4 Basic progress tracking | Which metric comes from which query; quiz accuracy excludes unattempted; review time accrues 15 s ticks while resumed. | `ProgressRepositoryImpl.kt`, `QuizItemDao.accuracy`, `AskScreen` review timer |
| 2.4.5 Supporting / optional / deferred | FR5 (5 improvement kinds), FR7 (practice), FR9 (XP/badges, local only), FR10 (notifications). Short status table. | `QuestionDetailScreen.kt`, `QuizRepositoryImpl.kt`, `GamificationRepositoryImpl.kt`, `NotificationRepositoryImpl.kt` |
| 2.5.1 Toolchain evidence | Per tool: activity, artefact, date, screenshot/link, effect. Not a list of tool names. | `.github/workflows/android-test.yml`, git log, `docs/BUILD_LOG_NP5.md`, `docs/TEST_REPORT_NP5.md` |
| 2.5.2 Methodology in practice | One-week iterations, backlog, DoD, review. Do **not** claim full Scrum ceremony. | `docs/AGILE_SPRINT_LOG.md`, `AI Study/CONTRIBUTING.md` |
| 2.6.1 Completed tasks and stages | Sequence, output per stage, member split. Screenshots must show stages, not only the finished UI. | `PENDING` — build/IDE stage captures |
| 2.6.2 Challenges, defects and fixes | Symptom → cause → fix → retest → residual limit. | `docs/BUG_LIST_NP5.md` (18 fixed, 4 open), commit history |
| 2.7 Support documentation and package | Source, environment, key config, build/run, user guide, demo account, reproduction steps. No real key. | `README.md`, `CONTRIBUTING.md`, `ai.properties.example`, APK |
| 2.8 Demonstration / presentation | Slides or demo record. | `PENDING` |

### 3. Evaluation Against Initial Requirements (P6)
| Sub-section | Content | Evidence |
|---|---|---|
| 3.1 Approach and evidence | Which build was evaluated (commit `79c523a`), baseline used, test methods, what is functional test vs opinion, and explicit "not verified" items. | This plan §1 |
| 3.2 Performance against the problem definition | Does the app let a student ask, get an explanation, retrieve history and see progress? Which original pain points remain unsolved? | Traced to specific test cases |
| 3.3 Functional requirement verification | Traceability matrix, statuses Met / Partial / Not met / **Not tested**; at least 20 test cases with expected/actual. | §6 of this plan seeds the matrix; 20 TC rows `PENDING` |
| 3.4 Non-functional verification | Performance (measure, don't assert), usability (3-action rule), compatibility, privacy/security scope, offline behaviour (manual test with airplane mode). | `PENDING` measurements; NFR3 already verified by `minSdk = 33` |
| 3.5 Feature quality vs user expectations | Separate technical correctness (tests pass) from user acceptance (peer/usability feedback). | Needs §1 data |
| 3.6 Requirement gaps and residual limitations | Unmet requirements, impact, cause, priority; bridge to §4 and §5. | §5 audit table |

### 4. Critical Review of the Development Process (M5)
| Sub-section | Content | Evidence |
|---|---|---|
| 4.1 Contribution of the systems investigation | Specific finding → specific decision → specific change → quality effect. | Part 1 §1.3 + bug log |
| 4.2 Critical review of the design stage | Strengths and weaknesses of OOAD/UML/architecture against the as-built app; wrong assumptions; trade-offs. | Part 1 §4 vs code drift (see §5) |
| 4.3 Critical review of the development stage | Toolset fit, SDLC fit, team coordination, 14 weeks, junior capability, AI cost control. Planned vs actual. | Sprint log, commit cadence |
| 4.4 Critical review of the testing stage | Part 1 test plan vs what ran. 152 JVM tests exist; instrumented 39 compiled; UI coverage is a structural smoke test, not a behaviour test. Absence of AI-accuracy, privacy and offline tests is a quality risk. | `docs/PART1_M1_TEST_PLAN.md` vs actual test files |
| 4.5 Risk identification, monitoring, mitigation | R1–R10 revisited with observed evidence, mitigation outcome, residual risk. No invented probabilities. | Part 1 §2.2 + `docs/BUG_LIST_NP5.md` |
| 4.6 Impact on reliability, usability, security, effectiveness | Per attribute: action → evidence → improvement or limit. | Cross-reference §3.4 |

### 5. Feedback Decisions and Future Improvements (D2)
| Sub-section | Content | Evidence |
|---|---|---|
| 5.1 Feedback → change traceability | FB-ID → decision → module/design changed → commit + test. "Planned" is not "done". | Needs §1 data |
| 5.2 Justification of adopted feedback | Per item: evidence strength → problem → solution → effect on users/cost/schedule/security → test result → residual limit; argue why better than no change. | Needs §1 data |
| 5.3 Justification of feedback not acted upon | Per rejection/deferral: underlying need, options considered, reason, consequence, revisit condition. If nothing was rejected, state that honestly. | Needs §1 data |
| 5.4 Opportunities for improvement and further development | Evidence of gap → enhancement → expected benefit → feasibility/trade-off → priority. Must not present Part 1 requirements as new discoveries. | `docs/D2_FUTURE_NP5.md` (leaderboard, real 2FA, two-way sync, SSE streaming, Vietnamese mistake alerts) |
| 5.5 Overall evaluation of the improvement strategy | Which decisions created the most credible value, which were limited, cost/quality/schedule trade-offs, recommended priorities. No invented percentage improvements. | Synthesis |

### Appendices
- **A** — Peer review questionnaire, question-by-question, verbatim, with Q-IDs.
- **B** — Participant profiles and raw responses + aggregation table.
- **C** — Test evidence: JUnit XML summary, instrumented report, manual matrix.
- **D** — Screenshot set with captions (Pixel API 35).
- **E** — Commit/PR/CI evidence index.

---

## 5. Audit of the completed application (evidence, not opinion)

### 5.1 The four MVP functions

| # | Function | Status | Verified evidence | Honest limitation to state |
|---|---|---|---|---|
| 1 | AI question submission (text) | **Implemented** | `AskScreen.kt` submit path; `QuestionRepositoryImpl.askInternal`; validation ≤2,000 chars; length/empty guards; 152 JVM tests green | No Compose behaviour test for the submit path; error mapping tested only via unit-level parsers |
| 2 | AI question submission (image) | **Partial** | Visual-media picker, thumbnail preview, `AesGcmImageStore.store` (AES-GCM, AES key in AndroidKeyStore), bytes sent as `inline_data` | **Attached image is never displayed again**: no `imageStore.read` in any UI file; `questionImagePath` is written but never rendered in detail/history |
| 3 | AI answer generation | **Implemented** | `GeminiAiClient.answer`, JSON `steps[{title,body,expr}]` + `finalAnswer`, retry 429/5xx with backoff, 20 s timeout, LaTeX escape repair, `AiException` taxonomy, `usageTokens()` from `usageMetadata` | `FakeAiClient` is the demo path when no key; answer correctness is unmeasured — must be described as a risk, not a quality claim |
| 4 | Question history | **Implemented** | `observeHistory`, `search` (LIKE), `bySubject`, bookmarking, duplicate collapse by `questionHash`, delete-by-hash, survives restart (Room) | Search is `LIKE '%…%'`, not FTS; no pagination; the "survives restart" case has no automated test (`PENDING`) |
| 5 | Basic progress tracking | **Implemented** | `ProgressRepositoryImpl.summary` over 4 DAOs; total questions, per-subject counts, review seconds, quiz accuracy (unattempted excluded), XP totals, insights generated offline | No performance measurement; no dashboard screenshot from the target device |

### 5.2 Supporting functions

| Area | Status | Evidence / gap |
|---|---|---|
| Auth: register, login, logout, delete account, lockout | **Implemented** | `AuthRepositoryImpl` (transactional register, unique index race guard, PBKDF2 120k + per-user salt, constant-time verify, `SharedPrefsLoginThrottle` persists across restart, account deletion clears 5 tables) |
| Onboarding: level & style | **Implemented** | `OnboardingScreen.kt` two steps |
| Onboarding: **subject selection** | **Not implemented** | `submitOnboarding(subjects = emptySet())` — `OnboardingUiState` has no subject field; profile subjects always empty at onboarding. FR2 is **Partial** |
| Quiz / practice | **Implemented, one scoring concern** | `QuizRepositoryImpl`, `PracticeViewModel`; MCQ correctness compares the chosen **option text** against `correctAnswer` (`PracticeScreen.kt:269`, `PracticeViewModel.checkAnswer`). If the model returns an option letter (as `FakeAiClient` does with `correctAnswer = "A"`), an MCQ can never be scored correct. **Flag as a defect to verify manually**, not to fix. Practice has no automated test |
| Gamification | **Partial** | XP, levels, streaks, badges, daily idempotency (`GamificationRepositoryImpl`); **no leaderboard** (`grep leaderboard` → 0 matches in `main`). FR9 Partial |
| Notifications | **Partial** | List, read/unread, detail screen, deep link, daily summary, motivational, mistake alert. `REVIEW_REMINDER` is defined but never generated. Mistake alert keys on English words only (`MISTAKE_KEYWORDS`). FR10 Partial |
| Security | **Implemented with scope limits** | PBKDF2, EncryptedSharedPreferences session, AES-GCM images, lockout persistence, minimal permissions (INTERNET, ACCESS_NETWORK_STATE only), no key in VCS (`ai.properties` gitignored, `BuildConfig` field) |
| 2FA | **Not implemented** | `twoFactorSecret` column and setter exist; no UI, no secret generation, no verification. Schema-only |
| Abuse filtering | **Not implemented in the running app** | `AbuseFilter` exists and is unit-tested (5 cases) but is **never referenced from `main`** — `grep AbuseFilter` returns only its own file. Dead code |
| Offline | **Partial** | Saved history readable without network; `FakeAiClient` keeps the app usable with no key. **No connectivity observer, no pending-sync queue** — `grep pending|queue|offline` finds only strings and comments. A new question while offline simply fails |
| Image lifecycle / privacy | **Gap** | `ImageStore.delete` is never called; `clearUserData` deletes rows only. Encrypted image files survive account deletion |
| Error handling | **Implemented** | Typed `AiException` (NotConfigured / Provider / Network / Malformed) mapped to user-facing strings; retry with exponential backoff; typed UI error banners |
| AI credential handling | **Implemented** | `ai.properties` → `local.properties` → env var; `.gitignore` covers `local.properties` and `/ai.properties`; key becomes a `BuildConfig` field; CI runs keyless with `FakeAiClient` |

### 5.3 Documented-but-unverified claims (must not be repeated as fact)

| Claim | Source | Status |
|---|---|---|
| "39/39 instrumented tests pass" | `docs/IMPROVEMENT_LOG.md`, sprint log S14 | `DOC-ONLY` — no device attached in this session, no XML retained |
| "9/9 AI question matrix passes" | `docs/IMPROVEMENT_LOG.md` | `DOC-ONLY` — manual run, no retained artefact |
| "143 unit tests" | `docs/IMPROVEMENT_LOG.md` | **Contradicted** — actual is 152 |
| "76 tests / 8 files" | `docs/TEST_REPORT_NP5.md` | Outdated — actual is 152 / 12 |
| "120 tests, 0 failures" | Part 1 §4.6 | Outdated — actual is 152 |
| Sprint log S04–S13 filled | `docs/AGILE_SPRINT_LOG.md` | `PENDING` — 10 sprints are empty rows |

**Rule for the report:** use only the re-measured numbers in §1, or label a figure with its source document and the date it was produced.

### 5.4 Requirement traceability seed (P6 table)

Fill the final "Test / actual outcome" and "Evidence" columns only with what you can show.

| Req | Implementation | Status | Note |
|---|---|---|---|
| FR1 | `AuthRepositoryImpl`, `PasswordHasher`, `LoginThrottle`, `EncryptedPrefsSessionStore` | Met | 13 instrumented cases exist; execution `PENDING` |
| FR2 | `OnboardingScreen` (level, style) | **Partial** | No subject selection |
| FR3 | `AskScreen`, `QuestionRepositoryImpl`, `AesGcmImageStore` | **Partial** | Text met; image stored but never re-displayed |
| FR4 | `GeminiAiClient`, `AiError` | Met | 27 parsing tests pass |
| FR5 | `improve()`, `ImprovementKind`, `QuestionDetailScreen` | Met (post-MVP) | Not in the MVP MoSCoW cut; 5 kinds wired |
| FR6 | `QuestionAnswerDao`, `HistoryScreen/ViewModel` | Met | Search is LIKE-based |
| FR7 | `QuizRepositoryImpl`, `PracticeScreen` | Met with defect risk | MCQ scoring needs manual verification |
| FR8 | `ProgressRepositoryImpl`, `ProgressScreen`, `ProgressInsights` | Met | No measurement |
| FR9 | `GamificationRepositoryImpl`, `XpRules`, `BadgeCatalog` | **Partial** | Local only, no leaderboard |
| FR10 | `NotificationRepositoryImpl`, notification screens | **Partial** | No review reminders; English-only mistake keywords |
| NFR1 | coroutines/IO, progress card | **Not tested** | Needs measurement |
| NFR2 | Ask screen flow | **Not tested** | Count actions manually |
| NFR3 | `minSdk = 33` | Met | Build config |
| NFR4 | PBKDF2, AES-GCM, EncryptedPrefs, minimal permissions | **Partial** | SQLite columns unencrypted; image files not deleted on account removal |
| NFR5 | Room local read | **Partial** | Read-only offline; no queue |
| NFR6 | Password rules, lockout | **Partial** | 2FA absent |

---

## 6. Peer review instrument (design only — no data)

Deliverable: a questionnaire plus a response template. **Do not populate it.**

**Participant table (template)**

| Participant ID | Name | Role | Job description / study context | Date & mode | Consent |
|---|---|---|---|---|---|
| PR-01 | `PENDING` | Student / Developer / Other | `PENDING` | `PENDING` | `PENDING` |
| PR-02 | `PENDING` | … | `PENDING` | `PENDING` | `PENDING` |
| PR-03 | `PENDING` | … | `PENDING` | `PENDING` | `PENDING` |

Minimum viable and defensible: **3 participants**, at least one student outside the team and one person with development experience. State the sample limitation explicitly in §1.3.1.

**Question set (fixed Q-IDs so results are traceable)**

Likert 1–5 (1 = strongly disagree, 5 = strongly agree):

| Q-ID | Target | Question |
|---|---|---|
| Q01 | Problem definition | The learning problems AI Study Mentor addresses are clearly defined. |
| Q02 | Problem definition | The target users (middle school, high school, university) are clearly identified. |
| Q03 | Requirements | The proposed user requirements reflect students' actual learning needs. |
| Q04 | Requirements | The priority order of the requirements is sensible for a 14-week MVP. |
| Q05 | Solution | The proposed AI question-and-answer feature is suitable for the identified problems. |
| Q06 | Solution | The question history and progress features are useful to students. |
| Q07 | Solution | The proposed architecture (local-first, no backend) is appropriate for the budget and team. |
| Q08 | Strategy | The selected development tools are appropriate for building the application. |
| Q09 | Strategy | The selected development methodology suits a small junior team. |
| Q10 | Strategy | The development plan is realistic within the 14-week timeline and limited budget. |

Open questions:

| Q-ID | Target | Question |
|---|---|---|
| Q11 | Requirements | What important user requirements or learning problems are missing? |
| Q12 | Solution / strategy | What weaknesses or risks do you see in the solution or the development strategy? |
| Q13 | Improvement | What single improvement would you prioritise, and why? |

**Response record (template, one row per participant per question)** — `PENDING` for every cell:

| Participant ID | Q-ID | Response | Received on | Channel |
|---|---|---|---|---|
| PR-01 | Q01 | `PENDING` | `PENDING` | `PENDING` |

**Analysis rules to state in the report**
- Report invitations, responses and valid responses as three separate numbers.
- If n < 5, do not compute percentages and do not generalise beyond the sample.
- Likert results: report median and range per question, not a single "satisfaction score".
- Open answers: quote verbatim (with participant ID), then group into themes.
- Any weakness in §1.3.2 needs at least one quoted response as its evidence.

---

## 7. Evidence production plan

### 7.1 Screenshot set — Pixel, API 35 (all `PENDING`)

Existing images are from a 1080×2400 AVD on android-37. They may be reused only as **interim** evidence and must be replaced for the final submission.

| Shot | Screen | What must be visible | Purpose |
|---|---|---|---|
| S01 | Welcome / Login | Email + password form, sign-in and sign-up actions | FR1 |
| S02 | Register | Full name, email, password, validation state | FR1 |
| S03 | Onboarding step 1 | Level choices + COPPA note | FR2 |
| S04 | Onboarding step 2 | Explanation style choices | FR2 |
| S05 | Ask (empty) | Input, image picker, submit, no-key banner if applicable | FR3 |
| S06 | Ask (loading) | Thinking state with progress indicator | NFR1 |
| S07 | Ask (answer) | Detected subject chip, steps, final answer | FR4 |
| S08 | Answer with formula | Rendered LaTeX fraction/limit | FR4 quality |
| S09 | Question detail | Answer plus five improvement blocks | FR5 |
| S10 | History (list) | Search field, filters, subject grouping | FR6 |
| S11 | History (search result) | Filtered result for a typed keyword | FR6 |
| S12 | Bookmarked filter | Only bookmarked items | FR6 |
| S13 | Practice setup | Source question, count stepper 1–20 | FR7 |
| S14 | Practice question | MCQ or short answer | FR7 |
| S15 | Practice feedback | Correct/incorrect plus explanation | FR7 |
| S16 | Progress | Totals, review time, accuracy, XP, per-subject bars, insights | FR8 |
| S17 | Profile | Level, style, badges, logout, delete account | FR9 |
| S18 | Notifications list | Types, unread badge | FR10 |
| S19 | Notification detail | Deep-link action | FR10 |
| S20 | Offline history | Airplane mode on, saved answers still readable | NFR5 |

**Capture conditions to record in Appendix D:** device model, API level 35, build commit `79c523a`, app version 1.0 (versionCode 1), whether a real API key was configured, and date.

### 7.2 Measured results (`PENDING`)

| Measurement | Method | Must record |
|---|---|---|
| Cold start → Ask screen | `adb shell am start -W`, 5 runs | min/median/max, device |
| AI round trip | `adb logcat -s GeminiAPI` timestamps, 5 questions | median, network type, model name |
| History open with N rows | On-screen timing with a seeded dataset | N, median |
| Submit actions | Manual count: tap field → type → tap submit | Count (target ≤3, NFR2) |
| Test suite runtime | `:app:testDebugUnitTest` wall clock | seconds |
| APK size | File size of `app-debug.apk` | 33.53 MB (already verified) |

### 7.3 GitHub / repository evidence (`HAVE`, to be screenshot for Appendix E)

| Evidence | Value |
|---|---|
| Repository | `https://github.com/mkhang-ship-it/AndroidStudio` |
| Branch | `main` @ `79c523a` |
| Commit count / span | 13 commits, 2026-09-30 → 2026-11-02 |
| Authors | mkhangsip (25), Le Quy Tam (9), NCnguyenn (1), trungvinh-52hz (1) |
| CI workflow | `.github/workflows/android-test.yml` — `./gradlew test` on push/PR to main |
| Absent (must be stated, not hidden) | No tags, no merge commits, no pull requests, no issues, ten sprints without entries |

Screens needed: commit graph, one representative diff, the CI workflow file, and an Actions run page (`PENDING` — public Actions history not inspected).

---

## 8. Dependency order and work sequence

```
Step 1  Recruit 3+ reviewers, issue Q01–Q13            [human, blocking]
Step 2  Collect responses, build Appendix B            [blocking]
Step 3  Write §1.2–1.4 and FB table                    [depends 1–2]
Step 4  Capture Pixel API 35 screenshots S01–S20        [blocking, needs AVD]
Step 5  Run measurement matrix + instrumented tests     [blocking, needs device]
Step 6  Write §2 (P5/M4)                                [depends 4]
Step 7  Write §3 with the 20-case matrix                [depends 4–5]
Step 8  Write §4 (M5)                                   [depends 6–7]
Step 9  Write §5 and the D2 justification               [depends 3, 8]
Step 10 Assemble appendices, Harvard references, PDF    [depends all]
```

Steps 4 and 5 can run in parallel with 1–3. Step 5 needs the instrumented suite executed once with a device attached so `39/39` becomes verified or corrected.

## 9. Acceptance gates

| Gate | Condition to pass |
|---|---|
| G1 Evidence integrity | Every factual sentence in the final report maps to an entry in `ASM2_EVIDENCE_CHECKLIST.md`, or is explicitly labelled as a limitation. |
| G2 P4 completeness | Questionnaire, participant table with real identities, raw responses, aggregation — all present. |
| G3 P5 completeness | APK installs on Pixel API 35; all 20 screenshots present; build + run instructions reproduce a working install. |
| G4 M4 strength | Each selected tool has activity-level evidence, not a name in a table. |
| G5 P6 integrity | ≥20 test cases with expected/actual; no "Met" without a result; "Not tested" used where appropriate. |
| G6 M5 depth | Risk table shows observed evidence and residual risk; testing critique names real coverage gaps. |
| G7 D2 honesty | Every adopted change has a commit + test; every rejection has a reason; no invented feedback. |
| G8 Format | English, Calibri 12, 1.5 spacing, margins 1.25/1/1/1 cm, page numbers, Harvard, PDF on CMS. |

## 10. Risks and open decisions

| ID | Risk | Impact | Mitigation |
|---|---|---|---|
| RK-01 | Reviewers not recruited in time | P4, M3, D2 all unverifiable | Start recruitment now; 3 is the minimum defensible sample |
| RK-02 | No API 35 system image installed | Screenshot gate fails | Install image and create AVD (checklist §2) |
| RK-03 | Instrumented suite never executed | `39/39` claim unusable; T6/T7 remain unproven | Run once with device; record XML |
| RK-04 | Doc numbers contradict each other (143 vs 152 vs 76) | Marker distrust | Use only re-measured figures, state source and date |
| RK-05 | Sprint log 10/14 empty | M4 methodology evidence weak | Fill retrospectively from git dates, marked as reconstructed |
| RK-06 | MCQ scoring may mis-grade | FR7 integrity | Verify manually with a real key; report honestly either way |
| **DEC-01** | Word limit conflict (2,000–2,500 vs 2,500–3,000) | Format marks | Ask tutor; default to Activity 2's figure and note it |
| **DEC-02** | Reuse android-37 screenshots or recapture on API 35 | Evidence authenticity | Recapture; interim use must be captioned with the real device |
| **DEC-03** | Claim "not implemented" vs "out of MVP scope" for 2FA, leaderboard, sync | P6 accuracy | Use Part 1's own MoSCoW line; label "deferred by design" |

---

## 11. Handoff to Pane_Assigment

**Draft first, in this order:**
1. **§2.1.1 + §2.1.2** — you can write these today from the verified code audit in §5; no external input needed.
2. **§2.3.1 + §2.3.2** — as-built architecture, data flow, security scope. Sources: `ServiceGraph.kt`, the four layer packages, `docs/uml/*.puml`.
3. **§2.2.2 + §2.5.1 + §2.5.2** — toolchain and methodology, with the verified versions in §1 and the tool→evidence pairs in §7.3.
4. **§2.4.1–§2.4.5** — feature descriptions; place screenshot references as `[S05]`…`[S19]` placeholders until captures exist.
5. **§3.3 matrix skeleton** — write all 20 TC rows with expected results now; leave "actual" blank until measurements land.
6. **§4.3 + §4.4** — development and testing critique; the coverage gaps listed in §5.3 are ready material.

**Evidence dependencies to wait on:** §1.2–1.4 (review data), §3.4 (measurements), Appendix B (responses), Appendix D (Pixel captures), §5.1–5.3 (feedback decisions).

**Blockers to escalate now:** RK-01 (no reviewers), RK-02 (no API 35 image), RK-03 (instrumented suite unexecuted), DEC-01 (word limit).

**Do not write:** any participant name, any Likert score, any measured latency, any claim that 39/39 instrumented tests pass, any statement that the app encrypts the database body, any assertion of AI answer accuracy.
