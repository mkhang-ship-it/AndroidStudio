# ASM2 — Evidence Log

**Purpose:** single index of every claim ASM2 will make. If a claim is not in this file with status `VERIFIED`, it cannot appear in the report.
**Baseline date:** 2026-11-09
**Branch / commit:** `mkhang-ship-it/Assigment` / `79c523a`
**Device target:** Pixel emulator, Android 15 (API 35) — **not yet captured**
**Companion document:** `ASM2_IMPLEMENTATION_BASELINE.md`

---

## 1. Status values

| Status | Meaning |
|---|---|
| `VERIFIED` | Artefact exists and was inspected/captured in this session, or command output is recorded below |
| `PENDING` | Not yet obtained; must not be used as a report claim |
| `BLOCKED` | Cannot be obtained without a decision/resource (e.g. participant data) |
| `NOT APPLICABLE` | Feature absent by design; record the absence explicitly |

**Never use:** "assumed", "should be", "probably", "expected to pass" in the report.

---

## 2. Evidence ID conventions

| Prefix | Category |
|---|---|
| `EV-0xx` | Repository, build, test, source facts (verified now) |
| `EV-1xx` | Pixel API 35 runtime evidence |
| `EV-2xx` | Screenshots |
| `EV-3xx` | Tooling, methodology, GitHub, CI |
| `EV-4xx` | Peer review (P4/M3) |
| `EV-5xx` | Support documentation, presentation, submission packaging |

---

## 3. Verified evidence (this baseline)

