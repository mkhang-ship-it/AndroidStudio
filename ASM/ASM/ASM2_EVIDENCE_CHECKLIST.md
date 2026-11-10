# ASM2 — Evidence Checklist

**Companion to:** `ASM2_AUDIT_PLAN.md`
**Purpose:** the single list of every artefact the report needs, where it comes from, how to produce it, and whether it exists.
**Rule:** nothing enters the report unless it is ticked here with a real path or command output.

**Status:** `HAVE` (verified this session) · `DOC-ONLY` (claimed in a doc, unverified) · `PENDING` (must be produced) · `BLOCKED` (external decision)

> **Phase 3 update (2026-11-09):** runtime evidence now exists. Instrumented suite executed on `Pixel_6_API_35` (Android 15 / API 35): **39 tests, 0 failures**, XML retained; **19 screenshots** captured. See `ASM/ASM/evidence/PIXEL_API35_RUNTIME_EVIDENCE.md`, `ASM/ASM/evidence/AVD_API35_SETUP.md` and `ASM/ASM/evidence/pixel-api-35/`. Any row below that still marks those two artefacts `PENDING` is superseded by this note and by `ASM2_REPORT_DRAFT.md` §3.5.1. Peer-review responses, performance measurements and real-Gemini evidence remain outstanding.

---

## 1. Environment setup (do this first)

Verified problem: Gradle fails with `SDK location not found` because `AI Study/local.properties` is gitignored and absent. Export `ANDROID_HOME` instead of committing it.

```powershell
$env:JAVA_HOME     = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME  = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:Path"
```

- [ ] `HAVE` — JBR present at `C:\Program Files\Android\Android Studio\jbr`
- [ ] `HAVE` — SDK at `%LOCALAPPDATA%\Android\Sdk`; platform `android-37.0` installed
- [ ] `PENDING` — `AI Study/ai.properties` with `GEMINI_API_KEY` (copy from `ai.properties.example`). **Never commit; never paste the key into the report.**

---

## 2. Build evidence

Working directory: `C:\Users\ADMIN\orca\workspaces\AndroidStudio\AUDIT`

| ID | Artefact | Command | Status |
|---|---|---|---|
| B-01 | Unit-test run, green | `& "AI Study\gradlew.bat" -p "AI Study" :app:testDebugUnitTest --offline --console=plain` | `HAVE` — BUILD SUCCESSFUL in 23 s |
| B-02 | Unit-test aggregation | see §3 command | `HAVE` — 152 tests / 12 classes / 0 failures |
| B-03 | Debug APK | `& "AI Study\gradlew.bat" -p "AI Study" :app:assembleDebug --offline --console=plain` | `HAVE` — `AI Study/app/build/outputs/apk/debug/app-debug.apk`, 33.53 MB |
| B-04 | Instrumented test APK compiles | `& "AI Study\gradlew.bat" -p "AI Study" :app:assembleDebugAndroidTest --offline --console=plain` | `HAVE` — BUILD SUCCESSFUL |
| B-05 | Instrumented tests **executed** | `& "AI Study\gradlew.bat" -p "AI Study" :app:connectedDebugAndroidTest --console=plain` | `PENDING` — no device attached; 39 cases compiled, pass status unproven |
| B-06 | Build log saved to repo | copy console output to `docs/BUILD_LOG_ASM2.md` | `PENDING` |
| B-07 | Test report XML archived | `AI Study/app/build/test-results/testDebugUnitTest/TEST-*.xml` | `HAVE` (generated); archive `PENDING` |
| B-08 | Lint / static check | `& "AI Study\gradlew.bat" -p "AI Study" :app:lintDebug` | `PENDING` |

**Screenshot to take:** Terminal window showing `BUILD SUCCESSFUL` for B-01 and B-03 → `evidence/B01-unit-tests.png`, `evidence/B03-apk-build.png`.

---

## 3. Test evidence (numbers to quote in §3.3 and §4.4)

```powershell
cd "C:\Users\ADMIN\orca\workspaces\AndroidStudio\AUDIT\AI Study"
$x = Get-ChildItem -Recurse -Filter "TEST-*.xml" app\build\test-results\testDebugUnitTest
$t=$f=$e=$s=0
foreach ($i in $x) {
  [xml]$d = Get-Content $i.FullName
  $t += [int]$d.testsuite.tests; $f += [int]$d.testsuite.failures
  $e += [int]$d.testsuite.errors; $s += [int]$d.testsuite.skipped
}
"files=$($x.Count) tests=$t failures=$f errors=$e skipped=$s"
```

