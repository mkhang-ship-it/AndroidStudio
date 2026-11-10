# ASM2 — Implementation Baseline and Report Plan

**Status:** DRAFT FOR AUDIT — planning/evidence document only. No report prose, no survey data.
**Prepared:** 2026-11-09 (implementation pane, worktree `mkhang-ship-it/Assigment`)
**Scope:** Unit 22 Application Development, Assignment Part 2 (P4, M3, P5, M4, P6, M5, D2).
**Platform for ASM2 evidence:** Pixel emulator, Android 15 (API 35), English report.
**Rule:** every non-verified item is marked **PENDING**. Do not promote a PENDING item to a fact without the artefact listed in `ASM2_EVIDENCE_LOG.md`.

> **Superseded baseline (2026-11-09).** Iterated evidence now exists: 16 authoritative real-final images, real Gemini ask/quiz HTTP 200, 159 JVM tests and 39/39 instrumented tests after the fix, plus cold start and Ask round-trip measurements. Use `ASM2_REPORT_COMPACT.md`, `FINAL_QA.md` and `REPORT_FINAL_QA.md` for current claims.

---

## 0. Truth rules for this baseline

1. A fact may cite only: a source file path, a test artefact, a command output captured in this document, a commit hash, or a real screenshot file.
2. "Implemented" means the code path exists and compiles. It does **not** mean the behaviour was observed on a device.
3. No peer-review participant, response, rating, screenshot, test result, GitHub action run, or performance number may be invented.
4. Existing project documents contain historical claims that no longer match the repository (see §8). The repository is the source of truth.
5. Pixel API 35 evidence was captured on 2026-11-09 (`real-final/`); this baseline predates it.

---

## 1. Assignment obligations (from the briefs)

Source: `C:\Users\ADMIN\orca\AndroidStudio\ASM\ASM\Assignment part 2.docx` and `Assignment_Part_2_Outline.docx`.

| Activity | Criterion | Deliverable | ASM2 owner section |
|---|---|---|---|
| 1 | P4 | Peer review of problem definition, proposed solution, development strategy; documented feedback | Activity 1 draft |
| 1 | M3 | Interpret peer-review feedback; identify previously unconsidered opportunities | Activity 1 draft |
| 2 | P5 | Functional business application + support documentation | Activity 2 draft + software package |
| 2 | M4 | Evidence of preferred tools, techniques, methodologies from a specific design document | Activity 2 draft |
| 2 | — | Written development review (~2,500–3,000 words), screenshots of development stages | Activity 2 draft |
| 3 | P6 | Review performance against problem definition and initial requirements | Evaluation section |
| 3 | M5 | Critical review of design, development and testing stages including risks | Critical review section |
| 3 | D2 | Justify improvements made and not made because of feedback; further development | Feedback decisions section |

Submission constraints from the brief: group report in the brief's wording (this submission is individual, so group and member fields are N/A), Calibri 12, 1.5 line spacing, margins left 1.25 cm / right 1 cm / top 1 cm / bottom 1 cm, page numbers, Harvard referencing, 2,000–2,500 words excluding tables/figures/appendices, front sheet 2, PDF upload.

---

## 2. Part 1 baseline that ASM2 must honour

Source: `BC00720_KhangNM_Assignment_Part1.pdf` (Section 4, M1) and `docs/PART1_M1_SOFTWARE_DESIGN.md`, `docs/PART1_M1_TEST_PLAN.md`.

### 2.1 Functional requirements (Part 1, Table 5)

| ID | Requirement | Priority (Part 1) | ASM2 status |
|---|---|---|---|
| FR1 | Register, sign in, protect access | Must | Implemented in code |
| FR2 | Onboarding: level, subjects, explanation style | Must | Implemented in code |
| FR3 | Submit text or image questions | Must | Implemented in code |
| FR4 | Generate structured AI answer | Must | Implemented in code |
| FR5 | Simplify, alternatives, key concepts, mistakes, follow-ups | Should | Implemented in code (on-demand) |
| FR6 | History, search, bookmark, subject filter | Must | Implemented in code |
| FR7 | Generate practice questions with feedback | Should | Implemented in code |
| FR8 | Track questions, subjects, accuracy, review time | Must | Implemented in code |
| FR9 | XP, levels, badges, leaderboard | Could | Partially implemented: no leaderboard |
| FR10 | Reminders, mistake alerts, summaries | Could | Partially implemented: in-app rows only |

### 2.2 Non-functional requirements (Part 1, §4.1)

| ID | Part 1 statement | As-built reality |
|---|---|---|
| NFR1 | UI ≤100 ms; progress during AI call | Loading UI and coroutines exist; cold start and Ask round trip measured, 100 ms UI-frame timing pending (superseded baseline) |
| NFR2 | Question submittable in ≤3 actions | UI supports it; **no usability measurement** |
| NFR3 | Android 13 (API 33)+ | `minSdk = 33`, `targetSdk = 37`; executed on API 35 (real-final) |
| NFR4 | Data encrypted at rest, minimised | Session and images encrypted; Room question/answer rows are **not** encrypted |
| NFR5 | Saved answers readable offline | Room persistence provides this; offline error/recovery observed; no queue or timing |
| NFR6 | Password strength, optional 2FA, lockout after 5 failures | Hash + lockout implemented; **no 2FA flow** (nullable DB column only) |

