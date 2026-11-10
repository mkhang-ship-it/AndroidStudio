# ASM2 — Package Review Template

**Purpose:** review the actual `ASM2_Software_Package.zip` and its APK once the archive is copied from the Assigment worktree into this worktree.
**Status:** template. **No ZIP or packaged APK exists in AUDIT at the time of writing.** Every observed value and verdict in this file is `PENDING` until a reviewer runs the commands against a real archive and pastes the output.
**Rule:** do not infer contents from the build tree, from a previous run, or from the filename. Inspect the archive.

**Status vocabulary**

| Status | Meaning |
|---|---|
| `PASS` | Command ran on the actual archive and the observed value meets the expectation. |
| `FAIL` | Command ran on the actual archive and the observed value violates the expectation. Blocks handover. |
| `PENDING` | Not run, or the archive is not present. Never counts as a pass. |

---

## 0. Intake record

| Field | Value |
|---|---|
| Reviewer | `PENDING` |
| Date and time | `PENDING` |
| Archive path | `PENDING` |
| Archive SHA-256 | `PENDING` |
| Archive size (bytes) | `PENDING` |
| APK SHA-256 | `PENDING` |
| Source commit claimed by the packager | `PENDING` |
| Work directory | `PENDING` |

---

## 1. Preconditions

- Windows PowerShell 5.1 or later; Python 3.x for the binary scan.
- Android SDK platform-tools (`adb`) and either `apkanalyzer` or `aapt`.
- At least one clean API 35 emulator for the install check.
- Free space for two extractions: the archive and the APK.
- Permission to write only to a scratch directory. Do not edit the extracted source.

```powershell
$zip  = 'C:\path\to\ASM2_Software_Package.zip'
$apk  = 'C:\path\to\app-debug.apk'
$work = Join-Path $env:TEMP 'asm2_pkg_review'
if (Test-Path -LiteralPath $work) { Remove-Item -LiteralPath $work -Recurse -Force -Confirm:$false }
New-Item -ItemType Directory -Path $work | Out-Null
```

All rows below start as `PENDING`; replace with the captured output.

---

## 2. Archive identity and readability

```powershell
Get-Item -LiteralPath $zip | Select-Object FullName, Length, LastWriteTime
Get-FileHash -LiteralPath $zip -Algorithm SHA256 | Format-List
tar -tf $zip | Measure-Object -Line
tar -tf $zip | Select-Object -First 40
```

| Check | Expected | Observed | Status |
|---|---|---|---|
| Archive exists and is readable | file opens | — | `PENDING` |
| SHA-256 recorded | one value, copied verbatim | — | `PENDING` |
| Entry count recorded | plausible file count, no thousands of cache files | — | `PENDING` |
| Listing succeeds without error | no corrupt entry | — | `PENDING` |

---

## 3. Extraction and layout

```powershell
Expand-Archive -LiteralPath $zip -DestinationPath $work -Force
Get-ChildItem -LiteralPath $work -Recurse -File -Force |
  Measure-Object -Property Length -Sum |
  Select-Object Count, Sum
Get-ChildItem -LiteralPath $work -Directory -Force | Select-Object Name
Get-ChildItem -LiteralPath $work -Recurse -File -Force |
  Select-Object -First 25 FullName
```

| Check | Expected | Observed | Status |
|---|---|---|---|
| Extraction completes | no error | — | `PENDING` |
| Single project root | one root folder plus `README.md` and `USER_GUIDE.md` | — | `PENDING` |
| No nested archive | no second `.zip` inside | — | `PENDING` |
| No absolute or traversal path | 0 hits | — | `PENDING` |
| No duplicate entries | 0 hits | — | `PENDING` |

Path-safety command:

```powershell
tar -tf $zip | Where-Object { $_ -match '(^/|^[A-Za-z]:|(^|/)\.\.(/|$))' }
```

---

## 4. Forbidden paths