| ID | Claim / fact | Type | Source / exact value | Captured | Status |
|---|---|---|---|---|---|
| EV-001 | Worktree is on `mkhang-ship-it/Assigment`, HEAD `79c523a`, tree clean except untracked `.commandcode/` | command | `git status --short --branch` → `## mkhang-ship-it/Assigment` + `?? .commandcode/` | 2026-11-09 | VERIFIED |
| EV-002 | 10 commits, initial `4873ae5`, latest `79c523a` (2026-11-02 15:48:43 +07) | command | `git log --oneline -15`, `git log -5 --format=...` | 2026-11-09 | VERIFIED |
| EV-003 | Remotes: `origin` = github.com/mkhang-ship-it/AndroidStudio; `target` = github.com/QuyTamIT/AIStudy.git | command | `git remote -v` | 2026-11-09 | VERIFIED |
| EV-004 | `dev_khang` tracks `target/dev_khang`; `main` tracks `origin/main`; `mkhang-ship-it/Assigment` has no upstream | command | `git branch -vv` | 2026-11-09 | VERIFIED |
| EV-005 | JVM unit tests pass: 12 classes, 152 tests, 0 failures, 0 errors, 0 skipped | command + XML | `.\build.ps1 :app:testDebugUnitTest` → BUILD SUCCESSFUL in 1m 23s; totals parsed from `AI Study/app/build/test-results/testDebugUnitTest/*.xml` | 2026-11-09 | VERIFIED |
| EV-006 | Per-class test counts (Latex 19, Similarity 33, GeminiParsing 27, SubjectDetector 15, XpRules 13, ReusePolicy 12, Converters 8, FakeAiClient 8, ProgressInsights 7, AbuseFilter 5, TmpRegexCheck 4, ExampleUnitTest 1) | XML | same directory as EV-005 | 2026-11-09 | VERIFIED |
| EV-007 | Debug APK builds and exists (35,157,795 bytes) | command + file | `.\build.ps1 :app:assembleDebug` → BUILD SUCCESSFUL in 13s; `AI Study/app/build/outputs/apk/debug/app-debug.apk` | 2026-11-09 | VERIFIED |
| EV-008 | Instrumented test sources compile (2 warnings: "Condition is always 'true'" in `MainShellSmokeTest.kt:88,93`) | command | `.\build.ps1 :app:compileDebugAndroidTestKotlin` → BUILD SUCCESSFUL in 6s | 2026-11-09 | VERIFIED |
| EV-009 | No Android device/emulator attached; instrumented tests not executed | command | `adb devices` → empty list | 2026-11-09 | VERIFIED (negative) |
| EV-010 | Debug build has no Gemini key: generated `BuildConfig provider key = empty`; therefore runtime uses `FakeAiClient` | file | `AI Study/app/build/generated/source/buildConfig/debug/com/aistudy/aistudy/BuildConfig.java` | 2026-11-09 | VERIFIED |
| EV-011 | Build reads key from `ai.properties` → `local.properties` → env; real files are gitignored | source | `AI Study/app/build.gradle.kts`; `AI Study/.gitignore`; `ai.properties.example` | 2026-11-09 | VERIFIED |
| EV-012 | Toolchain: Gradle 9.5.0, AGP 9.3.3, Kotlin 2.2.10, KSP 2.3.12, Room 2.7.2, Compose BOM 2026.02.01, compileSdk/targetSdk 37, minSdk 33, Java 11 | source | `gradle/wrapper/gradle-wrapper.properties`, `gradle/libs.versions.toml`, `app/build.gradle.kts` | 2026-11-09 | VERIFIED |
| EV-013 | Single `:app` module, four source layers (domain/data/platform/ui) | source | `settings.gradle.kts`, package tree under `app/src/main/java/com/aistudy/aistudy` | 2026-11-09 | VERIFIED |
| EV-014 | 7 Room tables, schema version 1, `exportSchema = true` | source | `data/db/AppDatabase.kt` | 2026-11-09 | VERIFIED |
| EV-015 | Ask flow: validation → hash → exact cache → 50-row fuzzy reuse → AI → insert → XP/notifications | source | `AskScreen.kt`, `QuestionRepositoryImpl.kt`, `ReusePolicy.kt`, `QuestionSimilarity.kt` | 2026-11-09 | VERIFIED (code inspection) |
| EV-016 | Gemini client: REST `HttpURLConnection`, `v1beta`, 20 s timeouts, retries 429/5xx, JSON `steps`+`finalAnswer`, token usage parsing | source | `domain/ai/GeminiAiClient.kt` | 2026-11-09 | VERIFIED (code inspection) |
| EV-017 | Security: PBKDF2-HMAC-SHA256 120,000 iterations / 16-byte salt / 256-bit; EncryptedSharedPreferences session; AES-GCM-256 images with Keystore key; lockout 5 attempts / 5 minutes persisted | source | `PasswordHasher.kt`, `EncryptedPrefsSessionStore.kt`, `AesGcmImageStore.kt`, `LoginThrottle.kt`, `AuthRepositoryImpl.kt` | 2026-11-09 | VERIFIED (code inspection) |
| EV-018 | Room question/answer text is not encrypted | source | `QuestionAnswer.kt` columns `questionText`, `answerText`; no crypto wrapper | 2026-11-09 | VERIFIED |
| EV-019 | No 2FA flow; only nullable `twoFactorSecret` column and DAO setter | source | `User.kt`, `UserDao.kt` | 2026-11-09 | VERIFIED |
| EV-020 | No connectivity observer, WorkManager or pending-sync queue in app code | search | grep `ConnectivityManager|NetworkCapabilities|WorkManager|pending` over `app/src/main` → no implementation hits | 2026-11-09 | VERIFIED (negative) |
| EV-021 | `AbuseFilter` rules are tested but never called from the ask/quiz flow | search | only `AbuseFilter.kt` and `AbuseFilterTest.kt` reference it | 2026-11-09 | VERIFIED |
| EV-022 | No leaderboard implementation | search | grep `leaderboard` over `app/src/main` → no hits | 2026-11-09 | VERIFIED (negative) |
| EV-023 | Image MIME hardcoded `image/jpeg`; no resize/compression/size cap | source | `GeminiAiClient.answer()`, `AesGcmImageStore.store()` | 2026-11-09 | VERIFIED |
| EV-024 | Notifications are in-app DB rows only; daily summary posted on `MainShell` launch | source | `NotificationRepositoryImpl.kt`, `MainShell.kt` | 2026-11-09 | VERIFIED |
| EV-025 | Existing screenshots: 11 PNGs in `docs/screenshots/` (01–10, including `03b`) | files | directory listing | 2026-11-09 | VERIFIED |
| EV-026 | CI workflow exists: `.github/workflows/android-test.yml` runs `./gradlew test` on Ubuntu/JDK 25/SDK 37 | file | workflow file | 2026-11-09 | VERIFIED (file exists) |
| EV-027 | Historical docs contain stale test counts (Part 1: 120; IMPROVEMENT_LOG: 143; TEST_REPORT_NP5: 76) | documents | Part 1 PDF §4.6; `docs/IMPROVEMENT_LOG.md`; `docs/TEST_REPORT_NP5.md` | 2026-11-09 | VERIFIED |
| EV-028 | Part 1 M1 evidence exists (Section 4: OOAD, UML views, test plan T1–T10, traceability Table 7, deployment note) | document | `BC00720_KhangNM_Assignment_Part1.pdf`, §4.1–4.8 | 2026-11-09 | VERIFIED |
| EV-029 | Existing project docs present: design, M1 test plan, bug list, improvement log, roadmap, UML, tools | files | `docs/` listing | 2026-11-09 | VERIFIED |
| EV-030 | Temporary test class `TmpRegexCheckTest` (4 cases) is committed | source | `app/src/test/java/com/aistudy/aistudy/TmpRegexCheckTest.kt` | 2026-11-09 | VERIFIED |