### 2.3 Part 1 test plan (T1–T10)

Part 1 defines T1–T10 with expected results. ASM2 evaluation should reuse these IDs; actual outcomes must be captured on Pixel API 35.

### 2.4 M1 / 20-test-case gate

Part 1 PDF Section 4 already contains the M1 software design document: OOAD, use-case/class/ER/activity views, coding and security design, test plan T1–T10 (Table 6), traceability matrix (Table 7), and implementation/deployment note. `docs/PART1_M1_TEST_PLAN.md` additionally contains a detailed TC catalogue.

**Decision:** the "at least 20 test cases" fallback is **not required** on current evidence. It remains a **conditional gate**: Pane_AUDIT must confirm that Part 1 M1 evidence is accepted. If audit rejects or partially accepts it, the fallback 20-case table must be produced with real executions.

---

## 3. Repository state (verified)

| Item | Value | Source |
|---|---|---|
| Worktree | `C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment` | session |
| Branch | `mkhang-ship-it/Assigment` (no upstream configured) | `git branch -vv` |
| HEAD | `79c523a` — "feat: đọc API key từ ai.properties để không bị Android Studio xoá" | `git log` |
| Working tree | clean except untracked `.commandcode/` | `git status --short --branch` |
| Commits | 10 commits, `4873ae5` (initial) → `79c523a`; last code commit 2026-11-02 15:48 +07 | `git log` |
| Remote `origin` | `https://github.com/mkhang-ship-it/AndroidStudio` | `git remote -v` |
| Remote `target` | `https://github.com/QuyTamIT/AIStudy.git` | `git remote -v` |
| Branches | `main` (tracks `origin/main`), `dev_khang` (tracks `target/dev_khang`), `mkhang-ship-it/AUDIT`, `mkhang-ship-it/Assigment` | `git branch -vv` |

**ASM2 caution:** the GitHub repository/CI evidence required by P5/M4 must show this history or a legitimate subset. Do not claim pushes, PRs, reviews or CI runs that are not visible in GitHub. GitHub proof is **PENDING**.

---

## 4. Build and test environment (verified)

| Item | Value |
|---|---|
| Project root | `AI Study/` (single Gradle module `:app`) |
| Gradle wrapper | 9.5.0 |
| AGP | 9.3.3 |
| Kotlin | 2.2.10 |
| KSP | 2.3.12 |
| Room | 2.7.2 |
| Compose BOM | 2026.02.01 |
| compileSdk / targetSdk | 37 / 37 |
| minSdk | 33 |
| Java source/target | 11 |
| Build JDK | Android Studio JBR (set by `build.ps1`); JDK on PATH is 1.8 and cannot run Gradle |
| Android SDK | `C:\Users\ADMIN\AppData\Local\Android\Sdk` (found via `ANDROID_HOME`; no `local.properties` in this worktree) |
| Build wrapper | `build.ps1` maps project to `R:` and serialises Gradle via a global mutex |
| Gradle settings | configuration cache on, `-Xmx2048m`, `android.overridePathCheck=true` |

### 4.1 Configuration files

| File | Fact |
|---|---|
| `AI Study/app/build.gradle.kts` | Reads `ai.properties` → `local.properties` → env `provider key`; injects `BuildConfig.provider key` and `GEMINI_MODEL`; model fallback `gemini-3.8-flash` |
| `AI Study/ai.properties.example` | Documents `provider key`, `GEMINI_MODEL=gemini-flash-lite-latest` |
| `AI Study/local.properties.example` | Documents `sdk.dir` only |
| `AI Study/.gitignore` | Ignores `/ai.properties`, `local.properties`, build dirs |
| `AI Study/app/src/main/AndroidManifest.xml` | Permissions: `INTERNET`, `ACCESS_NETWORK_STATE` only |
| `.github/workflows/android-test.yml` | Ubuntu, JDK 25, SDK 37, `./gradlew test`; connected tests excluded |

### 4.2 Actual results captured for this baseline

| # | Command (from `AI Study/` via `.\build.ps1`) | Result | Timestamp |
|---|---|---|---|
| B1 | `:app:testDebugUnitTest` | **BUILD SUCCESSFUL** in 1m 23s; 28 tasks | 2026-11-09 |
| B2 | `:app:assembleDebug` | **BUILD SUCCESSFUL** in 13s; APK 35,157,795 bytes | 2026-11-09 |
| B3 | `:app:compileDebugAndroidTestKotlin` | **BUILD SUCCESSFUL** in 6s; 2 warnings ("condition is always true") | 2026-11-09 |
| B4 | `adb devices` | **no devices attached** → no instrumented test executed | 2026-11-09 |

### 4.3 JVM test totals (from `app/build/test-results/testDebugUnitTest/*.xml`)

**12 classes, 152 tests, 0 failures, 0 errors, 0 skipped.**

