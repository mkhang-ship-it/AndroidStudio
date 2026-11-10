AI STUDY MENTOR ??? SOFTWARE PACKAGE
==================================

Package: ASM2_Software_Package.zip
Product: AI Study Mentor (com.aistudy.aistudy)
Version: 1.0 (versionCode 1), debug build for evidence
Prepared: 2026-11-10

This archive contains source code, Gradle wrapper/configuration, project
documentation, runtime/test evidence, and one sanitized debug APK. It contains
no API key, no local SDK path, no keystore, and no IDE/build cache.

------------------------------------------------------------------
1. PACKAGE CONTENTS (top level)
------------------------------------------------------------------
README.md                     Project overview
build.ps1                     Convenience build script
docs/                         Design, test plan, roadmap, UML sources/PNGs
AI Study/                     Android application project (Kotlin + Compose)
ASM/ASM/                      Assignment report support docs and evidence
AIStudy-sanitized-debug.apk   Sanitized debug APK (no provider key)
README_PACKAGE.txt            This file
FILE_LIST.txt                 Exact list of every file in this archive

Key paths:
  AI Study/app/src/main/                 Application source (98 .kt files)
  AI Study/app/src/test/                 JVM unit tests (13 .kt files)
  AI Study/app/src/androidTest/          Instrumented tests (5 .kt files)
  AI Study/app/schemas/                  Room schema JSON
  AI Study/ai.properties.example         Key template (empty value)
  AI Study/local.properties.example      SDK path template (empty value)
  ASM/ASM/USER_GUIDE.md                  End-user guide
  ASM/ASM/NFR_MEASUREMENTS.md            Non-functional measurements
  ASM/ASM/PIXEL_API35_RUNTIME_EVIDENCE.md Runtime evidence narrative
  ASM/ASM/AVD_API35_SETUP.md             Emulator setup
  ASM/ASM/PACKAGE_QA.md                  Package QA record
  ASM/ASM/SOFTWARE_PACKAGE_DRAFT.md      Package manifest and policy
  ASM/ASM/evidence/pixel-api-35/real-final/
      16 authoritative final screenshots (01-16), MANIFEST_REAL.txt,
      Gemini/network/test/NFR logs, and optional/ supplementary evidence.

The 16 PNGs in real-final/ are the authoritative screenshot gate. The
real-final/optional/ folder (8 PNG + 5 TXT) is supplementary only.

------------------------------------------------------------------
2. SANITIZED APK POLICY
------------------------------------------------------------------
Included file: AIStudy-sanitized-debug.apk
  Size:    35,174,179 bytes
  SHA-256: e7391113c18907d71b84e8e9856e02a0e3ece9ee447bebb32fdb526e82e18700

Policy:
  - This APK was rebuilt from an isolated copy of the project with no
    ai.properties, no key in local.properties, and GEMINI_API_KEY unset.
  - BuildConfig.GEMINI_API_KEY is an empty string; the app runs the offline
    FakeAiClient (simulated answers).
  - A binary scan of all 170 APK entries found no provider-key-like bytes.
  - It installed successfully on Pixel_6_API_35 (Android 15 / API 35).
  - Do NOT distribute any debug APK built with a real key: BuildConfig embeds
    the key in DEX. Rebuild with your own key only for local runs and never
    ship that artifact.
  - The source package is authoritative; the sanitized APK is a convenience
    preview of the offline mode.

------------------------------------------------------------------
3. BUILD INSTRUCTIONS
------------------------------------------------------------------
Requirements:
  - Android Studio (or command-line Android SDK) with:
      * JDK 17 or newer (the project also builds with the Android Studio JBR)
      * Android SDK platform 35 and 37 (compileSdk 37, targetSdk 37, minSdk 33)
      * Android build-tools 36.0.0 (or compatible)
  - Gradle is provided by the wrapper (Gradle 9.5.0). No manual Gradle install.

Steps:
  1. Copy AI Study/local.properties.example to AI Study/local.properties and
     set sdk.dir to your Android SDK path. Example (Windows):
       sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
  2. Optional, for real Gemini answers: copy AI Study/ai.properties.example to
     AI Study/ai.properties and set GEMINI_API_KEY (see section 4). If the key
     is absent or empty, the app runs FakeAiClient offline.
  3. Build (Windows):
       cd "AI Study"
       gradlew.bat assembleDebug
     Build (macOS/Linux):
       cd "AI Study"
       ./gradlew assembleDebug
  4. Output APK:
       AI Study/app/build/outputs/apk/debug/app-debug.apk