---

## 4. Pending runtime evidence (Pixel API 35)

| ID | Claim to support | Capture method | Slot / artefact | Status |
|---|---|---|---|---|
| EV-101 | App runs on Pixel, Android 15 (API 35) | Settings → About phone screenshot; record AVD id | `evidence/pixel35/device_info.png` | PENDING |
| EV-102 | Debug APK installs on Pixel API 35 | `adb install -r app-debug.apk`; copy output | `evidence/logs/install.txt` | PENDING |
| EV-103 | Demo account exists and signs in | create account on device; controlled credentials | `evidence/logs/demo_account.md` (no real password in repo if policy forbids) | PENDING |
| EV-104 | Instrumented suites pass on API 35 | `.\build.ps1 :app:connectedDebugAndroidTest`; copy XML | `evidence/test-results/androidTest/` | PENDING |
| EV-105 | AI mode used for demo is explicit | real Gemini key via local `ai.properties`, or record `FakeAiClient` banner | `evidence/logs/ai_mode.md` | PENDING |
| EV-106 | Real AI call succeeds (only if EV-105 = real key) | `adb logcat -s GeminiAPI`; redact key/base64 | `evidence/logs/gemini_call.txt` | PENDING |
| EV-107 | Question persists after app kill/relaunch | force-stop, relaunch, open History | `evidence/screenshots/20_persistence.png` | PENDING |
| EV-108 | Saved answer readable in aeroplane mode | enable aeroplane mode, open saved item | `evidence/screenshots/19_offline_read.png` + log | PENDING |
| EV-109 | New question while offline fails gracefully and keeps input | aeroplane mode, submit, capture banner | `evidence/screenshots/08_error.png` + steps | PENDING |
| EV-110 | Cache hit reuses answer without API cost | submit same question twice; capture cached badge | `evidence/screenshots/07_cache_hit.png` + steps | PENDING |
| EV-111 | History filters work (search, bookmark, subject) | apply each filter | `evidence/screenshots/10_history_filter.png` | PENDING |
| EV-112 | Practice generation and feedback work | generate quiz, answer correctly and incorrectly | `evidence/screenshots/12_practice.png`, `13_practice_feedback.png` | PENDING |
| EV-113 | Progress dashboard reflects real activity | after ask + quiz, open Progress | `evidence/screenshots/14_progress.png` | PENDING |
| EV-114 | Profile edit and logout work | change level/style; logout | `evidence/screenshots/16_profile.png` + steps | PENDING |
| EV-115 | Notification read/detail/deep-link work | open list, open detail, tap deep link | `evidence/screenshots/17_notifications.png`, `18_notification_detail.png` | PENDING |
| EV-116 | Lockout after 5 failed logins persists across restart | 5 bad logins, kill app, retry | `evidence/logs/lockout.txt` | PENDING |
| EV-117 | Image question end-to-end | pick image, submit, receive answer | `evidence/screenshots/21_image_question.png` | PENDING |
| EV-118 | Startup time (cold start) | `adb shell am start -W`; 3 runs | `evidence/logs/startup.txt` | PENDING |
| EV-119 | AI response latency | timestamp before/after one non-cached call; note network | `evidence/logs/ai_latency.txt` | PENDING |
| EV-120 | History with larger dataset (e.g. 200+ rows) remains usable | seed records or repeat questions; record scroll/load | `evidence/logs/history_perf.txt` | PENDING |
| EV-121 | Accessibility basic check (font scale, content descriptions) | system font largest; TalkBack optional | `evidence/logs/a11y.md` | PENDING |