| Test class | Cases |
|---|---|
| `LatexTest` | 19 |
| `QuestionSimilarityTest` | 33 |
| `GeminiParsingTest` | 27 |
| `SubjectDetectorTest` | 15 |
| `XpRulesTest` | 13 |
| `ReusePolicyTest` | 12 |
| `ConvertersTest` | 8 |
| `FakeAiClientTest` | 8 |
| `ProgressInsightsTest` | 7 |
| `AbuseFilterTest` | 5 |
| `TmpRegexCheckTest` | 4 |
| `ExampleUnitTest` | 1 |

**Not executed:** all `androidTest` classes (`AuthRepositoryTest`, `UserDaoTest`, `QuestionAnswerDaoTest`, `MainShellSmokeTest`, `ExampleInstrumentedTest`). Compilation is proven; pass status is **unproven**.

### 4.4 Build artefact state

- `AI Study/app/build/outputs/apk/debug/app-debug.apk` exists.
- Generated `BuildConfig.provider key` is `""` in this worktree: no `ai.properties`, no key in `local.properties`, no `provider key` env. Therefore this APK uses **`FakeAiClient`**, not Gemini.
- No credentials are printed in this document. Never copy a real key into the report, screenshots or evidence log.

---

## 5. As-built architecture and data flow (code facts)

### 5.1 Layers (single `:app` module)

```
com.aistudy.aistudy
├── domain/      models, enums, pure rules, AI abstraction (no Android UI)
├── data/        Room entities/DAOs, repository implementations, security stores
├── platform/    ServiceGraph + ServiceLocator (manual DI)
└── ui/          Compose screens, ViewModels, theme, navigation
```

- `AppApplication.onCreate()` → `ServiceLocator.init(this)` → `ServiceGraph.install(context)`.
- `AppDependencies.fromServiceLocator()` resolves repositories once and passes them down; ViewModels receive constructor injection.
- Navigation: `AppRoot` gate (`Loading` / `SignedOut` / `NeedsOnboarding` / `Ready`) based on session + `Profile.onboardedAt`; `MainShell` hosts five top-level tabs: Ask, History, Practice, Progress, Profile. Sub-routes: `question/{id}`, `notifications`, `notification/{id}`.

### 5.2 Data store

Room database `AppDatabase`, version 1, `exportSchema = true`, 7 entities:

| Table | Purpose | Key facts |
|---|---|---|
| `users` | account | unique email, PBKDF2 hash + salt, XP/level/streak, `twoFactorSecret` nullable |
| `profiles` | onboarding/preferences | unique `userId`, level, style, subject set, `onboardedAt` |
| `question_answers` | central question/answer record | hash, cache flags, image path/IV, answer + 5 enrichment fields, bookmark, review seconds, tokens |
| `quiz_items` | generated practice | nullable `isCorrect`, `sourceQuestionId` FK |
| `xp_events` | XP ledger | reason, amount, timestamp |
| `user_badges` | earned badges | composite key `(userId, badgeCode)` |
| `notifications` | in-app notifications | type, title, body, optional deep link, read flag |

No server, no sync, no account backend. All learning data is local to the device.

### 5.3 Ask → answer → history → progress flow

1. `AskScreen` collects text and/or image (`PickVisualMedia`).
2. `AskViewModel.submitQuestion()` validates non-empty and length ≤ `QuestionSimilarity.MAX_QUESTION_LENGTH` (2,000).
3. Image is encrypted by `AesGcmImageStore` into `filesDir/questions/` (AES-GCM-256, key in AndroidKeyStore); only path + IV are stored.
4. `QuestionRepositoryImpl.askInternal()`:
   - normalises text and computes SHA-256 (`QuestionSimilarity.hashOf`);
   - exact hash lookup (`findByHash`) → `QuestionAnswerDao.recentByUser(userId, 50)` → `ReusePolicy.select`;
   - `ReusePolicy` requires containment ≥0.85, Jaccard ≥0.6, equal key tokens, and only connector tokens differing;
   - hit → insert a new row with `servedFromCache = true`, `matchedQuestionId`, zero review time; **no AI call, no XP**;
   - miss → `GeminiAiClient.answer()` (or `FakeAiClient`), then insert record, award XP, create badge notification, optionally mistake alert.
5. `GeminiAiClient` uses `HttpURLConnection` + `org.json` against `https://generativelanguage.googleapis.com/v1beta`, 20 s timeouts, up to 2 retries for 429/5xx, `responseMimeType = application/json`, parses `steps[]` + `finalAnswer` into markdown, repairs LaTeX escapes, reads `usageMetadata` tokens.
6. History: `HistoryViewModel` observes `observeByUser`, dedupes by `questionHash`, filters by search/bookmark/subject, supports bookmark, delete (including duplicate hashes), and re-ask.
7. Progress: `ProgressRepositoryImpl.summary()` aggregates counts, review seconds, distinct subjects, quiz counts/accuracy, XP total and last 7 days, unread notifications; `ProgressInsights` derives offline study suggestions.
8. Notifications: `MainShell` calls `postDailySummaryIfDue()` once per day; notification rows are in-app only.

