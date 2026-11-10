# PIXEL_6_API_35 — Runtime Evidence (Phase 2)

Captured: 2026-11-09
Worktree: `C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment`
App source: `AI Study` (source/Gradle not modified)
AVD: `Pixel_6_API_35` (already provisioned, not reprovisioned)

> Two sessions are documented:
> - **Session A** — built without `ai.properties` → `FakeAiClient`. Screenshots 02–19.
> - **Session B** — real key configured → real Gemini call. Screenshots 20–21.
> Both are real executions; no result is fabricated.

## 1. Device and toolchain

`adb devices` / `getprop` results:

| Property | Value |
|---|---|
| adb serial | `emulator-5554` (state `device`) |
| `ro.build.version.sdk` | `35` |
| `ro.build.version.release` | `15` |
| `ro.product.model` | `sdk_gphone64_x86_64` |
| `ro.product.device` | `emu64xa` |
| `ro.product.cpu.abi` | `x86_64` |
| Emulator | Android emulator 37.1.11.0 |
| sdkmanager | 12.0 |

Environment used for all Gradle/adb commands:

```bat
set "ANDROID_SDK_ROOT=C:\Users\ADMIN\AppData\Local\Android\Sdk"
set "ANDROID_HOME=C:\Users\ADMIN\AppData\Local\Android\Sdk"
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
```

Commands:

```bat
adb devices
adb shell getprop ro.build.version.sdk
adb shell getprop ro.build.version.release
adb shell getprop ro.product.model
```

## 2. Build artifacts

### Session A — no key present (FakeAiClient)

```bat
cd /d "C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment\AI Study"
gradlew.bat assembleDebug --no-daemon --console=plain
```

Exit `0`. APK SHA-256: `b33d924197c1773387f18aa65557e95155a4d4116fadc1a68f6d4d8664027ec1`

### Session B — real key configured

`ai.properties` was found at the **original project** path, then copied into this worktree:

```
From: C:\Users\ADMIN\orca\AndroidStudio\AI Study\ai.properties
To:   C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment\AI Study\ai.properties
```

The worktree `.gitignore` contains `/ai.properties`, so the file is not committed. The key value was never printed.