```powershell
$names = @('local.properties','ai.properties')
$exts  = @('.jks','.keystore','.apk','.aab','.ap_','.dex','.hprof')
Get-ChildItem -LiteralPath $work -Recurse -File -Force |
  Where-Object { $_.Name -in $names -or $_.Extension -in $exts } |
  Select-Object FullName

Get-ChildItem -LiteralPath $work -Recurse -Directory -Force |
  Where-Object { $_.Name -in @('build','.gradle','.kotlin','.idea','.git','captures') } |
  Select-Object FullName
```

| Check | Expected | Observed | Status |
|---|---|---|---|
| `local.properties`, `ai.properties` | absent | — | `PENDING` |
| `.jks`, `.keystore` | absent | — | `PENDING` |
| `build/`, `.gradle/`, `.kotlin/`, `.git/` | absent | — | `PENDING` |
| `.idea/`, `*.iml` | absent unless the brief asks for project files | — | `PENDING` |
| APK, AAB, DEX inside the source ZIP | absent; APK delivered separately | — | `PENDING` |
| Expected source files present | wrapper, settings, module build, `src/main`, tests, schema | — | `PENDING` |

---

## 5. Secret scan

### 5.1 Text files

```powershell
$patterns = @(
  'AIza[0-9A-Za-z_\-]{35}',
  'GEMINI_API_KEY\s*=\s*[A-Za-z0-9_\-]{8,}',
  '\bsk-[A-Za-z0-9]{20,}',
  '\bAKIA[0-9A-Z]{16}\b',
  '\bgh[pousr]_[0-9A-Za-z]{30,}\b',
  '\bxox[baprs]-[0-9A-Za-z\-]{10,}\b',
  '-----BEGIN [A-Z ]*PRIVATE KEY-----'
)
Get-ChildItem -LiteralPath $work -Recurse -File -Force |
  Where-Object { $_.Length -lt 5MB -and $_.Extension -notin @('.png','.jpg','.jpeg','.webp','.gif','.zip','.apk','.jar','.class','.dex') } |
  Select-String -Pattern $patterns -AllMatches |
  Select-Object Path, LineNumber, Line
```

### 5.2 Binary files

```powershell
python -c "import re,pathlib; p=pathlib.Path(r'C:\path\to\app-debug.apk'); b=p.read_bytes(); print('AIza',len(re.findall(rb'AIza[0-9A-Za-z_\-]{35}',b))); print('GEMINI_API_KEY',b.count(b'GEMINI_API_KEY')); print('sk',len(re.findall(rb'sk-[A-Za-z0-9]{20,}',b)))"
```

| Check | Expected | Observed | Status |
|---|---|---|---|
| Key-shaped Google key in source | 0 | — | `PENDING` |
| Assigned `GEMINI_API_KEY` value in source | 0 | — | `PENDING` |
| Other token or private-key patterns | 0 | — | `PENDING` |
| Key-shaped Google key in APK | 0 | — | `PENDING` |
| `GEMINI_API_KEY` field name in APK | may appear; no value | — | `PENDING` |
| Documentation mentions | allowed; record file and line, do not fail | — | `PENDING` |

A match inside a QA instruction such as `AIza…` is a mention, not a credential. A 39-character `AIza` value is a credential and is a `FAIL`.

---

## 6. README and reproduction

```powershell
Get-ChildItem -LiteralPath $work -Recurse -Filter 'README*' | Select-Object FullName
Get-ChildItem -LiteralPath $work -Recurse -Filter 'USER_GUIDE*' | Select-Object FullName
```

| Check | Expected | Observed | Status |
|---|---|---|---|
| `README.md` at archive root | present | — | `PENDING` |
| Build prerequisites stated | JDK, SDK, API level | — | `PENDING` |
| Key supplied locally only | `ai.properties` instruction; no value | — | `PENDING` |
| Build and test commands | reproduce without editing source | — | `PENDING` |
| Install and first-run steps | present | — | `PENDING` |
| Known limitations consistent with the report | no accuracy or encryption overclaim | — | `PENDING` |

