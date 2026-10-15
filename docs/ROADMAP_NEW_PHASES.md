# ROADMAP MỚI — AI Study Mentor (từ ASM gốc, thay Phase 1 View cũ)

> Nguồn: `ASM-20260928T082606Z-1-001/ASM/Assignment part 1.docx` + `Assignment part 2.docx`.
> Code hiện tại: `AI Study/` Compose + Clean Architecture (ui/domain/data/platform), Room 7 bảng, PBKDF2 + EncryptedPrefs + AES-GCM.
> Trạng thái: FR1-5, FR7 DONE; FR6/FR8 đủ MVP cơ bản; FR9/FR10 PARTIAL; NFR thiếu age-gate COPPA, 2FA/abuse thật, offline-sync-2-chiều, token metering.

## 1. Đối chiếu nhanh brief

- Part 1: P1+P2 slide (~30), P3+M2 tools ~400 từ, M1 design+UML+test plan ~400 từ, D1 cá nhân ~400 từ/người.
- Part 2: P4+M3 survey ~500 từ; P5+M4 build app + report 2500-3000 từ (how/what/how/difficulties/fixes + screenshots + stages/tools) + evaluate vs FR/NFR; P6+M5+D2 critical review + feedback handled/not-handled + future.
- MVP 14 tuần core: Ask, Answer, History, Basic progress.
- Ràng buộc: budget + AI-cost (cache exact, similar reuse, balance quality/cost); junior cần training (Gemini/ChatGPT, Compose, secure handling); monetization optional — KHÔNG làm ở MVP.

## 2. Gap hiện tại (đã verify)

| Nhóm | DONE | PARTIAL / MISSING |
|---|---|---|
| FR3 Ask text+image, FR4 AI answer, FR5 improvement, FR7 practice | `AskScreen.kt`, `GeminiAiClient.kt`, `FakeAiClient.kt`, `QuestionRepositoryImpl.kt`, `QuestionDetailScreen.kt`, `QuizRepositoryImpl.kt` | FR4 thiếu markdown renderer (raw text) |
| FR1 Auth, FR2 onboarding | `AuthRepositoryImpl.kt`, `PasswordHasher.kt` (PBKDF2 120k), `EncryptedPrefsSessionStore.kt`, `AuthGateViewModel.kt`, `OnboardingScreen.kt` | Thiếu lockout 5 lần, thiếu `withTransaction` register (`AuthRepositoryImpl.kt:60-77`), thiếu catch `SQLiteConstraintException` |
| FR6 History, FR8 Progress | `HistoryScreen.kt`, `HistoryViewModel.kt`, `QuestionAnswerDao.kt`, `ProgressRepositoryImpl.kt`, `ProgressScreen.kt` | Thiếu frequent-topics suggest; AI-insights mới số thô chưa narrative |
| FR9 Gamification, FR10 Notifications | `XpRules.kt`, `BadgeCatalog.kt`, `GamificationRepositoryImpl.kt`, `Notification*.kt`, `NotificationsScreen.kt` | Thiếu avatar/themes/leaderboard global; `REVIEW_ANSWER` XP không nơi record; `REVIEW_REMINDER`/`MISTAKE_ALERT` chưa sinh; `postDailySummaryIfDue` dead code |
| NFR Security/Privacy/Offline | Encrypted prefs + AES image store + Room local | Thiếu age-gate COPPA, 2FA thật, abuse filter thật, offline-sync-2-chiều, token log (`inputTokens=0`), benchmarks |
| Evidence P5/M4/P6 | Design doc D-1..D-12, 13 UML, test plan ~90 ca, 6 unit test files | Thiếu screenshots, APK+build log, `AGILE_SPRINT_LOG.md`, 20-30 instrumented tests + report/coverage, usability test, bug list |

## 3. Phase mới (NP0-NP5 + stretch)

### NP0 — Training + CI (tuần 1-2)
- Mục tiêu: junior chạy được `gradlew test`, key chỉ trong `local.properties`, đóng băng style + CI.
- Chạm: `CONTRIBUTING.md` mới, `.github/workflows/*` (nếu có), `local.properties.example`.
- DoD: `gradlew test` xanh; mỗi người 1 PR nhỏ qua review.
- Evidence: M4 (methodology setup).

