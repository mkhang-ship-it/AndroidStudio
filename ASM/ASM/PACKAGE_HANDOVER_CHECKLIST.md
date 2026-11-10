# ASM2 — Package Handover Acceptance Checklist

**Scope:** source-only software ZIP and sanitised debug APK for the Unit 22 Assignment Part 2 submission.
**Target branch for verified copies:** `Assignment_Report`.
**Prepared:** 2026-11-10 in the AUDIT worktree.
**Status:** checklist only. The archive was produced in the Assigment worktree: `ASM/ASM/deliverables/software/ASM2_Software_Package.zip` (243 files, 16,680,560 bytes, SHA-256 `340b92e27410090bdf05262e1674274eff1c141eb8ad9f32c87360355e816d90`) and its QA is in `PACKAGE_QA.md`. The package is **not present in AUDIT**; every item below stays `PENDING` until the coordinator copies the archive here and it is inspected. Nothing in this file asserts that AUDIT holds the package.

**Rules:** do not modify app source; do not fabricate survey, consent, reference or rehearsal data; do not commit or push from AUDIT unless explicitly instructed.

---

## 1. Handover identity

Record before any check. Unknown values stay `PENDING`.

| Field | Value | Status |
|---|---|---|
| Archive name | `ASM2_Software_Package.zip` | `PENDING` |
| Archive SHA-256 | — | `PENDING` |
| Archive size (bytes) | — | `PENDING` |
| Source commit under test | fixed build commit for the evidence set | `PENDING` |
| APK name | `app-debug.apk` | `PENDING` |
| APK SHA-256 | expected fixed build `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`; confirm from the archive | `PENDING` |
| APK package / version | `com.aistudy.aistudy`, versionName 1.0, versionCode 1 | `PENDING` |
| Reviewer | — | `PENDING` |
| Review date | — | `PENDING` |
| Handover destination | `Assignment_Report` | `PENDING` |

---

## 2. Artefact contract

| Artefact | Required state | Acceptance |
|---|---|---|
| Source archive | **Source only.** Kotlin, Gradle files, wrapper, resources, tests, schema. No build output, no APK inside. | `PENDING` |
| Sanitised APK | Delivered separately from the source archive. Debug-signed, installable, no embedded key value. | `PENDING` |
| `README.md` | At the archive root; build and run steps reproduce without editing source. | `PENDING` |
| `USER_GUIDE.md` | Copy of `ASM/ASM/USER_GUIDE.md`; no secret material. | `PENDING` |
| Evidence folder | Optional. If included: `EVIDENCE/` holds the test XML/logs and the exact 16 final images, clearly separated from optional captures. | `PENDING` |
| Report / deck | Kept out of the source archive; delivered through the report path, not the software ZIP. | `PENDING` |

---

## 3. Forbidden paths in the source archive

Any hit is a `FAIL`, not a warning.

| Forbidden path or pattern | Why |
|---|---|
| `local.properties` | Local SDK path; machine-specific. |
| `ai.properties` | Contains the Gemini key. |
| `*.properties` containing an assigned provider-key field | Secret material. |
| `*.jks`, `*.keystore` | Signing material. |
| `.git/` | History and remote metadata. |
| `.gradle/`, `.kotlin/` | Local caches. |
| Any `build/` directory (root, `app/`, `androidTest/`) | Generated output; includes APKs and intermediates. |
| `app/build/outputs/` | Packaged artefacts and results; source-only rule. |
| `.idea/`, `*.iml` | Local IDE state; exclude unless the brief explicitly asks for project files. |
| `*.apk`, `*.aab`, `*.ap_`, `*.dex` | Build output; APK is a separate artefact. |
| `*.hprof`, `captures/`, `*.log` from local runs | Runtime debris; not source. |
| Absolute paths, `..` traversal, duplicate entries, nested archives | Broken or unsafe archive layout. |