Follow the README in a scratch copy. Record the exact command and the final Gradle line.

```powershell
cd (Join-Path $work 'AI Study')
.\gradlew.bat :app:assembleDebug --offline --console=plain
```

`PENDING` until the actual package is run. A `BUILD SUCCESSFUL` from the AUDIT working tree does not count.

---

## 7. APK metadata and install

```powershell
Get-FileHash -LiteralPath $apk -Algorithm SHA256 | Format-List
& "$env:ANDROID_HOME\cmdline-tools\latest\bin\apkanalyzer.bat" manifest print $apk |
  Select-String 'package=|versionName|versionCode|minSdkVersion|targetSdkVersion'
adb devices
adb install -r $apk
adb shell pm list packages com.aistudy.aistudy
```

If `apkanalyzer` is unavailable, use `aapt dump badging` from the installed build-tools.

| Check | Expected | Observed | Status |
|---|---|---|---|
| APK hash recorded | matches the fixed build or the actual packaged build | — | `PENDING` |
| Package name | `com.aistudy.aistudy` | — | `PENDING` |
| Version | versionName 1.0, versionCode 1 | — | `PENDING` |
| SDK levels | target 37, min 33 | — | `PENDING` |
| Clean install on API 35 | success | — | `PENDING` |
| Launch | opens Welcome | — | `PENDING` |
| No key configured | fake client and simulation banner | — | `PENDING` |

---

## 8. Final evidence gate

Only if the archive carries an `EVIDENCE/` folder or a screenshot appendix.

```powershell
$expected = @(
  '01-welcome.png','02-register-filled.png','03-onboarding-step1.png',
  '04-onboarding-step2.png','05-ask-input.png','06-ask-gemini-answer.png',
  '07-history-real-question.png','08-question-detail.png','09-progress-real.png',
  '10-practice-generate.png','11-practice-question.png',
  '12-practice-correct-feedback.png','13-profile.png','14-notifications.png',
  '15-logout-confirm.png','16-logged-out-welcome.png'
)
$actual = Get-ChildItem -LiteralPath (Join-Path $work 'EVIDENCE\real-final') -Filter '*.png' |
  Select-Object -ExpandProperty Name
Compare-Object -ReferenceObject $expected -DifferenceObject $actual
```

| Check | Expected | Observed | Status |
|---|---|---|---|
| Final image count | 16 | — | `PENDING` |
| Name set | exact 16, no extra, no missing | — | `PENDING` |
| Root demo capture used as final | none | — | `PENDING` |
| Optional captures separated | `optional/` and labelled supplementary | — | `PENDING` |
| Optional counted as final | no | — | `PENDING` |

`Compare-Object` with no output means the sets match. Any output is a `FAIL`.

---

## 9. Verdict

| Gate | Condition | Observed | Status |
|---|---|---|---|
| P1 Source-only | no forbidden path, no build output, no APK inside | — | `PENDING` |
| P2 Secret-free | source and APK scans clean | — | `PENDING` |
| P3 Final evidence | exact 16; optional separated | — | `PENDING` |
| P4 APK | hashed, sanitised, installs on API 35 | — | `PENDING` |
| P5 ZIP | contents inspected; extraction and path checks clean | — | `PENDING` |
| P6 README | reproduces a runnable build without editing source | — | `PENDING` |
| P7 Branch | verified copies land in `Assignment_Report` | — | `PENDING` |

**Overall verdict:** `PENDING`

**Reviewer note:** attach the command output for every `PASS`. A row without output stays `PENDING`.

---

## 10. Handover after review

1. Copy this completed review and `PACKAGE_HANDOVER_CHECKLIST.md` into `Assignment_Report`.
2. Record the archive hash, APK hash and source commit in the handover record.
3. Leave survey, consent, reference and rehearsal items `PENDING`; they are outside the package.
4. Do not commit or push from AUDIT unless the coordinator asks.