### NP1 — Ask → Answer hardening (tuần 2-5)
- Mục tiêu: đường Ask→AI→Answer ổn định: timeout/retry backoff, loading/error/retry/offline/fake-no-key.
- Chạm: `domain/ai/GeminiAiClient.kt`, `FakeAiClient.kt`, `ui/screens/ask/AskScreen.kt`, `ui/questiondetail/QuestionDetailScreen.kt`.
- DoD: `GeminiParsingTest` mở rộng; Compose test 6 state; không crash khi thiếu key; mọi answer đi qua `ReusePolicy`.
- AI-cost: trung lập (không phá cache).

### NP2 — AI-cost + observability (tuần 4-6, //NP1)
- Mục tiêu: cache exact + similar-reuse đo được.
- Chạm: `data/repository/ReusePolicy.kt`, `domain/rules/QuestionSimilarity.kt`, logger token/call, budget guard/ngày.
- DoD: case VN không dấu + đa chủ ngữ; 20 câu lặp → 1 AI call thật; log ra cache-hit/reuse/AI-call ratio; sửa `inputTokens=0`.
- AI-cost: giảm mạnh, ROI cao nhất.

### NP3 — History + Progress cơ bản (tuần 6-9)
- Mục tiêu: search/bookmark/by-subject + dashboard total/subject/accuracy/time.
- Chạm: `ui/screens/history/HistoryScreen.kt`, `ui/history/HistoryViewModel.kt`, `data/db/dao/QuestionAnswerDao.kt`, `ui/screens/progress/ProgressScreen.kt`, `data/repository/ProgressRepositoryImpl.kt`.
- Bù MVP: frequent-topics suggest (query group-by đơn giản); AI-insights bản text template trước.
- DoD: DAO in-memory test; UI test search/filter; query <200ms; offline đọc được.
- AI-cost: 0 call mới (local DB).

### NP4 — FR9/10 tối thiểu + NFR chặn (tuần 9-11)
- Mục tiêu: đủ dùng, không làm stretch.
- Chạm: `NotificationRepositoryImpl.kt` (sinh REVIEW_REMINDER/MISTAKE_ALERT, wire `postDailySummaryIfDue` vào `MainShell`), `GamificationRepositoryImpl.kt` (record `REVIEW_ANSWER`), age-gate COPPA đơn giản + text parental note, abuse keyword filter, `AuthRepositoryImpl.kt` (+transaction + constraint catch + lockout).
- KHÔNG làm: leaderboard global/friends (cần server), 2FA verify, sync-2-chiều, monetization.
- DoD: reminder tạo được; lockout sau 5 sai; register trùng email báo đẹp; không user mồ côi.

### NP5 — Đóng băng MVP + Evidence Part 2 (tuần 11-14)
- Mục tiêu: E2E splash→ask→history→progress + gói nộp bài.
- Việc: 20-30 instrumented tests (DAO + repo + Compose smoke), `connectedAndroidTest` report + coverage, benchmark NFR, usability 3-5 users, 6-8 screenshots + caption, APK + build log, `docs/AGILE_SPRINT_LOG.md` (14 sprint: goal/done/blocker/retro), bug list severity → D2 future.
- DoD: E2E xanh; crash-free; bug blocker = 0; đủ artifact cho P5/M4/P6/M5/D2.

### Stretch (sau MVP, ngoài 14 tuần)
- S1 leaderboard global (cần backend + anti-cheat).
- S2 2FA thật. S3 offline-sync-2-chiều (conflict policy). S4 monetization.

## 4. Nguyên tắc
1. NP2 trước NP3 vì code nền đã có → ROI nhanh.
2. Mỗi phase 1-2 người, ownership theo màn/gói, cho // khi độc lập.
3. Mọi DoD phải có test tự động, không "dùng thử ok".
4. Giữ D-1/D-5: không Hilt, không Retrofit (HttpURLConnection + org.json).
5. Không phase MVP nào thêm dependency nặng hay hook monetization.
