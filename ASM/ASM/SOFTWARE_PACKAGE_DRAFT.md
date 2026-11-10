# SOFTWARE PACKAGE DRAFT — AI Study Mentor

Status: **DRAFT**. No ZIP has been created or inspected yet.
This document defines the exact clean-package manifest and removal rules.
`PACKAGE_QA.md` records the checks actually performed.

## Package identity

- Product: AI Study Mentor (`com.aistudy.aistudy`)
- Version: 1.0 (versionCode 1), debug build for evidence
- Fixed source state: practice-scoring fix present but **uncommitted**
- Verified APK (built with real Gemini key): SHA-256
  `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`
- Verified device: Pixel_6_API_35 / Android 15 / API 35

## Exact clean-package manifest (include)

Include only the following paths (relative to the project root):

```
README.md
build.ps1
CONTRIBUTING.md
docs/*.md
docs/uml/**
AI Study/.gitignore
AI Study/ai.properties.example
AI Study/local.properties.example
AI Study/build.gradle.kts
AI Study/settings.gradle.kts
AI Study/gradle.properties
AI Study/gradlew
AI Study/gradlew.bat
AI Study/gradle/**
AI Study/app/build.gradle.kts
AI Study/app/schemas/**
AI Study/app/src/main/**
AI Study/app/src/test/**
AI Study/app/src/androidTest/**
ASM/ASM/USER_GUIDE.md
ASM/ASM/NFR_MEASUREMENTS.md
ASM/ASM/PIXEL_API35_RUNTIME_EVIDENCE.md
ASM/ASM/AVD_API35_SETUP.md
ASM/ASM/evidence/pixel-api-35/real-final/01-welcome.png
ASM/ASM/evidence/pixel-api-35/real-final/02-register-filled.png
ASM/ASM/evidence/pixel-api-35/real-final/03-onboarding-step1.png
ASM/ASM/evidence/pixel-api-35/real-final/04-onboarding-step2.png
ASM/ASM/evidence/pixel-api-35/real-final/05-ask-input.png
ASM/ASM/evidence/pixel-api-35/real-final/06-ask-gemini-answer.png
ASM/ASM/evidence/pixel-api-35/real-final/07-history-real-question.png
ASM/ASM/evidence/pixel-api-35/real-final/08-question-detail.png
ASM/ASM/evidence/pixel-api-35/real-final/09-progress-real.png
ASM/ASM/evidence/pixel-api-35/real-final/10-practice-generate.png
ASM/ASM/evidence/pixel-api-35/real-final/11-practice-question.png
ASM/ASM/evidence/pixel-api-35/real-final/12-practice-correct-feedback.png
ASM/ASM/evidence/pixel-api-35/real-final/13-profile.png
ASM/ASM/evidence/pixel-api-35/real-final/14-notifications.png
ASM/ASM/evidence/pixel-api-35/real-final/15-logout-confirm.png
ASM/ASM/evidence/pixel-api-35/real-final/16-logged-out-welcome.png
ASM/ASM/evidence/pixel-api-35/real-final/MANIFEST_REAL.txt
ASM/ASM/evidence/pixel-api-35/real-final/06-gemini-network-logcat.txt
ASM/ASM/evidence/pixel-api-35/real-final/11-gemini-quiz-logcat.txt
ASM/ASM/evidence/pixel-api-35/real-final/tests-unit-practice.txt
ASM/ASM/evidence/pixel-api-35/real-final/tests-connected.txt
ASM/ASM/evidence/pixel-api-35/real-final/tests-connected-result.xml
ASM/ASM/evidence/pixel-api-35/real-final/nfr-cold-start.txt
ASM/ASM/evidence/pixel-api-35/real-final/nfr-gemini-ask-1.txt
ASM/ASM/evidence/pixel-api-35/real-final/nfr-gemini-ask-2.txt
ASM/ASM/evidence/pixel-api-35/real-final/nfr-gemini-ask-3.txt
ASM/ASM/evidence/pixel-api-35/real-final/nfr-network.txt
ASM/ASM/evidence/pixel-api-35/real-final/optional/**   # supplementary only, not part of the 16-image gate
PACKAGE_QA.md
SOFTWARE_PACKAGE_DRAFT.md
```

