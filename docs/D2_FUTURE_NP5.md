# D2 FUTURE WORK — NP5 (critical review: improvements sau MVP)

> Nguồn: gap còn **Open** trong `docs/BUG_LIST_NP5.md` + DoD/stretch trong `docs/ROADMAP_NEW_PHASES.md` §3.
> Mỗi mục: **Rationale** (tại sao làm) + **Evidence** (bug/test/code nào chỉ ra) + **Phạm vi đề xuất** (không thực hiện trong MVP).
> Link file path tương đối `AI Study/app/src/main/java/com/aistudy/aistudy/` trừ khi ghi khác.

## Đã xử lý sau MVP (2026-10-26)

- **D2-4 Markdown + LaTeX renderer** → đã làm: `ui/common/Latex.kt`, `ui/common/MarkdownText.kt` (markdown + công thức Unicode + phân số xếp tầng bằng Compose inline content), test `LatexTest`; ảnh `docs/screenshots/03_answer_math.png`.
- **D2-5 Token metering thật** → đã làm: `GeminiAiClient.usageTokens()` đọc `usageMetadata`, repository lưu `inputTokens`/`outputTokens`, test `GeminiParsingTest`.
- **BUG-01 lockout bền vững** → đã làm: `data/security/LoginThrottle.kt` + `SharedPrefsLoginThrottle`, test restart.
- **BUG-03 progress insights** → đã làm: `domain/rules/ProgressInsights.kt` (template offline, tối đa 3 gợi ý), test `ProgressInsightsTest`.
- **BUG-09/10/11** → render LaTeX trong Luyện tập, sửa nhãn XP tuần, đo thời gian ôn tập lifecycle-aware.

Chi tiết + screenshot: [`IMPROVEMENT_LOG.md`](IMPROVEMENT_LOG.md).

## D2-1 — Leaderboard global (server-backed)

- **Rationale**: Gamification hiện chỉ local (XP + badge của từng user) — FR9 nêu "so sánh tiến bộ với bạn bè" nhưng không có backend nên không leaderboard cross-user. Cần server + anti-cheat (chống set XP giả) trước khi làm; giữ nguyên nguyên tắc D-1/D-5 "không Hilt, không Retrofit" → backend tách riêng (HTTP endpoint + HttpURLConnection hoặc service ngoài).
- **Evidence**: `docs/BUG_LIST_NP5.md` → BUG-04 (Low/Open); code hiện tại chỉ có `data/repository/GamificationRepositoryImpl.kt` + `domain/rules/BadgeCatalog.kt` (không model global-rank); `ROADMAP_NEW_PHASES.md` §3 stretch S1.
- **Phạm vi đề xuất (sau MVP)**: (1) schema server (user_id, xp_total, updated_at) + sync endpoint; (2) anti-cheat: server validate `XpEvent` thay vì client-trusted; (3) UI `LeaderboardScreen` top-N + rank của mình; (4) test: contract test + fake server trong `FakeAiClientTest` style.

## D2-2 — 2FA thật (verify flow)

- **Rationale**: NFR Security yêu cầu 2FA, MVP mới có **schema** (`twoFactorSecret` nullable) nhưng không có sinh secret/kiểm mã → bật "2FA" là giả, tạo cảm giác an toàn sai (worse than none). Cần TOTP chuẩn (RFC 6238) + UI quét QR + bước verify bắt buộc ở login.
- **Evidence**: `docs/BUG_LIST_NP5.md` → BUG-05 (Low/Open); `domain/model/entity/User.kt:38-40` (`twoFactorSecret` đã có cột); `data/db/dao/UserDao.kt:42-43` (`setTwoFactorSecret` đã có nhưng không UI nào gọi — verify bằng grep toàn `main/java`); `ROADMAP_NEW_PHASES.md` §3 stretch S2.
- **Phạm vi đề xuất (sau MVP)**: (1) generate+store TOTP secret (AES-GCM qua `ImageStore`-style); (2) màn hình quét QR + nhập 6 mã để verify; (3) ép verify ở login khi đã bật; (4) unit test TOTP vector chuẩn; (5) recovery code 1 lần.

## D2-3 — Offline sync 2 chiều (conflict policy)

- **Rationale**: Room local-only → multi-device hoặc reinstall mất dữ liệu học tập; FR/NFR offline yêu cầu "đọc được offline" (đạt) nhưng "đồng bộ 2 chiều" (PARTIAL). Thiếu conflict policy rõ ràng (last-write-wins vs merge) nên cần thiết kế trước khi code.
- **Evidence**: `docs/BUG_LIST_NP5.md` → BUG-06 (Low/Open); `data/db/AppDatabase.kt` + `data/db/dao/*` toàn local, không sync layer/queue; `ROADMAP_NEW_PHASES.md` §2 (NFR "thiếu offline-sync-2-chiều") + §3 stretch S3.
- **Phạm vi đề xuất (sau MVP)**: (1) thêm cột `updatedAt`/`dirty` vào `QuestionAnswer`, `XpEvent`, `UserBadge`; (2) outbound queue (Outbox table) flush khi có mạng; (3) conflict policy: LWW theo `updatedAt` cho history, merge-set cho badge/XP; (4) test: `QuestionAnswerDaoTest` mở rộng scenario offline→reconnect.

## D2-4 — Markdown + LaTeX renderer cho AI answer (✅ đã xử lý 2026-10-26)

