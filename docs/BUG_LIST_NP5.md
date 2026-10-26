# BUG LIST — NP5 (severity → D2 future)

> Nguồn: gap đã verify trong `docs/ROADMAP_NEW_PHASES.md` §2 + code review + usability test (`docs/USABILITY_SCRIPT_NP5.md`).
> Quy ước: **Blocker** = chặn nộp/E2E crash · **High** = lỗ hổng/thiếu chức năng ảnh hưởng an toàn hay FR cốt lõi · **Med** = thiếu tinh chỉnh UX/chất lượng · **Low** = stretch/follow-up.
> Cột Trạng thái: **Open** = còn tồn tại ở freeze; **Fixed** = đã sửa trong NP1/NP4 (kèm bằng chứng).
> Link file là path tương đối `AI Study/app/src/main/java/com/aistudy/aistudy/`.

## Danh sách bug (cập nhật sau vòng cải thiện 2026-10-26)

| ID | Bug / Gap | Severity | FR/NFR liên quan | Trạng thái | File (đường dẫn tương đối) |
|---|---|---|---|---|---|
| BUG-01 | Lockout 5 lần sai sai mật khẩu lưu **in-memory** (`ConcurrentHashMap`) → mất khi app restart/process chết, attacker reset được bằng cách kill app | High | FR1 (Auth), NFR Security | **Fixed** (post-MVP 2026-10-26) | `data/security/LoginThrottle.kt` (`SharedPrefsLoginThrottle`), `data/repository/AuthRepositoryImpl.kt`; test `AuthRepositoryTest.login_lockout_vanConSauKhiTaoRepositoryMoi` |
| BUG-02 | `MISTAKE_ALERT` sinh theo **keyword cứng tiếng Anh** (`algebra`, `equation`, `formula`, `grammar`, `syntax`) → không khớp câu hỏi tiếng Việt/không dấu, alert sai hoặc không bắn | Med | FR10 (Notifications) | Open | `data/repository/QuestionRepositoryImpl.kt` (`MISTAKE_KEYWORDS`, dòng ~236; sinh notification dòng ~195-201) |
| BUG-03 | Progress chỉ hiển thị **số thô** (tổng câu, accuracy, XP, theo môn) — **chưa có AI-insights narrative** ("bạn đang yếu đại số, nên ôn…") theo yêu cầu FR8 | Med | FR8 (Progress) | **Fixed** (post-MVP 2026-10-26) — template offline, không tốn AI call | `domain/rules/ProgressInsights.kt`, `ui/screens/progress/ProgressScreen.kt`; test `ProgressInsightsTest`; ảnh `docs/screenshots/04_progress.png` |
| BUG-04 | **Leaderboard global/friends chưa có** — gamification mới local (XP + badge), thiếu so sánh giữa các user (cần backend + anti-cheat) | Low | FR9 (Gamification) | Open (defer → stretch S1) | `data/repository/GamificationRepositoryImpl.kt`, `domain/rules/BadgeCatalog.kt` |
| BUG-05 | **2FA chưa có verify flow** — schema `twoFactorSecret` đã tồn tại nhưng không có UI/secret generation/check mã → bật 2FA không thực sự bảo vệ | Low | NFR Security | Open (defer → stretch S2) | `domain/model/entity/User.kt` (`twoFactorSecret`, dòng ~38-40); `data/db/dao/UserDao.kt` (`setTwoFactorSecret`, dòng ~42-43) |
| BUG-06 | **Offline sync 2 chiều chưa có** — Room local-only, không có conflict policy, multi-device mất dữ liệu | Low | NFR Offline/Sync | Open (defer → stretch S3) | `data/db/AppDatabase.kt`, `data/db/dao/*` (toàn bộ local, không sync layer) |
| BUG-07 | **Markdown renderer thiếu** — câu trả lời AI có thể chứa markdown (prompt cho phép, `GeminiAiClient.kt:207`) nhưng UI hiển thị raw text → user thấy `**đậm**`/`#`/bảng lỗi | Med | FR4 (AI answer) | **Fixed** (post-MVP 2026-10-26) — markdown + LaTeX Unicode + phân số xếp tầng | `ui/common/Latex.kt`, `ui/common/MarkdownText.kt`; test `LatexTest`; ảnh `docs/screenshots/03_answer_math.png` |
| BUG-08 | **Token metering chưa thật** — `GeminiAiClient` luôn trả `inputTokens = 0`, không đọc `usageMetadata` từ response thật → không đo được AI-cost/cache-hit ratio | Med | NFR AI-cost/observability | **Fixed** (post-MVP 2026-10-26) — đọc `usageMetadata` và lưu DB | `domain/ai/GeminiAiClient.kt` (`usageTokens`); test `GeminiParsingTest` (3 case token) |
| BUG-09 | Prompt/đáp án luyện tập hiển thị raw `$...$`/LaTeX | Med | FR7 (Practice) | **Fixed** (post-MVP 2026-10-26) — dùng `MarkdownText` cho prompt, option, giải thích | `ui/screens/practice/PracticeScreen.kt`; ảnh `docs/screenshots/05_practice.png` |
| BUG-10 | Sticker "XP tuần này" thiếu số XP (dùng nhầm string không có placeholder) | Low | FR8/FR9 | **Fixed** (post-MVP 2026-10-26) | `ui/screens/progress/ProgressScreen.kt`, `res/values/strings.xml` (`progress_xp_week_summary`) |
| BUG-11 | Thời gian ôn tập gần như không lưu: `QuestionDetailScreen` launch trong `onDispose` bằng scope đã bị huỷ; Ask không đếm → hay hiện 0 phút | Med | FR8 (Progress) | **Fixed** (post-MVP 2026-10-26) — cộng 15 giây/lần, lifecycle-aware ở Ask + Detail | `ui/screens/ask/AskScreen.kt`, `ui/questiondetail/QuestionDetailScreen.kt`; ảnh `docs/screenshots/04_progress.png` |