Measured output (`HAVE`, 2026-11-09):

```
files=12 tests=152 failures=0 errors=0 skipped=0
```

| Class | Tests | Class | Tests |
|---|---|---|---|
| ConvertersTest | 8 | QuestionSimilarityTest | 33 |
| ReusePolicyTest | 12 | SubjectDetectorTest | 15 |
| FakeAiClientTest | 8 | XpRulesTest | 13 |
| GeminiParsingTest | 27 | ExampleUnitTest | 1 |
| AbuseFilterTest | 5 | TmpRegexCheckTest | 4 |
| ProgressInsightsTest | 7 | LatexTest | 19 |

- [ ] `HAVE` — 152 JVM tests
- [ ] `PENDING` — per-class coverage % (tool not configured; optional)
- [ ] `DOC-ONLY` — instrumented 39/39 (source: `docs/IMPROVEMENT_LOG.md`; no retained XML)
- [ ] `PENDING` — 20 written test cases (TC-01…TC-20) with expected and actual columns

**Do not write** "143 tests" (`docs/IMPROVEMENT_LOG.md`) or "76 tests" (`docs/TEST_REPORT_NP5.md`) or "120 tests" (Part 1). All three are superseded by the measured 152.

---

## 4. Pixel API 35 device (screenshot + measurement prerequisite)

Current state: only `android-37.1/google_apis_playstore_ps16k` is installed; `Medium_Phone` AVD runs android-37 at 1080×2400. **No API 35 image exists.** All captures in `docs/screenshots/` come from that AVD.

```powershell
# 1. Install API 35 system image
& "$env:ANDROID_HOME\cmdline-tools\latest\bin\sdkmanager.bat" "system-images;android-35;google_apis;x86_64"

# 2. Create a Pixel AVD on API 35
& "$env:ANDROID_HOME\cmdline-tools\latest\bin\avdmanager.bat" create avd `
    -n Pixel_API_35 -k "system-images;android-35;google_apis;x86_64" -d pixel_7

# 3. Boot with explicit DNS (emulator DNS proxy is unreliable here — see README)
& "$env:ANDROID_HOME\emulator\emulator.exe" -avd Pixel_API_35 -dns-server 8.8.8.8,1.1.1.1