### 5.4 State and error model

- UI state: `MutableStateFlow` → `StateFlow` per screen ViewModel; Compose collects with lifecycle awareness.
- AI errors: `AiException.NotConfigured | Provider | Network | Malformed`, mapped to string resources and shown in an error banner. Input is retained on failure.
- Database/network work runs on `Dispatchers.IO`.
- `FakeAiClient` is a deterministic local implementation, not an empty stub.

---

## 6. Feature inventory (as-built)

Status vocabulary: **IMPLEMENTED** = code path exists and compiles; **PARTIAL** = limited scope or one sub-feature missing; **ABSENT** = not in code. Runtime column is **PENDING** for every row until captured on Pixel API 35.

### 6.1 The four MVP functions

| MVP function | Code map | Status | Runtime evidence |
|---|---|---|---|
| AI question submission | `ui/screens/ask/AskScreen.kt` (`AskViewModel.submitQuestion`), `data/repository/QuestionRepositoryImpl.kt` (`ask`, `askWithImage`, `askInternal`), `data/security/AesGcmImageStore.kt` | IMPLEMENTED | PENDING |
| AI answer generation | `domain/ai/GeminiAiClient.kt`, `domain/ai/AiClient.kt`, `domain/ai/AiError.kt`, `domain/ai/FakeAiClient.kt`, `QuestionRepositoryImpl` | IMPLEMENTED | PENDING (current APK = FakeAiClient) |
| Question history | `data/db/dao/QuestionAnswerDao.kt`, `data/repository/QuestionRepositoryImpl.kt`, `ui/history/HistoryViewModel.kt`, `ui/screens/history/HistoryScreen.kt`, `ui/questiondetail/QuestionDetailScreen.kt` | IMPLEMENTED | PENDING |
| Basic progress tracking | `data/repository/ProgressRepositoryImpl.kt`, `domain/rules/ProgressInsights.kt`, `ui/progress/ProgressViewModel.kt`, `ui/screens/progress/ProgressScreen.kt` | IMPLEMENTED | PENDING |

### 6.2 Supporting and non-MVP features

| Area | Code map | Status | Notes / limits |
|---|---|---|---|
| Email registration / login / logout | `data/repository/AuthRepositoryImpl.kt`, `ui/auth/AuthScreens.kt`, `ui/root/AuthGateViewModel.kt` | IMPLEMENTED | email + password only; regex validation, min 8 chars, letter+digit |
| Account deletion / data clear | `AuthRepositoryImpl.deleteAccount`, `AppDatabase.clearUserData` | IMPLEMENTED | transaction: clear user data then delete user; profile cascades |
| Onboarding and profile edit | `ProfileRepositoryImpl`, `ui/onboarding/OnboardingScreen.kt`, `ui/profile/ProfileScreen.kt` | IMPLEMENTED | level, subject set, explanation style |
| Password security | `data/security/PasswordHasher.kt` | IMPLEMENTED | PBKDF2-HMAC-SHA256, 120,000 iterations, 16-byte salt, 256-bit, constant-time compare |
| Session security | `EncryptedPrefsSessionStore.kt` | IMPLEMENTED | EncryptedSharedPreferences; stores user id + timestamp only |
| Login lockout | `data/security/LoginThrottle.kt` | IMPLEMENTED | 5 failures → 5-minute lock, persisted in SharedPreferences |
| 2FA | `User.twoFactorSecret`, `UserDao.setTwoFactorSecret` | ABSENT (flow) | column/DAO only; no enrolment, verification or UI |
| Question image handling | `AskScreen` picker, `AesGcmImageStore` | PARTIAL | encrypted at rest; MIME hardcoded `image/jpeg`; no resize/compression/size cap; stored image not displayed in history/detail |
| Notifications | `NotificationRepositoryImpl`, `ui/notifications/*`, `MainShell` | PARTIAL | in-app rows + daily summary on app open; no OS notification, no scheduled reminders, no FCM |
| Practice quiz | `QuizRepositoryImpl`, `ui/practice/PracticeViewModel.kt`, `ui/screens/practice/PracticeScreen.kt` | IMPLEMENTED | 1–20 questions; MULTIPLE_CHOICE / SHORT_ANSWER / FILL_BLANK; immediate feedback |
| Gamification | `GamificationRepositoryImpl`, `XpRules.kt`, `BadgeCatalog.kt` | PARTIAL | XP, level, streak, badges; no leaderboard screen |
| Caching / reuse | `QuestionSimilarity.kt`, `ReusePolicy.kt`, `QuestionAnswerDao.findByHash` | IMPLEMENTED | exact hash + fuzzy reuse + subject guard; 50-row window |
| Offline read | Room persistence | PARTIAL | saved data readable offline; **no connectivity observer, no pending-sync queue** (see L-02) |
| Error states | `AiError.kt`, screen `UiState.errorMessage`, `ui/common/Common.kt` (`ErrorBanner`) | IMPLEMENTED | 4 typed AI errors; retries inside client; no global connectivity banner (strings `offline_banner_*` exist but are unreferenced) |
| Content abuse filter | `domain/rules/AbuseFilter.kt` + `AbuseFilterTest` | PARTIAL | rule set + tests exist, but **not called** from ask/quiz flow (see L-04) |
| Privacy controls | `clearUserData`, manifest permissions | PARTIAL | no export; no encryption of Room text; no data policy screen |
| Accessibility | Compose semantics; mascot is Canvas decoration | PARTIAL | no recorded accessibility test; mascot has no content description |