---

## 5. Screenshot index (Pixel API 35)

| ID | Slot file | Screen/state | Supports | Status |
|---|---|---|---|---|
| EV-201 | `evidence/pixel35/01_login.png` | Welcome/login | FR1 | PENDING |
| EV-202 | `evidence/pixel35/02_register.png` | Register + validation | FR1 | PENDING |
| EV-203 | `evidence/pixel35/03_onboarding.png` | Onboarding choices | FR2 | PENDING |
| EV-204 | `evidence/pixel35/04_ask.png` | Ask empty state | FR3 | PENDING |
| EV-205 | `evidence/pixel35/05_ask_loading.png` | Loading/progress | NFR1 | PENDING |
| EV-206 | `evidence/pixel35/06_answer.png` | Answer with steps/formula | FR4 | PENDING |
| EV-207 | `evidence/pixel35/07_cache_hit.png` | Cached badge | FR4/cost | PENDING |
| EV-208 | `evidence/pixel35/08_error.png` | Network/provider error | Error handling | PENDING |
| EV-209 | `evidence/pixel35/09_history.png` | History list | FR6 | PENDING |
| EV-210 | `evidence/pixel35/10_history_filter.png` | Search/bookmark/subject | FR6 | PENDING |
| EV-211 | `evidence/pixel35/11_detail.png` | Detail + enrichment | FR5 | PENDING |
| EV-212 | `evidence/pixel35/12_practice.png` | Practice questions | FR7 | PENDING |
| EV-213 | `evidence/pixel35/13_practice_feedback.png` | Feedback correct/incorrect | FR7 | PENDING |
| EV-214 | `evidence/pixel35/14_progress.png` | Progress dashboard | FR8 | PENDING |
| EV-215 | `evidence/pixel35/15_progress_empty.png` | Empty progress state | FR8 | PENDING |
| EV-216 | `evidence/pixel35/16_profile.png` | Profile/settings | FR2/FR1 | PENDING |
| EV-217 | `evidence/pixel35/17_notifications.png` | Notification list | FR10 | PENDING |
| EV-218 | `evidence/pixel35/18_notification_detail.png` | Notification detail | FR10 | PENDING |
| EV-219 | `evidence/pixel35/19_offline_read.png` | Offline read of saved answer | NFR5 | PENDING |
| EV-220 | `evidence/pixel35/20_persistence.png` | Persistence after relaunch | FR6/NFR5 | PENDING |
| EV-221 | `evidence/pixel35/21_image_question.png` | Image question flow | FR3 | PENDING |

**Rule:** filenames above are slots, not existing files. Existing images in `docs/screenshots/` are pre-existing project assets with unknown device/API; they may be referenced only with that caveat, never as Pixel API 35 evidence.

---

## 6. Tooling and methodology evidence (P5/M4)