- **Trạng thái:** đã triển khai trong `ui/common/Latex.kt` + `ui/common/MarkdownText.kt`; giữ nguyên nguyên tắc không thêm dependency. Hỗ trợ markdown block/inline + công thức Unicode + phân số xếp tầng; test `LatexTest` (18 case). Giới hạn: chưa hỗ trợ `align`, ma trận phức tạp, phân số lồng nhiều tầng.
- **Rationale (lịch sử)**: Prompt của Gemini cho phép trả lời bằng markdown (`GeminiAiClient.kt:207` ví dụ `"answerText": "loi giai day du, co the dung markdown"`), nhưng UI hiển thị plain `Text(answerText)` → user thấy ký hiệu `**`, `#`, bảng thô. Ảnh hưởng trực tiếp tới trải nghiệm lõi (đọc lời giải) — độ ưu tiên cao nhất trong danh sách D2 (Med/Open).
- **Evidence**: `docs/BUG_LIST_NP5.md` → BUG-07 (Med, **Fixed 2026-10-26**); code cũ hiển thị `text = qa.answerText` trần trong `QuestionDetailScreen.kt` + `AskScreen.kt`; `ROADMAP_NEW_PHASES.md` §2 FR4 từng ghi "thiếu markdown renderer (raw text)".
- **Phạm vi đề xuất (sau MVP)**: (1) parser markdown nhẹ nhàng (đậm/nghiêng/danh sách/code block) map sang `AnnotatedString` — không thêm dependency nặng (giữ nguyên tắc #4); (2) fallback hiển thị plain khi parse fail; (3) Compose test snapshot với answer mẫu markdown.

## D2-5 — Token metering thật (AI-cost observability) (✅ đã xử lý 2026-10-26)

- **Trạng thái:** `GeminiAiClient.usageTokens()` đọc `promptTokenCount` / `candidatesTokenCount`; `parseAnswer()` nhận token; `QuestionRepositoryImpl` đã lưu xuống DB. Test 3 case trong `GeminiParsingTest`. Việc còn lại: log ratio theo ngày và budget guard dùng số thật.
- **Rationale (lịch sử)**: DoD NP2 yêu cầu "sửa `inputTokens=0`" + đo cache-hit/reuse/AI-call ratio nhưng client thật vẫn hardcode `inputTokens = 0` → không có số liệu AI-cost thật, mọi quyết định cache/budget guard đang dựa trên ước lượng (chỉ `FakeAiClient` estimate được). Budget guard/ngày không có số liệu đầu vào thì vô nghĩa.
- **Evidence**: `docs/BUG_LIST_NP5.md` → BUG-08 (Med, **Fixed 2026-10-26**); code cũ hardcode `inputTokens = 0` trong `domain/ai/GeminiAiClient.kt`, nay đã thay bằng `usageTokens()`; cột `inputTokens` có sẵn trong `domain/model/entity/QuestionAnswer.kt:104`; test `GeminiParsingTest` đã assert token.
- **Phạm vi đề xuất (sau MVP)**: (1) parse `usageMetadata.promptTokenCount` / `candidatesTokenCount` từ Gemini JSON response thật; (2) log cache-hit/reuse/AI-call ratio theo ngày (logger hiện có); (3) budget guard dùng số thật (ngưỡng tokens/ngày) thay vì đếm call; (4) test: fixture response có `usageMetadata` → assert persist đúng token.

## D2-6 — Streaming thật từ Gemini (SSE)

- **Rationale**: Hiện tại `TypewriterMarkdown` chỉ là **hiệu ứng hiện chữ phía client** sau khi nhận đủ JSON. Người dùng vẫn phải chờ toàn bộ câu trả lời (3–10 giây). Muốn chữ hiện ngay khi model sinh cần gọi `streamGenerateContent?alt=sse`, đọc từng chunk và cập nhật UI.
- **Vì sao chưa làm trong vòng 4 giờ**: Gemini đang trả **một JSON** chứa nhiều trường (`answerText`, `difficulty`, `subject`, `keyConcepts`…). Stream SSE sẽ trả JSON dở dang, không parse được bằng `JSONObject`; muốn đúng cần đổi prompt sang plain text + gọi thêm lần 2 để lấy metadata (tăng cost) hoặc viết incremental JSON parser (rủi ro cao). Typewriter là trade-off hợp lý cho demo.
- **Phạm vi đề xuất**: (1) thêm `streamAnswer()` vào `AiClient` với SSE parser; (2) tách metadata khỏi answer text (1 call stream + 1 call metadata, hoặc bỏ metadata khỏi flow stream); (3) UI state `streamingText` + nút dừng; (4) test parser SSE bằng fixture nhiều chunk.

---

## Tổng hợp ưu tiên đề xuất (post-MVP)

| Ưu tiên | Mục | Lý do |
|---|---|---|
| 1 | D2-2 2FA verify | Đóng lỗ hổng "an toàn giả" trong NFR Security |
| 2 | D2-1 Leaderboard | FR9 đầy đủ nhưng cần backend + anti-cheat, chi phí lớn nhất |
| 3 | D2-3 Sync 2 chiều | Quan trọng cho multi-device, cần conflict design trước |
| 4 | D2-6 Streaming thật | Trải nghiệm tốt hơn nhưng cần đổi protocol/prompt, cost cao hơn |
| 5 | BUG-02 mistake alert tiếng Việt | Nhỏ, chỉnh keyword/heuristic local |

> Bug chi tiết từng mục: xem `docs/BUG_LIST_NP5.md` (BUG-02, BUG-04 → BUG-06 còn Open).
