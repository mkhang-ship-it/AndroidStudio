# PACKAGE QA — AI Study Mentor

QA date: 2026-11-10 (local)
Worktree: `C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment`
Status: **checks performed, but no ZIP created or inspected.** A clean ZIP is
therefore NOT claimed.

## Actual checks

| Check | Command / method | Actual result | Verdict |
|---|---|---|---|
| Fixed APK present | `Get-Item app-debug.apk` | 35,596,239 bytes | Present |
| APK SHA-256 | `certutil -hashfile app-debug.apk SHA256` | `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71` | Matches verified build |
| `ai.properties` exists | file check | Yes, 470 bytes | **Must be excluded** |
| `ai.properties` gitignored | `git check-ignore -v` | `AI Study/.gitignore:16:/ai.properties` | Ignored |
| `local.properties` | file check | Absent; only `local.properties.example` | No machine path to remove |
| Keystores/keys | recursive `*.jks,*.keystore,*.p12,*.pem,*.key` | 0 files | Clean |
| Source file counts | recursive `.kt` | main 98, test 13, androidTest 5 | Present |
| Key-pattern scan (source/docs) | regex for Gemini-key-like material over `AI Study/app/src`, `README.md`, `docs` | 0 files | Clean |
| Key-pattern scan (APK) | extract DEX entries, search for Gemini-key-like material | **found in 2 DEX files** (classes12.dex, classes15.dex) | **APK NOT package-safe** |
| Final evidence PNGs | count in `real-final/*.png` | **16** | Matches the 16-image gate |
| Final evidence files (parent) | count in `real-final/` | 16 PNG + 10 TXT + 1 XML = 27 | Recorded |
| Optional evidence | `real-final/optional/` | 8 PNG + 5 TXT = 13 | Supplementary, labelled |
| Old demo screenshots | count in parent evidence folder | 22 PNG (`01`, `02`–`22`) | Excluded from package |
| Focused unit tests | `PracticeAnswerTest` | 7/7 passed, 0 failures | Verified |
| Connected tests | `connectedDebugAndroidTest` | 39/39 passed, 0 failures/skipped | Verified |
| Full unit suite | `testDebugUnitTest` | 159/159 passed, 0 failures | Verified |
| ZIP existence | file check | No ZIP created in this phase | **PENDING** |

## APK finding (blocking clean APK inclusion)

The current debug APK was built with the local `ai.properties`, so
`BuildConfig.GEMINI_API_KEY` is embedded in the DEX. The scan found key-like
material in two DEX files. The key value was not printed.

Consequences:

- Do **not** publish the current APK as a clean package.
- A sanitized APK (empty/placeholder key → offline `FakeAiClient`) or a
  source-only package is required.
- This is recorded as PENDING, not as a clean result.

## Evidence gate

- Authoritative final screenshots remain exactly `real-final/01`–`16` (16 PNGs).
- `real-final/optional/` is supplementary (image question, offline
  error/recovery, login-after-logout) and is not part of the 16-image gate.
- All old demo/FakeAiClient screenshots are excluded from the clean package.

## PENDING items

| Item | Reason |
|---|---|
| Sanitized APK (no embedded key) | Current APK contains key material; rebuild needed |
| Actual ZIP creation and file listing | Not performed in this phase |
| ZIP SHA-256 and post-ZIP secret scan | Cannot be claimed until the ZIP is inspected |
| Controlled demo account | Not supplied; must be provided separately |

## Verdict

Source, docs, tests, and real-final evidence are ready to stage under the
manifest in `SOFTWARE_PACKAGE_DRAFT.md`. The package is **not yet a verified
clean ZIP** because (a) the APK contains embedded key material and (b) no ZIP
has been created and inspected.
