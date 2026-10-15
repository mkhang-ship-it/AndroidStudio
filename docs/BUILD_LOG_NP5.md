# BUILD LOG NP5 — assembleDebug (số thật, 2026-10-15)

- Lệnh: `.\gradlew.bat :app:assembleDebug --offline` trong `AI Study/` (sau `.\gradlew.bat --stop` để giải quyết xung đột port daemon).
- Kết quả: **BUILD SUCCESSFUL in 58s**, 39 tasks (4 executed, 35 up-to-date).
- APK: `AI Study/app/build/outputs/apk/debug/app-debug.apk` — **33.1 MB**, built 2026-10-15 17:37.
- Toolchain: AGP 9.3.3, Kotlin 2.2.10, KSP 2.3.12, Room 2.7.2, compileSdk/targetSdk 37, minSdk 33 (theo `gradle/libs.versions.toml` + `app/build.gradle.kts`).
- Lưu ý nộp bài: build offline được (dependencies đã cache). APK debug dùng để chụp screenshots + demo, không phải bản release.
