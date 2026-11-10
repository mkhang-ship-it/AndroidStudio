# AI Study Mentor — User Guide

## 1. Purpose

AI Study Mentor is an Android learning application for submitting academic questions, receiving Gemini explanations, reviewing saved questions, practising generated quizzes, and monitoring progress.

## 2. Requirements

- Android Studio with the project JDK and Android SDK configured.
- Android device or emulator. The verified evidence target is `Pixel_6_API_35` (Android 15/API 35).
- Network access for Gemini requests.
- A Gemini API key supplied locally through `AI Study/ai.properties`; never commit or publish this file.

## 3. Build and install

1. Open the `AI Study` project in Android Studio.
2. Create `AI Study/ai.properties` from the local example if required.
3. Add the key locally using the project’s expected property name. Do not copy the key into source code, screenshots, logs, or the report.
4. Connect an emulator/device.
5. Run `gradlew.bat assembleDebug` or launch the app from Android Studio.
6. Install the generated debug APK.

## 4. First use

1. Launch the app.
2. Register with a display name, email address and password.
3. Complete the onboarding screens.
4. Sign in with the registered account.

A controlled demo account has not been supplied yet; `[PENDING]`.

## 5. Ask Gemini

1. Open the Ask screen.
2. Enter an academic question.
3. Select the available learning context when shown.
4. Submit the question.
5. Wait for the Gemini response.
6. Review the explanation, steps and final answer.

The verified runtime evidence includes a real Gemini answer and HTTP 200 network log. AI answers may still require human verification.

## 6. History and detail

Open History to review saved questions. Select an item to open its detail view. The verified real-final evidence includes a saved real question and its detail screen.

## 7. Progress and practice

Progress presents learning indicators derived from stored activity. Practice generates quiz content through the AI client. Select an answer to receive feedback. The corrected build displays positive feedback when the selected answer matches an answer key represented as a letter, number, or answer text.

## 8. Profile, notifications and logout

Use Profile to view account information and available settings. Use Notifications to review generated notification items. Use Logout and confirm the action to return to the logged-out welcome screen.

## 9. Troubleshooting

- **AI is unavailable:** check network access and verify the local property file exists without exposing its value.
- **Simulation banner appears:** the build is running without a configured key and uses the fake client; do not use that session as real Gemini evidence.
- **Build fails after SDK changes:** confirm the project SDK path, JDK, Gradle wrapper and Android API 35/37 packages.
- **No saved history:** complete a successful question submission and allow the response to be stored before opening History.

## 10. Known limitations

- A demo account procedure is not yet supplied.
- Formal performance measurements, offline measurements and usability observations remain `[PENDING]`.
- Image-question, formula/loading and offline flows were not included in the final real-evidence set.
- The app is local-first and does not provide a server-backed multi-device history.

## 11. Evidence reference

Final runtime screenshots and test logs are stored under `ASM/ASM/evidence/pixel-api-35/real-final/`. Older root screenshots are marked DEMO/NON-FINAL.