APK: **excluded / PENDING** — see “APK policy” below. Do not ship the current
debug APK until a sanitized rebuild is produced.

## Removal rules (must be applied before creating a ZIP)

Remove or never include:

1. **Secrets and local configuration**
   - `AI Study/ai.properties` (contains the real Gemini key; gitignored)
   - `AI Study/local.properties` (machine-specific SDK path)
   - `**/*.jks`, `**/*.keystore`, `**/*.p12`, `**/*.pem`, `**/*.key`
   - `**/.env`, `**/.env.*`, any `*secret*` file
2. **Build and IDE caches**
   - `AI Study/.gradle/**`, `AI Study/.idea/**`, `AI Study/.kotlin/**`
   - `AI Study/build/**`, `AI Study/app/build/**`
   - `AI Study/-679d5055b92ee0f8fd306b3ca6696a/**`
   - root `.gradle/**`, `.idea/**`, `.kotlin/**`
3. **VCS/tooling state**
   - `.git/**`, `.commandcode/**`, `opencode.jsonc`
4. **Non-final / demo evidence**
   - `ASM/ASM/evidence/pixel-api-35/*.png` (old 01, 02–22; none are final)
   - `ASM/ASM/evidence/pixel-api-35/MANIFEST.txt`
   - old `connectedDebugAndroidTest*.txt` / `*.xml` in the parent evidence folder
   - `docs/screenshots/**` (contain historical/demo screenshots)
   - `ASM-20260928T082606Z-1-001/**` (unrelated copy)
5. **Unneeded assignment material**
   - `Phase 0.pdf`, root `PHASE_1_STEP_BY_STEP_CODE.md` (duplicate of `docs/`)

After removal, verify that no `ai.properties`, `local.properties`, keystore, or
key-like string exists in the staged package. Do not claim a clean ZIP until the
actual archive contents are listed and scanned.

## APK policy (important)

The available APK is the fixed real-Gemini debug build:

- Size: 35,596,239 bytes
- SHA-256: `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`
- It embeds `GEMINI_API_KEY` in `BuildConfig` because it was built with the local
  `ai.properties`. A scan found key-like material in two DEX files.

Therefore the current APK must **not** be included in a clean public package.
Options (PENDING):

1. Rebuild a sanitized APK with an empty/placeholder key (offline `FakeAiClient`)
   and package that; or
2. Build a distribution APK with a key supplied at install/runtime (not currently
   supported by the app); or
3. Ship source only and let the recipient build with their own key.

The source package remains the authoritative deliverable until a sanitized APK
exists.

## Evidence policy

- The authoritative final screenshot gate is the **16 PNGs** in
  `real-final/` (`01`–`16`). Do not add, replace, or renumber them.
- `real-final/optional/` contains supplementary evidence (image question,
  offline error/recovery, login-after-logout). It is clearly labelled and is
  **not** part of the 16-image gate.
- Old demo / FakeAiClient screenshots are excluded from the package.
- `MANIFEST_REAL.txt` must continue to identify the 16 final images and the
  optional folder as supplementary.

## Packager checklist

1. Copy the include list into a staging directory.
2. Apply all removal rules.
3. Confirm `ai.properties`, `local.properties`, keystores, `.gradle`, `build`,
   and demo screenshots are absent.
4. Run a key-pattern scan over staging (do not print matches).
5. Decide the APK policy (sanitized rebuild or source-only).
6. Create the ZIP only after the checks pass.
7. Record the actual ZIP file list, SHA-256, and scan result in `PACKAGE_QA.md`.
