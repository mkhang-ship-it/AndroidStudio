# BUG LIST — NP5 (severity → D2 future)

> Nguồn: gap đã verify trong `docs/ROADMAP_NEW_PHASES.md` §2 + code review + usability test (`docs/USABILITY_SCRIPT_NP5.md`).
> Quy ước: **Blocker** = chặn nộp/E2E crash · **High** = lỗ hổng/thiếu chức năng ảnh hưởng an toàn hay FR cốt lõi · **Med** = thiếu tinh chỉnh UX/chất lượng · **Low** = stretch/follow-up.
> Cột Trạng thái: **Open** = còn tồn tại ở freeze; **Fixed** = đã sửa trong NP1/NP4 (kèm bằng chứng).
> Link file là path tương đối `AI Study/app/src/main/java/com/aistudy/aistudy/`.

## Open (còn tồn tại lúc đóng băng MVP)

| ID | Bug / Gap | Severity | FR/NFR liên quan | Trạng thái | File (đường dẫn tương đối) |
|---|---|---|---|---|---|
| BUG-01 | Lockout 5 lần sai sai mật khẩu lưu **in-memory** (`ConcurrentHashMap`) → mất khi app restart/process chết, attacker reset được bằng cách kill app | High | FR1 (Auth), NFR Security | Open | `data/repository/AuthRepositoryImpl.kt` (khai báo `failedAttempts`, dòng ~45-52, dùng ở ~113-134) |
| BUG-02 | `MISTAKE_ALERT` sinh theo **keyword cứng tiếng Anh** (`algebra`, `equation`, `formula`, `grammar`, `syntax`) → không khớp câu hỏi tiếng Việt/không dấu, alert sai hoặc không bắn | Med | FR10 (Notifications) | Open | `data/repository/QuestionRepositoryImpl.kt` (`MISTAKE_KEYWORDS`, dòng ~236; sinh notification dòng ~195-201) |
| BUG-03 | Progress chỉ hiển thị **số thô** (tổng câu, accuracy, XP, theo môn) — **chưa có AI-insights narrative** ("bạn đang yếu đại số, nên ôn…") theo yêu cầu FR8 | Med | FR8 (Progress) | Open | `ui/screens/progress/ProgressScreen.kt` (khối stats, dòng ~116-155); `data/repository/ProgressRepositoryImpl.kt` (chưa có generator narrative) |
| BUG-04 | **Leaderboard global/friends chưa có** — gamification mới local (XP + badge), thiếu so sánh giữa các user (cần backend + anti-cheat) | Low | FR9 (Gamification) | Open (defer → stretch S1) | `data/repository/GamificationRepositoryImpl.kt`, `domain/rules/BadgeCatalog.kt` |
| BUG-05 | **2FA chưa có verify flow** — schema `twoFactorSecret` đã tồn tại nhưng không có UI/secret generation/check mã → bật 2FA không thực sự bảo vệ | Low | NFR Security | Open (defer → stretch S2) | `domain/model/entity/User.kt` (`twoFactorSecret`, dòng ~38-40); `data/db/dao/UserDao.kt` (`setTwoFactorSecret`, dòng ~42-43) |
| BUG-06 | **Offline sync 2 chiều chưa có** — Room local-only, không có conflict policy, multi-device mất dữ liệu | Low | NFR Offline/Sync | Open (defer → stretch S3) | `data/db/AppDatabase.kt`, `data/db/dao/*` (toàn bộ local, không sync layer) |
| BUG-07 | **Markdown renderer thiếu** — câu trả lời AI có thể chứa markdown (prompt cho phép, `GeminiAiClient.kt:207`) nhưng UI hiển thị raw text → user thấy `**đậm**`/`#`/bảng lỗi | Med | FR4 (AI answer) | Open | `ui/questiondetail/QuestionDetailScreen.kt`, `ui/screens/ask/AskScreen.kt` (dòng ~473 `text = qa.answerText`) |
| BUG-08 | **Token metering chưa thật** — `GeminiAiClient` luôn trả `inputTokens = 0`, không đọc `usageMetadata` từ response thật → không đo được AI-cost/cache-hit ratio | Med | NFR AI-cost/observability | Open | `domain/ai/GeminiAiClient.kt` (dòng ~274 `inputTokens = 0`); cột `inputTokens` đã có sẵn trong `domain/model/entity/QuestionAnswer.kt:104` |

## Fixed trong MVP (evidence cho P6 "feedback handled")

| ID | Bug / Gap cũ | Severity cũ | FR liên quan | Trạng thái | File đã sửa |
|---|---|---|---|---|---|
| BUG-F1 | Register không có `withTransaction` → user mồ côi khi insert một nửa thất bại | High | FR1 | Fixed (S03) | `data/repository/AuthRepositoryImpl.kt` (`withTransaction`, dòng ~78) |
| BUG-F2 | Register email trùng ném `SQLiteConstraintException` crash/áo msg thô | Med | FR1 | Fixed (S03) | `data/repository/AuthRepositoryImpl.kt` (catch + báo đẹp, dòng ~97) |
| BUG-F3 | `REVIEW_ANSWER` XP không nơi record → badge "người chăm ôn tập" không unlock | Med | FR9 | Fixed (S03) | `data/repository/QuestionRepositoryImpl.kt` (record XP, dòng ~188); `domain/rules/XpRules.kt` (`XP_REVIEW_ANSWER`) |
| BUG-F4 | `postDailySummaryIfDue` là dead code, không màn hình nào gọi | Med | FR10 | Fixed (S03) | `ui/shell/MainShell.kt` (gọi khi vào shell, dòng ~29); `data/repository/NotificationRepositoryImpl.kt` (idempotent, dòng ~58) |

## Tổng hợp theo severity (lúc đóng băng)

| Severity | Open | Fixed | Tổng |
|---|---|---|---|
| Blocker | 0 | 0 | 0 |
| High | 1 (BUG-01) | 1 | 2 |
| Med | 4 (BUG-02, 03, 07, 08) | 2 | 6 |
| Low | 3 (BUG-04, 05, 06) | 0 | 3 |

> **DoD NP5**: Blocker Open = 0 ✔ (đạt). BUG-01 (High, lockout in-memory) được chấp nhận rủi ro có chủ đích để không phá schema freeze — chuyển sang D2 (`docs/D2_FUTURE_NP5.md`) cùng BUG-02..08.