Build run directly from PowerShell in the project directory (no nested cmd quoting):

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME="C:\Users\ADMIN\AppData\Local\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
Set-Location "C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment\AI Study"
& .\gradlew.bat assembleDebug --no-daemon --console=plain
```

Result: `BUILD SUCCESSFUL in 10s`, exit `0`. APK SHA-256:

```
0ac0c05abfce41106fa339983f795bc84466754627597631263f39653dabd84e
```

Artifact:

```
app/build/outputs/apk/debug/app-debug.apk
```

| Item | Value |
|---|---|
| Package | `com.aistudy.aistudy` |
| versionCode / versionName | `1` / `1.0` |
| compileSdk / targetSdk | `37` / `37` |
| minSdk | `33` |
| Size | 35,157,795 bytes (fake build) / real-key build repackaged |
| Locale label | `AI Study Mentor` |

Install and launch:

```bat
adb install -r app-debug.apk          # Success
adb shell am force-stop com.aistudy.aistudy
adb shell am start -n com.aistudy.aistudy/.MainActivity
```

## 3. AI configuration (resolved)

Initial finding (Session A): the worktree had no `ai.properties`, only `ai.properties.example`, so the provider key was empty and the app ran `FakeAiClient`.

Resolution:
- The key **did exist**, at `C:\Users\ADMIN\orca\AndroidStudio\AI Study\ai.properties` (original project, not the worktree).
- File content after copy (value masked):
  - `provider key configured (length recorded, value withheld)`
  - `GEMINI_MODEL=gemini-flash-lite-latest`
- Generated `debug` `BuildConfig.java` after rebuild:
  - `provider key was confirmed non-empty; value was not printed`
  - `GEMINI_MODEL = "gemini-flash-lite-latest"`
- Runtime Welcome/Ask screens no longer show the fake-mode banner.

Note on the earlier "empty" reading: a check had picked the stale `androidTest` `BuildConfig.java`. The `debug` `BuildConfig.java` is the correct file and it contains the key. Gradle log confirms the configuration cache was invalidated by the new `ai.properties` (`Calculating task graph as configuration cache cannot be reused because the file system entry 'ai.properties' has been created`) and `:app:generateDebugBuildConfig` re-ran.

## 4. Real Gemini flow (Session B)

Steps performed on the emulator:

1. Installed the real-key APK.
2. Old test account was gone (the connected test install reset app data); login failed with `Email hoac mat khau khong dung`.
3. Registered a new local test account:
   - display name `[REDACTED_LOCAL_TEST_ACCOUNT]`
   - email `[REDACTED_LOCAL_TEST_EMAIL]`
   - password `[REDACTED_LOCAL_TEST_PASSWORD]`
4. Completed onboarding (`Đại học`, `Từng bước`).
5. Asked: `What is the capital of France` → `Hỏi ngay`.

UI result (screenshot `21-gemini-real-answer.png`):

```
Step 1  Identify the country mentioned in the question, which is France.
Step 2  Recall the major geographical and political centers of France.
Step 3  Determine the designated capital city where the national government is seated.
Final answer
The capital of France is Paris.
```

Network proof (`21-gemini-network-logcat.txt`, filtered, no key):

```
D GeminiAPI: POST https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-lite-latest:generateContent (attempt 1)
D GeminiAPI: ← HTTP 200 responseLen=1232
"finishReason": "STOP"
"totalTokenCount": 380
"modelVersion": "gemini-3.5-flash-lite"
```

Conclusion: **real Gemini call verified** (HTTP 200, model `gemini-3.5-flash-lite`). No Gemini success is claimed anywhere else.

Observed mismatch (not fixed, out of scope): the model JSON returned `"subject": "other"`, but the UI chip displayed `Môn: Toán học` (`math`). The cause was not investigated because source changes are not allowed.

## 5. Flows attempted

All interactions were performed on the running emulator via `adb shell input` after locating nodes with `uiautomator dump`. Screenshots were captured with `adb shell screencap` + `adb pull` (PowerShell `>` redirection corrupts PNG, so it was not used).

### Session A (FakeAiClient)

| # | Flow | Input / action | Observed result | Status | Screenshot |
|---|---|---|---|---|---|
| 1 | Launch | App launch, fresh install | Welcome screen with fake-mode banner | PASS | `02-welcome.png` |
| 2 | Register | `[REDACTED_LOCAL_TEST_ACCOUNT]` / `[REDACTED_LOCAL_TEST_EMAIL]` / `[REDACTED_LOCAL_TEST_PASSWORD]` | Registration accepted, moved to onboarding | PASS | `03`, `04`, `05` |
| 3 | Onboarding | `Đại học`, `Từng bước` | 2/2 steps, entered MainShell | PASS | `05`, `06` |
| 4 | Ask text question | Suggestion `Giải thích định lý Pythagore` | Simulated answer, subject `Toán học`, difficulty `Trung bình` | PASS (simulated) | `07`, `08` |
| 5 | History | Opened History tab | `1 kết quả`, question row | PASS | `09` |
| 6 | Question detail | Tapped history row | Question, answer, `Đã đọc 0 min` | PASS | `10` |
| 7 | Progress | Opened Progress tab | `1` question, `15 giây`, `0%`, `+7 XP` | PASS | `11` |
| 8 | Practice generate | Source + count `3` | 3 simulated multiple-choice questions | PASS (simulated) | `12`, `13` |
| 9 | Practice answer | Selected A → `Nộp bài` | `Chưa đúng.` / `Đáp án đúng: A` | OBSERVED — cause not isolated | `14` |
| 10 | Profile | Opened Profile tab | `[REDACTED_LOCAL_TEST_ACCOUNT]`, `Cấp độ: 1`, `17 XP`, badge | PASS | `15` |
| 11 | Notifications | `Xem thông báo` | `1 chưa đọc`, one item | PASS | `16` |
| 12 | Notification detail | `Xem chi tiết` | Detail shown, `Đã đọc` | PASS | `17` |
| 13 | Logout | `Đăng xuất` → confirm | Returned to Welcome | PASS | `18`, `19` |

### Session B (real Gemini)

| # | Flow | Input / action | Observed result | Status | Screenshot |
|---|---|---|---|---|---|
| 14 | Install real-key APK | `adb install -r` | Success, no fake-mode banner | PASS | `20-ask-real-mode.png` |
| 15 | Login (old account) | `[REDACTED_LOCAL_TEST_EMAIL]` / `[REDACTED_LOCAL_TEST_PASSWORD]` | `Email hoac mat khau khong dung` (data reset) | EXPECTED CONTEXT — account absent | — |
| 16 | Register new account | `[REDACTED_LOCAL_TEST_ACCOUNT]` / `[REDACTED_LOCAL_TEST_EMAIL]` / `[REDACTED_LOCAL_TEST_PASSWORD]` | Accepted, onboarding shown | PASS | — |
| 17 | Onboarding | `Đại học`, `Từng bước` | Entered MainShell | PASS | — |
| 18 | Real ask | `What is the capital of France` | Real answer `The capital of France is Paris.` + HTTP 200 | PASS | `21-gemini-real-answer.png` |

### Flows not exercised

- Image / camera question (`Chụp / chọn ảnh bài tập`) — not attempted.
- OS-level notification permission / push — not exercised.
- Practice/History/Profile after the real-key rebuild — not re-run; those flows were verified in Session A on the same source.

## 6. Screenshot index

Location: `ASM/ASM/evidence/pixel-api-35/`
Manifest: `ASM/ASM/evidence/pixel-api-35/MANIFEST.txt`

**Final set (authoritative): `ASM/ASM/evidence/pixel-api-35/real-final/`**
Manifest: `real-final/MANIFEST_REAL.txt` — 16 screenshots captured on the running
app with real Gemini for AI flows.

All images are 1080x2400 PNG, verified readable after pull.

```
01-device-home.png
02-welcome.png
03-register-empty.png
04-register-filled.png
05-onboarding-step1.png
06-onboarding-step2.png
07-ask-empty.png
08-ask-answer-fake.png
09-history.png
10-question-detail.png
11-progress.png
12-practice.png
13-practice-generated.png
14-practice-answered.png
15-profile.png
16-notifications.png
17-notification-detail.png
18-logout-confirm.png
19-logged-out-welcome.png
20-ask-real-mode.png
21-gemini-real-answer.png
22-practice-correct-answer-fixed.png
```

Non-image evidence:

```
MANIFEST.txt
connectedDebugAndroidTest.txt
connectedDebugAndroidTest-result.xml
connectedDebugAndroidTest-after-key.txt
connectedDebugAndroidTest-after-key-result.xml
connectedDebugAndroidTest-practice-fix.txt
connectedDebugAndroidTest-practice-fix-result.xml
practice-fix-unit-tests.txt
21-gemini-network-logcat.txt
```

> **Authoritative final evidence: `real-final/` (Phase 6), captured with real Gemini.**
> Screenshots `02`–`19` were captured with `FakeAiClient` and are **DEMO / non-final**
> historical evidence. They are kept, not deleted. `20`–`22` are real-key/real-Gemini
> but predate the complete final set; `real-final/` is the authoritative set.

## 7. Instrumented tests (`connectedDebugAndroidTest`)

Command:

```bat
cd /d "C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment\AI Study"
gradlew.bat connectedDebugAndroidTest --no-daemon --console=plain
```

Gradle output (saved to `evidence/pixel-api-35/connectedDebugAndroidTest.txt`):

```
Starting 39 tests on Pixel_6_API_35(AVD) - 15
Pixel_6_API_35(AVD) - 15 Tests 27/39 completed. (0 skipped) (0 failed)
Finished 39 tests on Pixel_6_API_35(AVD) - 15
> Task :app:connectedDebugAndroidTest

