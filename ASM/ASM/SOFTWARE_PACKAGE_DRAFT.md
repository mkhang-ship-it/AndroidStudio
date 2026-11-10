# SOFTWARE PACKAGE — AI Study Mentor

Status: **staged and archived**. `PACKAGE_QA.md` records the checks actually
performed against the staging tree and the final ZIP.
This document defines the exact clean-package manifest and removal rules.

## Package identity

- Product: AI Study Mentor (`com.aistudy.aistudy`)
- Version: 1.0 (versionCode 1), debug build for evidence
- Source state: practice-scoring fix present on branch `Assignment_Report`
- Verified APK (built with real Gemini key): SHA-256
  `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`
  (NOT included — embeds key material)
- Sanitized APK (included): SHA-256
  `e7391113c18907d71b84e8e9856e02a0e3ece9ee447bebb32fdb526e82e18700`,
  35,174,179 bytes, built with empty key → offline `FakeAiClient`
- Verified device: Pixel_6_API_35 / Android 15 / API 35

## Exact clean-package manifest (include)

Include only the following paths (relative to the project root). The staging
root mirrors the project root.

```
README.md
build.ps1
docs/*.md
docs/uml/**
AI Study/.gitignore
AI Study/CONTRIBUTING.md
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
ASM/ASM/PACKAGE_QA.md
ASM/ASM/SOFTWARE_PACKAGE_DRAFT.md
AIStudy-sanitized-debug.apk
README_PACKAGE.txt
FILE_LIST.txt
```

## Removal rules (applied before creating the ZIP)

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
   - `ASM/ASM/deliverables/**` (the staging tree must not include itself)

## APK policy (applied)

The verified real-key APK must never be packaged: it embeds
`GEMINI_API_KEY` in `BuildConfig`/DEX. It is excluded.

Included instead:

- `AIStudy-sanitized-debug.apk` (35,174,179 bytes, SHA-256
  `e7391113c18907d71b84e8e9856e02a0e3ece9ee447bebb32fdb526e82e18700`).
- Rebuilt from an isolated copy with no `ai.properties`, no key in
  `local.properties`, and `GEMINI_API_KEY` unset.
- `BuildConfig.GEMINI_API_KEY == ""`; the app runs offline `FakeAiClient`.
- Scan of all 170 APK entries: 0 provider-key-like hits.
- Installed successfully on Pixel_6_API_35 (Android 15 / API 35).

Do not replace it with a keyed build. The source package remains authoritative.

## Evidence policy

- The authoritative final screenshot gate is the **16 PNGs** in
  `real-final/` (`01`–`16`). Do not add, replace, or renumber them.
- `real-final/optional/` contains supplementary evidence (image question,
  offline error/recovery, login-after-logout). It is clearly labelled and is
  **not** part of the 16-image gate.
- Old demo / FakeAiClient screenshots are excluded from the package.
- `MANIFEST_REAL.txt` must continue to identify the 16 final images and the
  optional folder as supplementary.

## Packager checklist (completed)

1. Copied the include list into `ASM/ASM/deliverables/software/staging/`.
2. Applied all removal rules.
3. Confirmed `ai.properties`, `local.properties`, keystores, `.gradle`, `build`,
   and demo screenshots are absent.
4. Ran a key-pattern scan over staging and over every ZIP member (0 hits).
5. Resolved the APK policy with a sanitized rebuild (included).
6. Created `ASM2_Software_Package.zip` after the checks passed.
7. Recorded the actual ZIP file list, SHA-256, and scan result in
   `PACKAGE_QA.md` and `FILE_LIST.txt`.