### 6.3 Screens present

`ui/auth/AuthScreens.kt` (welcome/login/register), `ui/onboarding/OnboardingScreen.kt`, `ui/screens/ask/AskScreen.kt`, `ui/screens/history/HistoryScreen.kt`, `ui/questiondetail/QuestionDetailScreen.kt`, `ui/screens/practice/PracticeScreen.kt`, `ui/screens/progress/ProgressScreen.kt`, `ui/screens/profile/ProfileScreen.kt`, `ui/screens/notifications/*` (list, detail), `ui/shell/MainShell.kt`.

---

## 7. ASM2 traceability skeleton (to be filled with Pixel evidence)

This is the structure for P6. Do **not** mark "Met" without an executed test.

| Req | Design element (Part 1) | Code map | Planned test | Result | Evidence |
|---|---|---|---|---|---|
| FR1 | User, AuthRepository, hasher, session | auth repositories/screens | TC-AUTH-01/02/11 (adapted), T4/T6 | PENDING | EV-P-01 |
| FR2 | Profile, level, style | profile/onboarding | TC-ONB-01/02 | PENDING | EV-P-02 |
| FR3 | QuestionAnswer, ImageStore, AskScreen | ask + image store | TC-ASK-05/07/08/09 | PENDING | EV-P-03 |
| FR4 | AiClient, GeminiAiClient | AI client + repository | TC-ANS-01/03 | PENDING | EV-P-04 |
| FR5 | Answer fields, detail screen | improve() + detail UI | TC-ANS-04/05/06 | PENDING | EV-P-05 |
| FR6 | DAO, History screen | history stack | TC-HIS-01/02/03/05 | PENDING | EV-P-06 |
| FR7 | QuizItem, Practice screen | quiz stack | TC-QUI-01/04/07 | PENDING | EV-P-07 |
| FR8 | ProgressRepository, Insights | progress stack | TC-PRO-01/05 | PENDING | EV-P-08 |
| FR9 | XP, badges, local leaderboard | gamification | TC-GAM-02/09 | PENDING | EV-P-09 |
| FR10 | Notification, repository | notifications | TC-NOT-01/04/05 | PENDING | EV-P-10 |
| NFR1 | coroutines/Flow | all screens | T10 / TC-NFR-01/02 | PENDING | EV-P-11 |
| NFR2 | Compose UI | ask screen | TC-NFR-07 / heuristic review | PENDING | EV-P-12 |
| NFR3 | minSdk 33 | Gradle config | Pixel API 35 run | PENDING | EV-P-13 |
| NFR4 | encrypted stores | security package | TC-SEC-01/05 | PENDING (partial by inspection) | EV-P-14 |
| NFR5 | Room | history/detail | TC-OFF-02 | PENDING | EV-P-15 |
| NFR6 | hasher, throttle, 2FA | auth security | TC-SEC-06/07 | PENDING (2FA absent) | EV-P-16 |

---

## 8. Known limitations and discrepancies (record honestly)