BUILD SUCCESSFUL in 29s
```

JUnit XML (copied to `evidence/pixel-api-35/connectedDebugAndroidTest-result.xml`):

```
tests=39 failures=0 errors=0 skipped=0 time=15.865
```

Per suite:

| Suite | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| `ExampleInstrumentedTest` | 1 | 0 | 0 | 0 |
| `QuestionAnswerDaoTest` | 11 | 0 | 0 | 0 |
| `UserDaoTest` | 5 | 0 | 0 | 0 |
| `AuthRepositoryTest` | 13 | 0 | 0 | 0 |
| `MainShellSmokeTest` | 9 | 0 | 0 | 0 |
| **Total** | **39** | **0** | **0** | **0** |

### Re-run after key (Phase 4) — VERIFIED

Command (PowerShell directly in `AI Study`, no nested cmd quoting):

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME="C:\Users\ADMIN\AppData\Local\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
Set-Location "C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment\AI Study"
& .\gradlew.bat connectedDebugAndroidTest --no-daemon --console=plain
```

Exit code `0`. Gradle output (saved to `evidence/pixel-api-35/connectedDebugAndroidTest-after-key.txt`):

```
Starting 39 tests on Pixel_6_API_35(AVD) - 15
Pixel_6_API_35(AVD) - 15 Tests 27/39 completed. (0 skipped) (0 failed)
Finished 39 tests on Pixel_6_API_35(AVD) - 15
> Task :app:connectedDebugAndroidTest

BUILD SUCCESSFUL in 50s
```