| ID | Claim to support | Source | Status |
|---|---|---|---|
| EV-301 | Version control used continuously | `git log` (EV-002), branch/remote facts (EV-003/004) | VERIFIED (text); screenshot PENDING |
| EV-302 | GitHub repository visible with commit history | browser screenshot of `origin` repository | PENDING |
| EV-303 | CI pipeline executed | GitHub Actions run page for `android-test.yml` | PENDING |
| EV-304 | Android Studio used as IDE | IDE screenshot: project tree, build output, emulator | PENDING |
| EV-305 | Methodology actually practised | `docs/AGILE_SPRINT_LOG.md` content verified against commits; any board/sprint artefacts | PENDING (audit) |
| EV-306 | Testing workflow used | Gradle test report HTML/XML; `docs/PART1_M1_TEST_PLAN.md`; `docs/TEST_REPORT_NP5.md` | VERIFIED (files exist); current reports PENDING copy |
| EV-307 | Bug tracking and fixes | `docs/BUG_LIST_NP5.md`, `docs/IMPROVEMENT_LOG.md`, fix commits `9563cdd`, `89e31d3`, `0fefe4d` | VERIFIED (files/commits); trace table PENDING |
| EV-308 | Design artefacts | `docs/uml/` (13 diagrams), `docs/PART1_M1_SOFTWARE_DESIGN.md` | VERIFIED (files exist) |
| EV-309 | Build measurement | EV-005 and EV-007 outputs (timestamps included) | VERIFIED |

---

## 7. Peer review evidence (Activity 1 — P4/M3)

Template only. **No participant data exists in this log.** Do not create names, roles, responses or charts.

| ID | Item | Required content | Status |
|---|---|---|---|
| EV-401 | Questionnaire final version | Q1–Q9 Likert 1–5, Q10–Q12 open-ended; stored in appendix | PENDING |
| EV-402 | Invitation/consent record | how participants were invited; consent for use of responses | PENDING |
| EV-403 | Participant profiles | real full name, role, job description/study background, review context | PENDING |
| EV-404 | Raw responses | per participant per question; anonymisation mapping | PENDING |
| EV-405 | Response summary | invitations sent, responses received, valid responses, aggregation method | PENDING |
| EV-406 | Charts/tables | generated from EV-404 only | PENDING |
| EV-407 | Feedback IDs and decisions | FB-01… mapped to requirement/design and decision (adopt/modify/defer/reject) | PENDING |
| EV-408 | Previously unconsidered opportunities (M3) | only items actually raised by participants | PENDING |
| EV-409 | Limitations of the review | sample size, bias, non-representativeness | PENDING (to be written honestly) |

**Blocker:** Activity 1 cannot be finalised without EV-401…EV-405.

---

## 8. Test execution log (P6)

Part 1 defines T1–T10. ASM2 runtime scenarios are separate. Every row starts as PENDING.

| ID | Test | Level | Expected (from Part 1 / design) | Actual | Status |
|---|---|---|---|---|---|
| T1 | XP awarded once per event; level curve | Unit | Exact XP/level values | 13 `XpRulesTest` cases passed (EV-006) | VERIFIED (unit only) |
| T2 | Similarity ≥0.85 reuses; below does not | Unit | Boundary cases pass | 33 `QuestionSimilarityTest` + 12 `ReusePolicyTest` cases passed | VERIFIED (unit only) |
| T3 | Gemini JSON → `AiAnswer`; malformed → typed error | Unit | No crash, typed error | 27 `GeminiParsingTest` cases passed | VERIFIED (unit only) |
| T4 | Password hash verify/reject | Unit | Constant-time comparison | No dedicated `PasswordHasher` test class in current suite | PENDING / gap |
| T5 | DAO insert/query/index | Instrumented | Rows returned in order | compiled only; no device | PENDING |
| T6 | Duplicate email rejected; five failures lock account | Instrumented | Correct error; lockout persists | `AuthRepositoryTest` compiled only | PENDING |
| T7 | Navigation across five destinations | UI | Destination visible | `MainShellSmokeTest` compiled only | PENDING |
| T8 | Manual UAT: ask, cache hit, offline read, bookmark, practice, progress | Manual | Six tasks complete | not executed | PENDING |
| T9 | Image unreadable without Keystore key | Inspection/unit | Ciphertext only | no test/capture | PENDING |
| T10 | First screen renders; AI call shows progress | Emulator | No main-thread block | not executed | PENDING |
| TC-ASK-02 | Exact duplicate reuses cache, 0 AI calls | Instrumented | Cache hit | not executed | PENDING |
| TC-ASK-03 | Similar question reuses cache | Instrumented | Cache hit | not executed | PENDING |
| TC-ASK-10 | Image >5 MB blocked or compressed | Instrumented | Clear message | feature absent (EV-023) | NOT APPLICABLE / gap |
| TC-SEC-06/07 | 2FA correct/incorrect code | Instrumented | Login success/deny | 2FA absent (EV-019) | NOT APPLICABLE |
| TC-SEC-08 | Abuse content blocked before AI | Instrumented | Blocked | filter not wired (EV-021) | NOT APPLICABLE / gap |
| TC-OFF-03/04 | Offline queue then drain | Instrumented | Queued, then sent once | queue absent (EV-020) | NOT APPLICABLE |
| TC-PRV-01/02 | Delete data, keep account | Instrumented | Data cleared, account retained | not executed | PENDING |