Allowed and expected: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties` (without secrets), `gradlew`, `gradlew.bat`, `gradle/wrapper/`, `app/build.gradle.kts`, `app/src/main/`, `app/src/test/`, `app/src/androidTest/`, `app/schemas/`, `gradle/libs.versions.toml`, `CONTRIBUTING.md`, `ai.properties.example`, `local.properties.example`.

---

## 4. Secret scan

Scan every text file and the APK. Expected result: **no key-shaped match and no assigned key value**.

| Pattern | Expected |
|---|---|
| `AIza[0-9A-Za-z_-]{35}` | 0 matches |
| Provider-key assignment pattern | 0 matches; the bare field name may appear in `BuildConfig` |
| `sk-[A-Za-z0-9]{20,}` | 0 matches |
| `AKIA[0-9A-Z]{16}` | 0 matches |
| `ghp_`, `gho_`, `ghs_`, `ghu_`, `ghr_` tokens | 0 matches |
| `xox[baprs]-` tokens | 0 matches |
| `-----BEGIN ... PRIVATE KEY-----` | 0 matches |
| APK SHA-256 digest | Allowed, evidence identifier only |

A documentation sentence that names a pattern (for example `AIza…` in a QA note) is not a secret; record it as a mention, not a failure.

---

## 5. Exact 16 final images

If `EVIDENCE/` is included, the final set must contain exactly these files, no additions and no omissions:

```
01-welcome.png
02-register-filled.png
03-onboarding-step1.png
04-onboarding-step2.png
05-ask-input.png
06-ask-gemini-answer.png
07-history-real-question.png
08-question-detail.png
09-progress-real.png
10-practice-generate.png
11-practice-question.png
12-practice-correct-feedback.png
13-profile.png
14-notifications.png
15-logout-confirm.png
16-logged-out-welcome.png
```

Acceptance:

- [ ] `real-final` count is 16 and the name set matches exactly. `PENDING`
- [ ] No root-folder demo capture (`01-device-home` … `22-practice-correct-answer-fixed`) is used as final evidence. `PENDING`
- [ ] Optional captures (image question, offline error/recovery, login after logout) live in a separate `optional/` folder and are labelled supplementary. `PENDING`
- [ ] Optional captures are never counted toward the 16-image gate. `PENDING`

---

## 6. APK key scan and install

| Check | Expected | Status |
|---|---|---|
| APK hash recorded and matches the fixed build | `c7c8f9e2…` or the hash of the actual packaged build, recorded verbatim | `PENDING` |
| `AIza` key shape inside the APK | 0 matches | `PENDING` |
| `GEMINI_API_KEY` field name | May appear; must not carry a value | `PENDING` |
| `local.properties`, `ai.properties`, `.jks`, `.keystore` inside the APK | Absent | `PENDING` |
| `apkanalyzer` / `aapt` package metadata | `com.aistudy.aistudy`, versionName 1.0 | `PENDING` |
| Clean install on API 35 emulator | Installs and launches | `PENDING` |
| Launch without a key | Falls back to the fake client; the simulation banner is visible | `PENDING` |

---

## 7. ZIP inspection

| Check | Method | Expected | Status |
|---|---|---|---|
| Archive reads | list contents before extraction | readable, no corrupt entry | `PENDING` |
| Entry count and size | count files and sum bytes | recorded, no unexpected bulk | `PENDING` |
| Top-level layout | list first path segment | one project root plus `README.md` / `USER_GUIDE.md`, no stray temp folder | `PENDING` |
| Forbidden paths | §3 filter | 0 hits | `PENDING` |
| Path safety | no absolute path, no `..`, no nested ZIP | 0 hits | `PENDING` |
| Extraction | extract to a clean directory | succeeds without error | `PENDING` |
| Source completeness | wrapper, settings, module build file, `src/main`, tests | all present | `PENDING` |
| README reproduction | follow README from the extracted tree | build succeeds without editing source | `PENDING` |

Use `PACKAGE_REVIEW_TEMPLATE.md` for the exact commands and result rows.

---

## 8. README requirements

- [ ] At archive root. `PENDING`
- [ ] States prerequisites: JDK, Android SDK, API level. `PENDING`
- [ ] States how to supply `ai.properties` locally; never includes the key. `PENDING`
- [ ] Gives build and test commands, including the offline/keyless path. `PENDING`
- [ ] Gives install and first-run steps. `PENDING`
- [ ] Lists known limitations consistent with the report; no accuracy or encryption overclaim. `PENDING`
- [ ] Does not cite a demo screenshot as final evidence. `PENDING`

---

## 9. Branch target and handover

- [ ] Verified checklist, review results and QA record are copied into `Assignment_Report`. `PENDING`
- [ ] Source and evidence paths in the copied documents still resolve from `Assignment_Report`. `PENDING`
- [ ] No app source was modified by the AUDIT worktree. `VERIFIED` — none changed.
- [ ] No commit or push from AUDIT. `VERIFIED` — none performed.
- [ ] Coordinator records the source commit and APK hash used for the submission. `PENDING`

---

## 10. Acceptance sign-off

| Gate | Condition | Status |
|---|---|---|
| P1 Source-only | No forbidden path, no build output, no APK inside the ZIP | `PENDING` |
| P2 Secret-free | §4 scan clean in source and APK | `PENDING` |
| P3 Final evidence | Exactly 16 final images; optional separated and labelled | `PENDING` |
| P4 APK | Sanitised, hashed, installs on API 35 | `PENDING` |
| P5 ZIP | Contents inspected, extraction clean, path-safe | `PENDING` |
| P6 README | Reproduces a runnable build without editing source | `PENDING` |
| P7 Branch | Verified copies land in `Assignment_Report` | `PENDING` |

**Overall:** `PENDING` — the archive and packaged APK are not in AUDIT. Do not mark any gate `PASS` without a captured command output or a reopened file.

**Still pending outside packaging:** survey responses and consent, Harvard reference verification, presentation rehearsal, remaining NFR rows and the demo-account procedure. These are not fixed by this checklist.