## Fixed trong MVP (evidence cho P6 "feedback handled")

| ID | Bug / Gap cũ | Severity cũ | FR liên quan | Trạng thái | File đã sửa |
|---|---|---|---|---|---|
| BUG-F1 | Register không có `withTransaction` → user mồ côi khi insert một nửa thất bại | High | FR1 | Fixed (S03) | `data/repository/AuthRepositoryImpl.kt` (`withTransaction`, dòng ~78) |
| BUG-F2 | Register email trùng ném `SQLiteConstraintException` crash/áo msg thô | Med | FR1 | Fixed (S03) | `data/repository/AuthRepositoryImpl.kt` (catch + báo đẹp, dòng ~97) |
| BUG-F3 | `REVIEW_ANSWER` XP không nơi record → badge "người chăm ôn tập" không unlock | Med | FR9 | Fixed (S03) | `data/repository/QuestionRepositoryImpl.kt` (record XP, dòng ~188); `domain/rules/XpRules.kt` (`XP_REVIEW_ANSWER`) |
| BUG-F4 | `postDailySummaryIfDue` là dead code, không màn hình nào gọi | Med | FR10 | Fixed (S03) | `ui/shell/MainShell.kt` (gọi khi vào shell, dòng ~29); `data/repository/NotificationRepositoryImpl.kt` (idempotent, dòng ~58) |

## Tổng hợp theo severity (cập nhật 2026-10-26)

| Severity | Open | Fixed | Tổng |
|---|---|---|---|
| Blocker | 0 | 0 | 0 |
| High | 0 | 2 (BUG-01, BUG-F1) | 2 |
| Med | 1 (BUG-02) | 8 (BUG-03, 07, 08, 09, 11, F2, F3, F4) | 9 |
| Low | 3 (BUG-04, 05, 06) | 1 (BUG-10) | 4 |
| **Tổng** | **4** | **11** | **15** |

> **Cập nhật 2026-10-26:** BUG-01 (High, lockout in-memory) đã được sửa bằng `SharedPrefsLoginThrottle` và có instrumented test restart. BUG-03/07/08/09/10/11 được sửa trong vòng cải thiện sau MVP — xem `docs/IMPROVEMENT_LOG.md`. BUG-02 (keyword tiếng Anh) và 3 mục Low (leaderboard, 2FA, sync) vẫn chuyển sang D2 future.
>
> **DoD NP5:** Blocker Open = 0 ✔ (đạt). Trước vòng cải thiện, BUG-01 từng được chấp nhận rủi ro để không phá schema freeze; nay đã đóng bằng giải pháp không đổi schema (SharedPreferences).