| ID | Finding | Evidence | Consequence for report |
|---|---|---|---|
| L-01 | Test-count claims in existing docs are stale | Part 1 PDF says 120 tests; `docs/IMPROVEMENT_LOG.md` says 143; `docs/TEST_REPORT_NP5.md` says 76 (2026-10-15); baseline run = 152 in 12 classes; after the practice fix the current run is **159 in 13 classes** | Use 159 with date and XML source; do not reuse old numbers |
| L-02 | No offline queue / connectivity observer | grep for `ConnectivityManager`, `WorkManager`, `pending` in `app/src/main` returns nothing; Part 1 §4.4 and NFR5 describe a pending queue | ASM2 must report partial offline support, not the designed behaviour |
| L-03 | Room text is not encrypted | `QuestionAnswer` stores `questionText`/`answerText` as plain columns; only session and images are encrypted | NFR4 "encrypted at rest" is partial; `TC-SEC-02` would fail as written |
| L-04 | `AbuseFilter` is not wired | Only `AbuseFilterTest` references it; ask/quiz flow never calls `isBlocked` | `TC-SEC-08` unproven; either test as absent or wire later (out of this scope) |
| L-05 | 2FA absent | Only nullable column + DAO setter | NFR6 partial; do not claim 2FA |
| L-06 | Leaderboard absent | grep for `leaderboard` in `app/src/main` returns no code | FR9 partial |
| L-07 | No OS-level notifications/reminders | `NotificationRepositoryImpl` writes DB rows; no `NotificationManager`, WorkManager or AlarmManager usage | FR10 partial; "reminders" wording must stay factual |
| L-08 | Image MIME fixed to `image/jpeg`; no downscale or 5 MB cap | `GeminiAiClient.answer` sets `mime_type = "image/jpeg"`; `AesGcmImageStore.store` reads whole stream | `TC-ASK-10` unproven; risk for large/PNG/HEIC images |
| L-09 | Instrumented tests never ran here | `adb devices` empty; only compilation proven | T6/T7 pass status unproven until Pixel run |
| L-10 | Debug APK has no API key in this worktree | Generated `BuildConfig provider key = empty` | Current build demonstrably uses `FakeAiClient`; real-Gemini evidence needs a key supplied outside git |
| L-11 | Default model fallback `gemini-3.8-flash` is not a documented public Gemini model | `app/build.gradle.kts` (`geminiModel()`), `GeminiAiClient.DEFAULT_MODEL`; example file uses `gemini-flash-lite-latest` | If a key is added without `GEMINI_MODEL`, calls may fail. Flag as deployment risk; do not edit code in ASM2 scope |
| L-12 | Temporary test class committed | `app/src/test/.../TmpRegexCheckTest.kt` (4 cases) | Legitimate test results, but weak presentation; disclose or exclude from headline count |
| L-13 | Existing screenshots are not Pixel API 35 evidence | `docs/screenshots/*.png` (11 files); existing docs reference `Medium_Phone`; no device/API metadata | All ASM2 screenshots must be recaptured or explicitly marked as pre-existing |
| L-14 | CI workflow existence ≠ CI run evidence | `.github/workflows/android-test.yml` present; no run log captured | GitHub Actions proof PENDING |
| L-15 | Docs/README describe features more broadly than P6 may imply | README lists all feature groups without status qualifiers | Report must distinguish implemented vs partial exactly as §6 |

**Do not "fix" any of the above in app code during ASM2.** They are evaluation inputs.

---

## 9. Evidence production plan (Pixel API 35, English report)

### 9.1 Device/run prerequisites (all PENDING)

| Item | Required | Status |
|---|---|---|
| AVD name and image | Pixel, Android 15 (API 35), Google APIs | PENDING — record exact AVD id |
| Device/API proof screenshot | Settings → About phone showing Pixel + Android 15 | PENDING |
| Install proof | `adb install -r app-debug.apk` output | PENDING |
| Demo account | a real test account created on device; credentials controlled by team | PENDING |
| AI mode | either real key via `ai.properties` (never committed) or explicitly labelled `FakeAiClient` demo | PENDING — decide |
| Instrumented test run | `:app:connectedDebugAndroidTest` on Pixel API 35 | PENDING |
| Logcat capture | `adb logcat -s GeminiAPI` for one real call (if key used) | PENDING |

### 9.2 Screenshot slots (must be captured, not reused blindly)

| Slot | Screen / state | Existing file (reference only) | New Pixel 35 file |
|---|---|---|---|
| S-01 | Welcome/login | — | `evidence/pixel35/01_login.png` |
| S-02 | Register + validation error | — | `evidence/pixel35/02_register.png` |
| S-03 | Onboarding (3 choices) | `08_onboarding.png` | `evidence/pixel35/03_onboarding.png` |
| S-04 | Ask: empty + suggestion chips | `01_ask.png` | `evidence/pixel35/04_ask.png` |
| S-05 | Ask: loading state | — | `evidence/pixel35/05_ask_loading.png` |
| S-06 | Ask: AI answer with steps/formula | `03_answer_math.png` | `evidence/pixel35/06_answer.png` |
| S-07 | Ask: cached answer badge | — | `evidence/pixel35/07_cache_hit.png` |
| S-08 | Ask: network error banner | — | `evidence/pixel35/08_error.png` |
| S-09 | History: list, newest first | `02_history.png` | `evidence/pixel35/09_history.png` |
| S-10 | History: search + subject filter + bookmark | — | `evidence/pixel35/10_history_filter.png` |
| S-11 | Question detail + enrichment actions | `03b_answer_math_formula.png` | `evidence/pixel35/11_detail.png` |
| S-12 | Practice: generated questions | `05_practice.png` | `evidence/pixel35/12_practice.png` |
| S-13 | Practice: feedback correct/incorrect | `06_practice_feedback.png` | `evidence/pixel35/13_practice_feedback.png` |
| S-14 | Progress dashboard | `04_progress.png` | `evidence/pixel35/14_progress.png` |
| S-15 | Progress: empty state | — | `evidence/pixel35/15_progress_empty.png` |
| S-16 | Profile/settings | `07_profile.png` | `evidence/pixel35/16_profile.png` |
| S-17 | Notifications list + unread badge | `09_notifications.png` | `evidence/pixel35/17_notifications.png` |
| S-18 | Notification detail | `10_notification_detail.png` | `evidence/pixel35/18_notification_detail.png` |
| S-19 | Offline read of saved answer | — | `evidence/pixel35/19_offline_read.png` |
| S-20 | Persistence after kill/relaunch | — | `evidence/pixel35/20_persistence.png` |

Capture rules: include system status bar/time; show the app in English; one claim per screenshot; never include an API key, password, or personal data; record capture date and device in the evidence log.