# 4. Confirm
adb devices
adb shell getprop ro.build.version.sdk      # must print 35
adb shell wm size                          # record resolution for Appendix D
```

- [ ] `BLOCKED` → `PENDING` — image installed
- [ ] `PENDING` — AVD created, SDK 35 confirmed
- [ ] `PENDING` — app installed: `adb install -r "AI Study\app\build\outputs\apk\debug\app-debug.apk"`
- [ ] `PENDING` — build commit recorded: `79c523a`

---

## 5. Screenshot checklist (all `PENDING`; 20 shots)

Save to `evidence/asm2/pixel35/` and reference as `[S01]`…`[S20]` in the report. Every caption must state: screen, requirement demonstrated, device/API, commit.

| ID | Screen | Must show | Req | Status |
|---|---|---|---|---|
| S01 | Welcome / Login | Email + password, sign-in, sign-up | FR1 | `PENDING` |
| S02 | Register | Name, email, password, validation error state | FR1 | `PENDING` |
| S03 | Onboarding step 1 | Education level choices + COPPA note | FR2 | `PENDING` |
| S04 | Onboarding step 2 | Explanation style choices | FR2 | `PENDING` |
| S05 | Ask — empty | Input, image picker, submit button | FR3 | `PENDING` |
| S06 | Ask — loading | Thinking indicator while AI call runs | NFR1 | `PENDING` |
| S07 | Ask — answer | Subject chip, steps, final answer | FR4 | `PENDING` |
| S08 | Answer — formula | Rendered fraction or limit, not raw LaTeX | FR4 quality | `PENDING` |
| S09 | Question detail | Answer + five improvement blocks | FR5 | `PENDING` |
| S10 | History — list | Search field, filters, subject grouping | FR6 | `PENDING` |
| S11 | History — search | Result after typing a keyword | FR6 | `PENDING` |
| S12 | History — bookmarked | Bookmark filter active | FR6 | `PENDING` |
| S13 | Practice — setup | Source question, count stepper 1–20 | FR7 | `PENDING` |
| S14 | Practice — question | MCQ or short-answer item | FR7 | `PENDING` |
| S15 | Practice — feedback | Correct/incorrect plus explanation | FR7 | `PENDING` |
| S16 | Progress | Totals, review time, accuracy, XP, subject bars, insights | FR8 | `PENDING` |
| S17 | Profile | Level, style, badges, logout, delete account | FR9 | `PENDING` |
| S18 | Notifications | Types, unread indicator | FR10 | `PENDING` |
| S19 | Notification detail | Deep-link button | FR10 | `PENDING` |
| S20 | Offline history | Airplane mode on, saved answers readable | NFR5 | `PENDING` |

`S20` procedure: load history → `adb shell cmd connectivity airplane-mode enable` → reopen History → capture → disable.

Interim fallback (only if API 35 is genuinely unavailable): reuse `docs/screenshots/01_ask.png` … `10_notification_detail.png`, but each caption **must** state "captured on Medium_Phone AVD, Android 16 (API 37), 1080×2400" — do not present them as Pixel API 35.

---

## 6. Measurement checklist (`PENDING` — no numbers exist yet)

| ID | Metric | Command / method | Record |
|---|---|---|---|
| M-01 | Cold start → Ask | `adb shell am start -W -n com.aistudy.aistudy/.MainActivity` ×5 | min / median / max |
| M-02 | AI round trip | `adb logcat -s GeminiAPI` timestamps, 5 distinct questions | median, network, model |
| M-03 | History open time | Seed N rows, open tab, stopwatch ×3 | N + median |
| M-04 | Actions to submit | Manual: tap field → type → tap submit | count (target ≤3) |
| M-05 | Unit suite runtime | wall clock from B-01 | 23 s (`HAVE`) |
| M-06 | APK size | file metadata | 33.53 MB (`HAVE`) |
| M-07 | Offline behaviour | Airplane mode, ask a new question | observed error message, verbatim |
| M-08 | Lockout persistence | 5 wrong passwords → kill app → reopen | still locked? yes/no |
| M-09 | MCQ scoring | Generate practice with a real key, answer a known-correct MCQ | scored correct? yes/no — **flag, do not fix** |
| M-10 | Image round trip | Ask with an image, then open the saved question | image displayed? currently expected **no** |

M-09 and M-10 are audit findings, not defects to repair. Record the actual behaviour and report it in §3.6 and §4.4.

---

## 7. Peer review evidence (`PENDING` — blocking P4 / M3 / D2)

| ID | Artefact | Status |
|---|---|---|
| PR-01 | Questionnaire text, final, Q01–Q13, in Appendix A | `PENDING` |
| PR-02 | Invitation log (who, when, channel) | `PENDING` |
| PR-03 | ≥3 participant profiles (name, role, job/study description, date) | `PENDING` — **must be real people** |
| PR-04 | Raw responses, one row per participant per question | `PENDING` |
| PR-05 | Aggregation table (median + range per Likert question) | `PENDING` |
| PR-06 | Thematic grouping of Q11–Q13 open answers, with verbatim quotes | `PENDING` |
| PR-07 | Consent record for using names/quotes | `PENDING` |
| PR-08 | FB-01…FB-nn decision table (adopt / modify / defer / reject + reason) | `PENDING` |

Do not pre-fill any cell. If only 2 reviewers can be recruited, say so in §1.3.1 and reduce the claims — do not fabricate a third.

---

## 8. GitHub / repository evidence

| ID | Evidence | Value | Status |
|---|---|---|---|
| GH-01 | Remote URL | `https://github.com/mkhang-ship-it/AndroidStudio` | `HAVE` |
| GH-02 | Branch + head | `main` @ `79c523a8911321de208e2cc9ff065ac9811afed2` | `HAVE` |
| GH-03 | Commit count / span | 13 commits, 2026-09-30 → 2026-11-02 | `HAVE` |
| GH-04 | Commit list with dates | `git log --date=short --pretty=format:"%h|%ad|%an|%s"` | `HAVE` |
| GH-05 | Author distribution | mkhangsip 25, Le Quy Tam 9, NCnguyenn 1, trungvinh-52hz 1 (all refs) | `HAVE` |
| GH-06 | CI workflow file | `.github/workflows/android-test.yml` (`./gradlew test` on push/PR) | `HAVE` |
| GH-07 | Successful CI run page | Actions tab screenshot | `PENDING` |
| GH-08 | Tags / releases | none exist | `HAVE` (absence) |
| GH-09 | Merge commits / PRs | none exist | `HAVE` (absence) |
| GH-10 | Commit graph screenshot | `git log --graph --oneline --all` | `PENDING` |
| GH-11 | Representative diff screenshot | e.g. `git show 0fefe4d --stat` | `PENDING` |

