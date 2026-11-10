# PACKAGE QA — AI Study Mentor

QA date: 2026-11-10 (local)
Worktree: `C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment`
Branch: `Assignment_Report`
Status: **staging verified, ZIP created and inspected.**

## Actual checks

| Check | Command / method | Actual result | Verdict |
|---|---|---|---|
| Staging files | recursive file count | **243 files / 87 directories** | Staged |
| Staging bytes | recursive size sum | 41,152,858 bytes | Recorded |
| `ai.properties` in staging | recursive file check | absent | Clean |
| `local.properties` in staging | recursive file check | absent | Clean |
| Keystores/keys | `*.jks,*.keystore,*.p12,*.pem,*.key` | 0 files | Clean |
| Build/IDE caches | `build`, `.gradle`, `.idea`, `.kotlin`, `.git`, `.commandcode` | 0 directories | Clean |
| Demo screenshots | parent `evidence/pixel-api-35/*.png` | 0 PNGs staged | Clean |
| Docs screenshots | `docs/screenshots` | excluded | Clean |
| Final evidence PNGs | count in `real-final/*.png` | **16** | Matches 16-image gate |
| Optional evidence PNGs | count in `real-final/optional/*.png` | 8 (supplementary) | Labelled, not gate |
| Final evidence files | `real-final/` root | 16 PNG + 10 TXT + 1 XML = 27 | Recorded |
| Sanitized APK | isolated rebuild, no key file/env | `app-debug.apk` 35,174,179 bytes | Built |
| Sanitized APK key scan | regex over all 170 extracted entries | **0 provider-key-like hits** | Clean |
| Sanitized `BuildConfig` | generated `BuildConfig.java` | `GEMINI_API_KEY = ""` | Offline |
| Sanitized APK install | `adb install -r` on emulator-5554 | `Success`; package + versionCode 1 / versionName 1.0 | Installs |
| APK SHA-256 | .NET SHA-256 over packaged APK | `e7391113c18907d71b84e8e9856e02a0e3ece9ee447bebb32fdb526e82e18700` | Matches staging copy |
| Staging secret scan | regex over every staged file | 0 hits | Clean |
| ZIP members | archive entry listing | see ZIP rows below | Inspected |
| ZIP member secret scan | regex over every member | 0 hits | Clean |
| ZIP SHA-256 / size | computed after archive creation | recorded below | Recorded |

## APK verdict

- The original real-key APK (SHA-256
  `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`) is
  **excluded**: it embeds `GEMINI_API_KEY` in DEX.
- The included `AIStudy-sanitized-debug.apk` was built from an isolated copy
  with no `ai.properties`, no key in `local.properties`, and
  `GEMINI_API_KEY` unset.
- Generated `BuildConfig.GEMINI_API_KEY` is empty; the app runs offline
  `FakeAiClient`.
- All 170 APK entries scanned: 0 provider-key-like hits.
- Installed successfully on Pixel_6_API_35 (Android 15 / API 35).

## ZIP rows (actual)

| Item | Actual result |
|---|---|
| Archive | `ASM/ASM/deliverables/software/ASM2_Software_Package.zip` |
| ZIP members | **243 files** |
| ZIP size | **16,680,495 bytes** |
| ZIP SHA-256 | `836607729dde6b2f0e3b6cb924448c3a91ddeaead2ba2d1066369f213f2de820` |
| ZIP secret scan | 0 hits (matches never printed) |
| Parent demo PNG in ZIP | 0 |
| Final PNG in ZIP | exactly 16 (plus 8 optional supplementary) |

The SHA-256 above is computed over the archive after it is finalized. Because a
file cannot contain its own archive hash, the archived copy of this QA file
records the same staging/ZIP facts with the hash line pointing to this
repository copy and `README_PACKAGE.txt` delivered beside the archive.

## Evidence gate

- Authoritative final screenshots remain exactly `real-final/01`–`16` (16 PNGs).
- `real-final/optional/` is supplementary (image question, offline
  error/recovery, login-after-logout) and is not part of the 16-image gate.
- All old demo/FakeAiClient screenshots are excluded from the clean package.

## PENDING items

| Item | Reason |
|---|---|
| Controlled demo account | Not supplied; create a local account in the app |
| OS-level push notification permission | Not exercised |
| Real-Gemini APK distribution | Deliberately excluded (key embedded) |

## Verdict

Source, docs, tests, sanitized APK, and real-final evidence are staged and
archived. The ZIP was listed and scanned after creation: no secrets, no demo
screenshots, exactly 16 authoritative final PNGs.
