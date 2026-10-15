# D2 FUTURE WORK — NP5 (critical review: improvements sau MVP)

> Nguồn: gap còn **Open** trong `docs/BUG_LIST_NP5.md` + DoD/stretch trong `docs/ROADMAP_NEW_PHASES.md` §3.
> Mỗi mục: **Rationale** (tại sao làm) + **Evidence** (bug/test/code nào chỉ ra) + **Phạm vi đề xuất** (không thực hiện trong MVP).
> Link file path tương đối `AI Study/app/src/main/java/com/aistudy/aistudy/` trừ khi ghi khác.

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

## D2-4 — Markdown renderer cho AI answer

- **Rationale**: Prompt của Gemini cho phép trả lời bằng markdown (`GeminiAiClient.kt:207` ví dụ `"answerText": "loi giai day du, co the dung markdown"`), nhưng UI hiển thị plain `Text(answerText)` → user thấy ký hiệu `**`, `#`, bảng thô. Ảnh hưởng trực tiếp tới trải nghiệm lõi (đọc lời giải) — độ ưu tiên cao nhất trong danh sách D2 (Med/Open).
- **Evidence**: `docs/BUG_LIST_NP5.md` → BUG-07 (Med/Open); `ui/questiondetail/QuestionDetailScreen.kt` + `ui/screens/ask/AskScreen.kt:473` (`text = qa.answerText`); `ROADMAP_NEW_PHASES.md` §2 FR4 "thiếu markdown renderer (raw text)".
- **Phạm vi đề xuất (sau MVP)**: (1) parser markdown nhẹ nhàng (đậm/nghiêng/danh sách/code block) map sang `AnnotatedString` — không thêm dependency nặng (giữ nguyên tắc #4); (2) fallback hiển thị plain khi parse fail; (3) Compose test snapshot với answer mẫu markdown.

## D2-5 — Token metering thật (AI-cost observability)

- **Rationale**: DoD NP2 yêu cầu "sửa `inputTokens=0`" + đo cache-hit/reuse/AI-call ratio nhưng client thật vẫn hardcode `inputTokens = 0` → không có số liệu AI-cost thật, mọi quyết định cache/budget guard đang dựa trên ước lượng (chỉ `FakeAiClient` estimate được). Budget guard/ngày không có số liệu đầu vào thì vô nghĩa.
- **Evidence**: `docs/BUG_LIST_NP5.md` → BUG-08 (Med/Open); `domain/ai/GeminiAiClient.kt:274` (`inputTokens = 0`) vs `domain/ai/FakeAiClient.kt:67` (estimate — chỉ đúng trong test); cột `inputTokens` đã có sẵn trong `domain/model/entity/QuestionAnswer.kt:104` nên schema đã chuẩn bị, chỉ thiếu đọc `usageMetadata`; test liên quan `ReusePolicyTest` (mục 20 câu lặp → 1 AI call) chưa assert được token thật.
- **Phạm vi đề xuất (sau MVP)**: (1) parse `usageMetadata.promptTokenCount` / `candidatesTokenCount` từ Gemini JSON response thật; (2) log cache-hit/reuse/AI-call ratio theo ngày (logger hiện có); (3) budget guard dùng số thật (ngưỡng tokens/ngày) thay vì đếm call; (4) test: fixture response có `usageMetadata` → assert persist đúng token.

---

## Tổng hợp ưu tiên đề xuất (post-MVP)

| Ưu tiên | Mục | Lý do |
|---|---|---|
| 1 | D2-4 Markdown renderer | Ảnh hưởng UX lõi hằng ngày, chỉ cần UI-local, không backend |
| 2 | D2-5 Token metering thật | Mở khóa AI-cost evidence (P5/M4) + budget guard đúng nghĩa |
| 3 | D2-2 2FA verify | Đóng lỗ hổng "an toàn giả" trong NFR Security |
| 4 | D2-1 Leaderboard | FR9 đầy đủ nhưng cần backend + anti-cheat, chi phí lớn nhất |
| 5 | D2-3 Sync 2 chiều | Quan trọng cho multi-device, cần conflict design trước |

> Bug chi tiết từng mục: xem `docs/BUG_LIST_NP5.md` (BUG-04 → BUG-08).