After-key JUnit XML (copied to `evidence/pixel-api-35/connectedDebugAndroidTest-after-key-result.xml`):

```
tests=39 failures=0 errors=0 skipped=0 time=12.5 timestamp=2026-11-09T12:56:48
```

Per-suite distribution is unchanged: `ExampleInstrumentedTest` 1, `QuestionAnswerDaoTest` 11, `UserDaoTest` 5, `AuthRepositoryTest` 13, `MainShellSmokeTest` 9 — all with 0 failures, 0 errors, 0 skipped.

- The earlier pre-key run also passed 39/39. Both runs use identical source; only `buildConfigField` values differ.
- `testDebugUnitTest`: **not run** — PENDING if unit-test evidence is required separately.
- These are real executions on the API-35 emulator, not compile-only output.

## 8. Blockers / PENDING

| Item | Status |
|---|---|
| Real Gemini answer | **PASS** — HTTP 200, model `gemini-3.5-flash-lite`, screenshot `21`, logcat |
| API key location | Resolved — key was at original project path, copied to worktree, gitignored, never printed |
| Practice grading mismatch (selected A → "Chưa đúng", correct answer A) | **FIXED (Phase 5)** — see section 9 |
| Subject chip mismatch (`other` in JSON vs `Toán học` UI) | OBSERVED, root cause PENDING |
| Image-pick question flow | PENDING — not exercised |
| Unit tests (`testDebugUnitTest`) | **VERIFIED** — 159/159 passed, including 7 new practice regression tests |
| Re-run instrumented tests on real-key build | **VERIFIED** — 39/39 passed after key (Phase 4 and Phase 5), exit 0 |

Phase 5 changed application source and tests (see section 9); no Gradle files or report prose were modified. `AI Study/ai.properties` remains gitignored and is not committed.

## 9. Code fix — Practice/Quiz answer key (Phase 5)

### Bug

In Practice/Quiz, selecting the correct option was reported as incorrect: choosing `A` while the correct answer was `A` showed `Chưa đúng` / `Đáp án đúng: A`.

### Root cause (actual data flow)

1. `FakeAiClient.generateQuiz()` stores `correctAnswer = "A"` (an option **letter**) while `optionA..D` hold option **texts**.
2. `GeminiAiClient.parseQuizDrafts()` stores whatever `correctAnswer` the model returns; for MULTIPLE_CHOICE this can be a letter (`"A"`) or the option text (verified in this session: Gemini returned `"4"` for the correct option).
3. `PracticeScreen` sets `userAnswer = optionText` when an option is tapped.
4. Both `PracticeViewModel.checkAnswer()` and `PracticeScreen` compared `userAnswer` (option text) to `QuizItem.correctAnswer` (possibly a letter) with raw `String.equals`.
   → `"Ket qua dung voi dieu kien cua bai toan" != "A"`, so a correct selection was always marked wrong, and `isCorrectOption` never highlighted the right option.

### Fix

Added one normalization point used by both ViewModel and UI, in `ui/practice/PracticeViewModel.kt`:

- `QuizItem.correctAnswerText()`: for MULTIPLE_CHOICE, if `correctAnswer` is an option letter `A`–`D` (or `1`–`4`), resolve it to the matching option text; if it already equals an option text, return it; for non-MC items return it unchanged.
- `QuizItem.isCorrectAnswer(answer)`: compares the user's selected option text to `correctAnswerText()` (trim + ignoreCase).
- `PracticeViewModel.checkAnswer()` delegates to `isCorrectAnswer`.
- `PracticeScreen` uses `isCorrectAnswer` for the feedback card, `correctAnswerText()` for the correct-option highlight and the `Đáp án đúng: …` text.

