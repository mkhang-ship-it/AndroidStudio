# TEST REPORT NP5 — unit tests (số thật, 2026-10-15)

- Lệnh: `.\gradlew.bat :app:testDebugUnitTest --offline` → **BUILD SUCCESSFUL**.
- Tổng hợp từ `AI Study/app/build/test-results/testDebugUnitTest/*.xml`: **8 files, 76 tests, 0 fail, 0 error, 0 skipped**.
- Gồm: XpRulesTest, QuestionSimilarityTest, GeminiParsingTest, FakeAiClientTest, ReusePolicyTest, ConvertersTest, AbuseFilterTest (mới, 5 case), +1.
- Instrumented tests (mới, chưa chạy vì không có emulator ở đây): `androidTest/.../db/UserDaoTest.kt` (5 case), `.../db/QuestionAnswerDaoTest.kt` (9 case), `.../repo/AuthRepositoryTest.kt` (12 case), `.../ui/MainShellSmokeTest.kt` (9 case). Chạy khi có emulator: `.\gradlew.bat :app:connectedDebugAndroidTest`. `compileDebugAndroidTestKotlin` đã BUILD SUCCESSFUL.