Notes:
  - ai.properties and local.properties are gitignored. Never commit them.
  - The local.properties generated in step 1 is machine-specific; it is not
    part of this package.

------------------------------------------------------------------
4. LOCAL GEMINI CONFIGURATION (NO CREDENTIALS INCLUDED)
------------------------------------------------------------------
The build reads the key in this order:
  1. GEMINI_API_KEY in AI Study/ai.properties
  2. GEMINI_API_KEY in AI Study/local.properties
  3. GEMINI_API_KEY environment variable

To enable real Gemini answers:
  1. Obtain your own key from Google AI Studio.
  2. Copy AI Study/ai.properties.example to AI Study/ai.properties.
  3. Set GEMINI_API_KEY=<your key> in that file (optionally set GEMINI_MODEL;
     the example uses gemini-flash-lite-latest).
  4. Rebuild. The key becomes BuildConfig.GEMINI_API_KEY at build time.

To run fully offline: leave the key empty or omit the file. The app then uses
FakeAiClient and makes no Gemini network calls.

No credential, token, or key value is present in this archive. The example
properties files ship with empty values only.

------------------------------------------------------------------
5. INSTALL INSTRUCTIONS
------------------------------------------------------------------
With a connected device or running emulator (USB debugging enabled):
  adb install -r AIStudy-sanitized-debug.apk

Or install the APK you built yourself:
  adb install -r "AI Study/app/build/outputs/apk/debug/app-debug.apk"

Verified environment for the packaged APK:
  - AVD Pixel_6_API_35, Android 15 / API 35 (emulator-5554)
  - Application ID com.aistudy.aistudy, versionName 1.0, versionCode 1

------------------------------------------------------------------
6. TESTS (for reference)
------------------------------------------------------------------
Recorded in ASM/ASM/evidence/pixel-api-35/real-final/:
  - tests-unit-practice.txt        Focused practice-scoring unit tests (7/7)
  - tests-connected.txt            39/39 connected instrumented tests
  - tests-connected-result.xml     JUnit XML result
Unit-test summary recorded in ASM/ASM/PACKAGE_QA.md (159/159).

Run locally:
  cd "AI Study"
  gradlew.bat testDebugUnitTest
  gradlew.bat connectedDebugAndroidTest    (device/emulator required)

------------------------------------------------------------------
7. INTEGRITY AND SECRET SCAN
------------------------------------------------------------------
- No ai.properties, local.properties, keystore, .env, or private key exists in
  this archive.
- No .git, .idea, .gradle, build/, .kotlin, .commandcode, or parent-level demo
  screenshots are included.
- A pattern scan over the staging tree and every ZIP member was performed; see
  ASM/ASM/PACKAGE_QA.md for the recorded result.
- The archive checksum (SHA-256) is recorded in the copy of ASM/ASM/PACKAGE_QA.md
  and in the copy of this file delivered beside the ZIP.

------------------------------------------------------------------
8. PENDING / NOT INCLUDED
------------------------------------------------------------------
- A controlled demo account is not supplied; create a local account in the app.
- OS-level push notification permission flow is not exercised.
- The APK in this package is offline (FakeAiClient) by design.

------------------------------------------------------------------
9. FINAL ARCHIVE INTEGRITY (EXTERNAL RECORD)
------------------------------------------------------------------
Archive:   ASM2_Software_Package.zip
Members:   243 files
Size:      16,680,560 bytes
SHA-256:   340b92e27410090bdf05262e1674274eff1c141eb8ad9f32c87360355e816d90

Verification (performed 2026-11-10):
  - 243 member files listed after creation.
  - 0 provider-key-like strings across all members and the staging tree.
  - 0 parent-level demo PNGs; exactly 16 authoritative final PNGs; 8 optional
    supplementary PNGs.
  - No ai.properties, local.properties, keystore, .git/.idea/.gradle/build.
  - Included APK SHA-256:
    e7391113c18907d71b84e8e9856e02a0e3ece9ee447bebb32fdb526e82e18700