No schema, repository, prompt, or unrelated feature changes.

### Changed files

```
AI Study/app/src/main/java/com/aistudy/aistudy/ui/practice/PracticeViewModel.kt
AI Study/app/src/main/java/com/aistudy/aistudy/ui/screens/practice/PracticeScreen.kt
AI Study/app/src/test/java/com/aistudy/aistudy/ui/practice/PracticeAnswerTest.kt   (new)
```

### Tests and build

Focused regression tests (PowerShell directly in `AI Study`):

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME="C:\Users\ADMIN\AppData\Local\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
Set-Location "C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment\AI Study"
& .\gradlew.bat testDebugUnitTest --tests "com.aistudy.aistudy.ui.practice.PracticeAnswerTest" --no-daemon --console=plain
```

Result: `BUILD SUCCESSFUL`, `PracticeAnswerTest` = **7 tests, 0 failures, 0 skipped**. Cases include:

- letter answer key `A` → option A text is correct, options B/C/D are incorrect;
- letter key resolved to option text for display;
- numeric key `1` → option A;
- answer key stored as option text still matches;
- short-answer trim/case handling;
- ViewModel: selecting the correct option records `isCorrect = true` and increments `correctCount`;
- ViewModel: selecting a wrong option records `isCorrect = false` and keeps `correctCount = 0`.

Full unit suite:

```powershell
& .\gradlew.bat testDebugUnitTest --no-daemon --console=plain
```

Result: **159 tests, 0 failures, 0 errors, 0 skipped** (13 suites; log saved as `evidence/pixel-api-35/practice-fix-unit-tests.txt`).

APK:

```powershell
& .\gradlew.bat assembleDebug --no-daemon --console=plain
```

Result: `BUILD SUCCESSFUL in 12s`, exit `0`.
APK SHA-256: `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`.

Connected tests after the fix:

```powershell
& .\gradlew.bat connectedDebugAndroidTest --no-daemon --console=plain
```

Result: `Starting 39 tests` → `Finished 39 tests`, `BUILD SUCCESSFUL in 34s`, exit `0`.
JUnit XML: `tests=39 failures=0 errors=0 skipped=0` (saved as `connectedDebugAndroidTest-practice-fix-result.xml`).
Note: the first attempt failed with `java.net.BindException: Address already in use: bind` (Gradle daemon/environment); one retry after `gradlew --stop` passed. No source-related failure.

### Screenshot status

`22-practice-correct-answer-fixed.png` — captured on Pixel_6_API_35 after installing the fixed APK.

- This is a **real Gemini-generated quiz** (not FakeAiClient): `POST …:generateContent` → HTTP 200; the response contained one MULTIPLE_CHOICE question with options `3 / 4 / 5 / 22` and `correctAnswer: "4"`.
- The screenshot shows option **B (4)** selected, the correct-option check mark, and `Chính xác!`.
- Limitation to record honestly: for this particular Gemini response the answer key was the option **text** `"4"`, so the screenshot demonstrates the corrected end-to-end UI but does **not** itself exercise the letter-key path. The letter-key regression is proven deterministically by `PracticeAnswerTest` (answer key `"A"`), not by this screenshot.
- Old screenshots `02`–`19` (including `14-practice-answered.png`) were produced with `FakeAiClient` and are marked **non-final**; they are kept, not deleted.

## 10. Real-final evidence set (Phase 6)

Setup:

- Fixed APK reinstalled on `Pixel_6_API_35` (emulator-5554): SHA-256 `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`.
- Local test account: `[REDACTED_LOCAL_TEST_ACCOUNT]` / `[REDACTED_LOCAL_TEST_EMAIL]`.
- AI mode: real Gemini via `ai.properties`; the key value is never printed or written to evidence.
- Every screenshot was captured only after the state was observed in the running app, with `adb shell screencap` + `adb pull`.
- Location: `ASM/ASM/evidence/pixel-api-35/real-final/` — manifest `MANIFEST_REAL.txt`.

| File | Flow | Action | Observed result | SDK | Gemini/network |
|---|---|---|---|---|---|
| `01-welcome.png` | Launch / welcome | Cold launch after reinstall | Welcome title, no fake-mode banner | 35 | No |
| `02-register-filled.png` | Registration | Filled name/email/password/confirm | Local form accepted input (password masked) | 35 | No |
| `03-onboarding-step1.png` | Onboarding 1/2 | Opened after registration | Education level options | 35 | No |
| `04-onboarding-step2.png` | Onboarding 2/2 | Selected level → Tiếp | Explanation style options | 35 | No |
| `05-ask-input.png` | Ask input | Typed `Explain the Pythagorean theorem` | Question text in input | 35 | No |
| `06-ask-gemini-answer.png` | Ask answer | `Hỏi ngay`, waited | Real Gemini steps + "Đã lưu vào Lịch sử." | 35 | Yes (HTTP 200) |
| `07-history-real-question.png` | History | Opened History tab | `1 kết quả`, saved real question | 35 | No (derived) |
| `08-question-detail.png` | Question detail | Tapped history row | Full question + real answer | 35 | No (derived) |
| `09-progress-real.png` | Progress | Opened Progress tab | 1 question, 15s, 0%, +7 XP, Toán học | 35 | No (derived) |
| `10-practice-generate.png` | Practice setup | Opened Practice tab | Source picker + count control | 35 | No |
| `11-practice-question.png` | Practice question | Source + count=1 → generate | Real Gemini MULTIPLE_CHOICE question | 35 | Yes (HTTP 200) |
| `12-practice-correct-feedback.png` | Practice answer | Selected option B → `Nộp bài` | Option B correct, feedback `Chính xác!` | 35 | No (quiz from Gemini) |
| `13-profile.png` | Profile | Opened Profile tab | [REDACTED_LOCAL_TEST_ACCOUNT], level 1, 22 XP, badge | 35 | No |
| `14-notifications.png` | Notifications | Profile → `Xem thông báo` | 1 unread motivational notification | 35 | No |
| `15-logout-confirm.png` | Logout confirm | Profile → `Đăng xuất` | Confirm dialog | 35 | No |
| `16-logged-out-welcome.png` | Logout result | Confirmed logout | Returned to Welcome | 35 | No |

Network evidence (filtered logcat, no API key):

- `real-final/06-gemini-network-logcat.txt`: `POST …/gemini-flash-lite-latest:generateContent` → `HTTP 200`, `finishReason=STOP`, `totalTokenCount=592`, `modelVersion=gemini-3.5-flash-lite`.
- `real-final/11-gemini-quiz-logcat.txt`: `POST …:generateContent` → `HTTP 200`; quiz `correctAnswer = "a^2 + b^2 = c^2"` (option B), `modelVersion=gemini-3.5-flash-lite`.

Practice fix verification in this final set:

- `12-practice-correct-feedback.png` shows the Gemini-generated quiz with option **B** selected, the correct-option check mark, and `Chính xác!`.
- For this quiz Gemini stored the answer key as option **text**, so the screenshot does not itself exercise the letter-key path; `PracticeAnswerTest` covers `correctAnswer = "A"` deterministically.

Tests re-run after the fixed APK:

- Focused: `testDebugUnitTest --tests "com.aistudy.aistudy.ui.practice.PracticeAnswerTest"` → `BUILD SUCCESSFUL`, **7 tests, 0 failures, 0 errors, 0 skipped** (`real-final/tests-unit-practice.txt`).
- Connected: `connectedDebugAndroidTest` → `Starting 39 tests` → `Finished 39 tests`, `BUILD SUCCESSFUL in 29s`, exit `0`; JUnit XML **39 tests, 0 failures/errors/skipped**, time 12.305 (`real-final/tests-connected.txt`, `real-final/tests-connected-result.xml`).
- Earlier full unit suite: **159/159**; source unchanged since.

Evidence separation:

- **DEMO / non-final:** `02`–`19` (FakeAiClient).
- **Real-key, pre-final:** `20`, `21`, `22`.
- **Final / authoritative:** `real-final/01`–`16` (this section).

Remaining real-evidence gaps (PENDING, not blockers):

- Login after logout was not separately captured; registration covers the auth flow.
- Image/camera question flow was not exercised.
- OS-level push notification permission was not exercised.
- Quiz screenshot used a text answer key; the letter-key path is covered by unit tests only.