**20-test-case gate:** Part 1 M1 evidence exists (EV-028), so the fallback is not triggered on current evidence. Pane_AUDIT must confirm; if confirmation is negative, this table becomes the seed for the 20 mandatory cases and must be fully executed.

---

## 9. Support documentation and packaging

| ID | Artefact | Owner | Status |
|---|---|---|---|
| EV-501 | English README/environment guide verified against current code | implementation pane | PENDING |
| EV-502 | Build/run guide with exact commands (EV-005/007) | implementation pane | VERIFIED (commands), doc rewrite PENDING |
| EV-503 | User guide (English, 1–2 pages) | report pane | PENDING |
| EV-504 | Architecture/API note reconciled with as-built (baseline §5–6) | report pane | PENDING |
| EV-505 | Software package: source snapshot + APK + checksum | implementation pane | PENDING (record APK SHA-256) |
| EV-506 | Demo account instructions without exposing credentials in the report | coordinator | PENDING |
| EV-507 | Test guide referencing EV-005…EV-008 and T1–T10 | report pane | PENDING |
| EV-508 | Presentation slides (Activity 2) | team | PENDING |
| EV-509 | Presentation screenshot for report §2.8 | team | PENDING |
| EV-510 | Front sheet 2 completed | coordinator | PENDING |
| EV-511 | Harvard reference list | report pane | PENDING |
| EV-512 | Formatting verification (Calibri 12, 1.5 spacing, margins, page numbers) | report pane | PENDING |
| EV-513 | Word counts per section | report pane | PENDING |
| EV-514 | Final PDF export and page count | report pane | PENDING |

---

## 10. Claim-to-evidence quick rules

1. Feature exists → cite source file (EV-015…EV-024) **and** runtime evidence (EV-1xx/2xx) before saying it works.
2. Test pass → cite the XML totals and date. Instrumented ≠ executed.
3. Performance → cite measured runs only. No estimates.
4. Usability → cite real observation or survey. A heuristic review must be labelled as such.
5. Security → say exactly what is encrypted; never generalise to "all data encrypted".
6. Offline → say "saved answers readable offline"; never claim queued submission.
7. AI accuracy → never claim "accurate". Describe structure, parsing safeguards and residual risk.
8. Peer review → real participants only; otherwise the section is incomplete, not fictional.
9. GitHub/CI → screenshot or exact URL of a real page; no assumed pipeline success.
10. Screenshots → Pixel API 35, English UI, no secrets, captured for ASM2 or clearly labelled pre-existing.

---

## 11. Change log for this file

| Date | Change | By |
|---|---|---|
| 2026-11-09 | Created; verified rows EV-001…EV-030 added from this baseline session; all runtime/screenshot/peer-review rows marked PENDING | implementation pane |