Screenshots to take: GH-10, GH-11, GH-07 → `evidence/github/`.

**Honest reporting requirement:** GH-08 and GH-09 are absences. If §2.5.2 claims a review process, it must be reconciled against the absence of pull requests. Do not describe a PR-review workflow that the evidence does not show.

---

## 9. Methodology evidence (M4 / M5)

| ID | Artefact | Status |
|---|---|---|
| ME-01 | `docs/AGILE_SPRINT_LOG.md` S01–S03 populated | `HAVE` |
| ME-02 | Sprint log S14 populated | `HAVE` |
| ME-03 | Sprint log S04–S13 (10 sprints) | `PENDING` — empty rows |
| ME-04 | `AI Study/CONTRIBUTING.md` (PR rules, test gate, code conventions) | `HAVE` |
| ME-05 | Definition of Done | `HAVE` (in CONTRIBUTING + roadmap) |
| ME-06 | Backlog / board | `PENDING` — none found |
| ME-07 | Retrospective notes per sprint | `PENDING` — only S01–S03 and S14 |
| ME-08 | Commit cadence chart | `PENDING` — derivable from GH-04 |

If ME-03 is filled retrospectively, label it "reconstructed from commit history on <date>" in §2.2.1. Do not present later reconstruction as contemporaneous record-keeping.

---

## 10. Code-evidence index (for citation in §2.3–§2.4)

Cite as `path:line`. All `HAVE`.

| Topic | File | Anchor |
|---|---|---|
| Ask flow + cache decision | `data/repository/QuestionRepositoryImpl.kt` | `askInternal` (exact hash → Jaccard → AI) |
| Reuse policy | `data/repository/ReusePolicy.kt` | `select`, `reuseAs` |
| Similarity thresholds | `domain/rules/QuestionSimilarity.kt` | `SIMILARITY_THRESHOLD = 0.85`, `MIN_JACCARD_GUARD = 0.6` |
| Gemini client | `domain/ai/GeminiAiClient.kt` | `post`, `parseAnswer`, `usageTokens`, retry block |
| Error taxonomy | `domain/ai/AiError.kt` | NotConfigured / Provider / Network / Malformed |
| Manual DI | `platform/ServiceGraph.kt` | `install` |
| Password hashing | `data/security/PasswordHasher.kt` | `ITERATIONS = 120_000` |
| Session storage | `data/security/EncryptedPrefsSessionStore.kt` | `EncryptedSharedPreferences.create` |
| Image encryption | `data/security/AesGcmImageStore.kt` | `store`, `read`, `delete` |
| Login throttle | `data/security/LoginThrottle.kt` | `SharedPrefsLoginThrottle` |
| Auth transaction | `data/repository/AuthRepositoryImpl.kt` | `withTransaction`, `SQLiteConstraintException` catch |
| History DAO | `data/db/dao/QuestionAnswerDao.kt` | `search` (LIKE), `deleteByHashes` |
| Quiz accuracy rule | `data/db/dao/QuizItemDao.kt` | `accuracy` (`isCorrect IS NOT NULL`) |
| Progress aggregation | `data/repository/ProgressRepositoryImpl.kt` | `summary` |
| Review-time accrual | `ui/screens/ask/AskScreen.kt` | `LaunchedEffect` 15 s tick |
| MCQ scoring | `ui/screens/practice/PracticeScreen.kt:269`, `ui/practice/PracticeViewModel.kt` `checkAnswer` | compares option **text** to `correctAnswer` |
| Onboarding subjects | `ui/onboarding/OnboardingScreen.kt` | `submitOnboarding(subjects = emptySet())` |
| Abuse filter (unused) | `domain/rules/AbuseFilter.kt` | no reference from `main` |
| Schema | `app/schemas/com.aistudy.aistudy.data.db.AppDatabase/1.json` | version 1, 7 tables |
| Build config | `AI Study/app/build.gradle.kts` | `minSdk = 33`, `BuildConfig.GEMINI_API_KEY` |
| Dependencies | `AI Study/gradle/libs.versions.toml` | AGP 9.3.3, Kotlin 2.2.10, Room 2.7.2 |
| CI | `.github/workflows/android-test.yml` | `./gradlew test` |