### 9.3 Development-process evidence (P5/M4)

| Evidence | How to obtain | Status |
|---|---|---|
| Commit history | `git log --oneline --graph` screenshot or exported text | READY (text captured) |
| GitHub repository page | browser screenshot of commits/branches/remotes | PENDING |
| CI run | GitHub Actions run page for `android-test.yml` | PENDING |
| IDE/tool use | Android Studio project + build/run screenshots | PENDING |
| Methodology | real sprint/iteration record (`docs/AGILE_SPRINT_LOG.md` exists; verify its content) | PENDING (audit) |
| Bug/fix log | `docs/BUG_LIST_NP5.md`, `docs/IMPROVEMENT_LOG.md` | READY (exists; must be verified as real) |
| Test reports | Gradle XML/HTML reports | READY (XML exists; HTML report path) |

---

## 10. Support documentation inputs (P5)

| Support artefact | Existing input | Action for ASM2 |
|---|---|---|
| Source package | This worktree | Freeze version; record commit `79c523a` |
| Environment guide | `AI Study/CONTRIBUTING.md`, `README.md`, `local.properties.example`, `ai.properties.example` | Verify English version and accuracy |
| Build/run guide | README commands; `build.ps1` | Record exact verified commands and JDK/SDK requirements |
| API/architecture note | `docs/PART1_M1_SOFTWARE_DESIGN.md`, `docs/uml/*` | Reconcile as-built differences (L-02…L-07) |
| User guide | Screens exist; no separate user guide found | PENDING — produce short English user guide |
| Test guide | `docs/PART1_M1_TEST_PLAN.md`, `docs/TEST_REPORT_NP5.md` | Update with real ASM2 runs |
| Demo account guidance | None | PENDING — must be created on device |
| Release/version note | `versionName = "1.0"`, `versionCode = 1` | Record APK hash and build date |
| Known limitations | §8 of this file | Reuse verbatim in report evaluation |

---

## 11. Report execution order (draft)

### Phase 0 — Baseline (this document)

Done when this file and `ASM2_EVIDENCE_LOG.md` exist and facts are traceable.

### Phase A — Freeze and verify repo facts

- Confirm commit, remotes, branch, clean tree.
- Capture GitHub repository, commit list, branches and CI run proof.
- Freeze APK and record SHA-256.
- Output: EV-001…EV-020 populated; no PENDING in the repository-facts group.

### Phase B — Pixel API 35 runtime capture (blocking for later phases)

- Create/confirm AVD; capture device proof.
- Install APK; create demo account; run the scripted scenario list (S-01…S-20).
- Run `:app:connectedDebugAndroidTest`; capture XML.
- If a real Gemini key is used, capture logcat and remove secrets from screenshots.
- Output: all runtime PENDING items resolved or explicitly marked NOT TESTED with reason.

### Phase C — Activity 1 (P4 + M3)

- Finalise the questionnaire (template exists in the outline; questions 1–9 Likert, 10–12 open).
- Recruit real participants; record names, roles, descriptions and consent.
- Tabulate raw responses; store anonymised raw data.
- Write analysis only from real responses; if the sample is small, state that limitation.
- Output: survey appendix, results table/chart, feedback IDs, refinement decisions.

**Gate C:** no participant data → Activity 1 cannot be finalised. Do not generate placeholder participants.

### Phase D — Activity 2 (P5 + M4)

- Use §5 (architecture), §6 (features), Phase B screenshots, Phase A tool evidence.
- Write development narrative: tools, methodology, stages, difficulties, fixes (from real commit/bug logs).
- Include requirement-to-code mapping and deviations.
- Output: implementation chapter + software package + support documentation.

### Phase E — P6 evaluation

- Apply the 20-test-case gate decision (§2.4). If audit requires it, execute and record 20 cases; otherwise use Part 1 T1–T10 + ASM2 scenarios.
- Fill §7 traceability with actual results.
- Evaluate against problem definition and initial requirements, including partial/absent items.
- Output: evaluation chapter with evidence citations.

### Phase F — Activity 3 (M5 + D2)

- Critical review of design, development and testing; risk register against real events.
- Feedback-to-change table: adopted / not adopted / deferred, with justification.
- Use only real feedback IDs from Phase C.
- Output: critical review + future improvements.

### Phase G — Assembly and submission

- Apply formatting rules; check word counts per section.
- Harvard references; figure/table numbering; front sheet 2.
- Convert to PDF; verify page count, fonts, margins, screenshot legibility.
- Final consistency check: every claim in the report must resolve to an EV ID.

---

## 12. Open decisions / questions for coordinator and Pane_AUDIT