---

## 11. Findings that must appear honestly in the report

Each is verified; none should be silently omitted or silently "fixed".

| ID | Finding | Where to raise it |
|---|---|---|
| F-01 | Onboarding collects no subjects; profile subjects are always empty at onboarding | §2.4.5, §3.3 (FR2 Partial), §3.6 |
| F-02 | Attached question image is stored but never displayed again in any UI | §2.4.1, §3.3 (FR3 Partial), §3.6 |
| F-03 | `AbuseFilter` is implemented and tested but never called by the app | §2.3.2, §3.3 (NFR4 Partial), §4.4 |
| F-04 | Encrypted image files are never deleted on account removal; `ImageStore.delete` has no caller | §2.3.2, §3.6, §4.4 |
| F-05 | No offline queue or connectivity observer; a new question while offline simply fails | §3.4 (NFR5 Partial), §4.4 |
| F-06 | 2FA is schema-only (column + setter, no UI, no verification) | §3.4 (NFR6 Partial), §3.6 |
| F-07 | Leaderboard absent; gamification is device-local | §2.4.5, §3.6 |
| F-08 | `REVIEW_REMINDER` defined but never generated; mistake alert matches English keywords only | §2.4.5, §3.3 (FR10 Partial) |
| F-09 | MCQ scoring compares option text to the answer key — may mis-grade when the model returns a letter | §3.3 (FR7), §4.2, §4.4 |
| F-10 | Room body columns are not encrypted; Part 1's "all user data encrypted" is only partly met | §2.3.2, §3.4 |
| F-11 | Doc numbers disagree (143 / 76 / 120 vs measured 152) | §3.1, §4.4 |
| F-12 | 10 of 14 sprint entries are empty | §2.2.1, §4.3 |
| F-13 | No tags, merges, PRs or issues in git | §2.5.2, §4.3 |
| F-14 | Instrumented suite never executed in this environment | §3.1, §4.4 |

---

## 12. Format and submission checklist

- [ ] `PENDING` — English throughout, British spelling
- [ ] `PENDING` — Calibri 12, 1.5 line spacing
- [ ] `PENDING` — margins left 1.25 cm, right/top/bottom 1 cm
- [ ] `PENDING` — page numbers
- [ ] `PENDING` — front sheet 2 completed (student name and code, class, assessor; group/member fields N/A — individual submission)
- [ ] `PENDING` — Harvard in-text citations + reference list
- [ ] `PENDING` — ≥20 test cases in §3.3
- [ ] `PENDING` — word count within the agreed limit (see DEC-01)
- [ ] `PENDING` — exported to PDF and uploaded to CMS
- [ ] `PENDING` — software package attached (source archive + APK + README), no API key included

---

## 13. Consolidated blocker list

| # | Blocker | Blocks | Owner |
|---|---|---|---|
| 1 | No peer reviewers recruited | P4, M3, D2, §1.4, §5.1–5.3, Appendix B | Group |
| 2 | No API 35 system image / AVD | S01–S20, Appendix D, G3 | Whoever has SDK write access |
| 3 | Instrumented suite not executed | B-05, T6/T7 claims, §4.4 | Whoever has a device |
| 4 | No measurements (M-01…M-04, M-07…M-10) | §3.4, NFR1/NFR2 judgement | Tester |
| 5 | Word limit conflict | Whole document (DEC-01) | Tutor |
| 6 | Sprint log S04–S13 empty | §2.2.1, ME-03, M4 strength | Group |
| 7 | `GEMINI_API_KEY` unavailable | Real-AI screenshots (S07, S08, S14, S15), M-09 | Group |