| # | Question | Why it matters |
|---|---|---|
| Q-01 | Confirm Part 1 M1 is accepted, so the 20-test-case fallback is not required | Changes Phase E workload and report structure |
| Q-02 | Confirm whether ASM2 demo uses a real Gemini key or `FakeAiClient` only | Determines whether real AI evidence can be claimed |
| Q-03 | Confirm Pixel API 35 AVD name/image and who captures screenshots | All runtime evidence depends on it |
| Q-04 | Confirm demo account ownership and credential storage | Needed for reproducibility without leaking secrets |
| Q-05 | Confirm whether historical `docs/screenshots/*.png` may be used at all, or only new Pixel 35 captures | Affects Activity 2 evidence |
| Q-06 | Confirm peer-review recruitment plan and participant identities | Activity 1 P4/M3 cannot be written without real data |
| Q-07 | Confirm report ownership split between panes | Avoid duplicated or contradictory prose |
| Q-08 | Confirm target submission date and whether GitHub history must be pushed to `target` | P5/M4 tool evidence |

---

## Appendix A — File/module map

| Path | Role |
|---|---|
| `AI Study/app/src/main/java/com/aistudy/aistudy/MainActivity.kt` | single activity host |
| `.../AppApplication.kt` | initialises DI |
| `.../platform/ServiceGraph.kt` | concrete dependency wiring |
| `.../platform/ServiceLocator.kt` | global service access for image store only |
| `.../ui/root/AppRoot.kt` | auth/onboarding gate |
| `.../ui/root/AuthGateViewModel.kt` | gate state |
| `.../ui/root/AppDependencies.kt` | composition-root dependency bundle |
| `.../ui/shell/MainShell.kt` | bottom navigation + daily summary |
| `.../ui/shell/AppNavHost.kt` | route graph |
| `.../ui/shell/Destinations.kt` | routes + tab navigation rules |
| `.../ui/screens/ask/AskScreen.kt` | ask UI + `AskViewModel` |
| `.../ui/screens/history/HistoryScreen.kt` | history UI |
| `.../ui/history/HistoryViewModel.kt` | filter/search/bookmark/delete/re-ask |
| `.../ui/questiondetail/QuestionDetailScreen.kt` | answer detail + review timer |
| `.../ui/screens/practice/PracticeScreen.kt` | practice UI |
| `.../ui/practice/PracticeViewModel.kt` | quiz generation/answer state |
| `.../ui/screens/progress/ProgressScreen.kt` | dashboard UI |
| `.../ui/progress/ProgressViewModel.kt` | summary loading |
| `.../ui/screens/profile/ProfileScreen.kt` | profile/settings |
| `.../ui/profile/ProfileViewModel.kt` | profile updates/logout/delete |
| `.../ui/notifications/*` | notification list/detail |
| `.../ui/onboarding/OnboardingScreen.kt` | onboarding |
| `.../ui/auth/AuthScreens.kt` | welcome/login/register |
| `.../domain/ai/GeminiAiClient.kt` | Gemini REST client + parsing |
| `.../domain/ai/FakeAiClient.kt` | offline AI implementation |
| `.../domain/ai/AiClient.kt`, `AiError.kt` | AI contract/errors |
| `.../domain/rules/QuestionSimilarity.kt` | normalisation/hash/similarity |
| `.../domain/rules/ReusePolicy.kt` | cache reuse decision |
| `.../domain/rules/XpRules.kt`, `BadgeCatalog.kt`, `ProgressInsights.kt`, `SubjectDetector.kt`, `AbuseFilter.kt` | pure rules |
| `.../data/repository/*` | repository interfaces/implementations |
| `.../data/db/AppDatabase.kt`, `dao/*`, `Converters.kt` | Room layer |
| `.../data/security/*` | hashing, session, throttle, image encryption |
| `AI Study/app/src/test/**` | 12 JVM test classes |
| `AI Study/app/src/androidTest/**` | 5 instrumented classes (compile only, not executed here) |
| `AI Study/app/schemas/` | exported Room schema |
| `docs/` | design, test plan, improvement log, UML, screenshots |
| `.github/workflows/android-test.yml` | CI unit-test workflow |

## Appendix B — Commands actually run for this baseline

```powershell
git status --short --branch
git log --oneline -15
git remote -v
git branch -vv
adb devices
$env:ANDROID_HOME='C:\Users\ADMIN\AppData\Local\Android\Sdk'
$env:ANDROID_SDK_ROOT='C:\Users\ADMIN\AppData\Local\Android\Sdk'
.\build.ps1 :app:testDebugUnitTest
.\build.ps1 :app:assembleDebug
.\build.ps1 :app:compileDebugAndroidTestKotlin
```

Result files:

- `AI Study/app/build/test-results/testDebugUnitTest/*.xml` (12 files, 152 cases)
- `AI Study/app/build/outputs/apk/debug/app-debug.apk`
- `AI Study/app/build/generated/source/buildConfig/debug/com/aistudy/aistudy/BuildConfig.java`

## Appendix C — Environment gaps observed

| Gap | Impact |
|---|---|
| No `local.properties` in worktree | Build relies on `ANDROID_HOME` set per command |
| `java` on PATH is 1.8 | Direct `gradlew` fails; must use `build.ps1` (sets JBR) or set `JAVA_HOME` |
| No Android device/emulator attached | No runtime/instrumented evidence yet |
| No `ai.properties` | Current APK uses `FakeAiClient` |
| Non-ASCII project path | Requires `build.ps1` `R:` mapping / `android.overridePathCheck` |
