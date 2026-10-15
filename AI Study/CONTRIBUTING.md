# CONTRIBUTING — AI Study

> Áp dụng cho mọi PR vào `AI Study/`. Ngắn gọn: 3 việc bắt buộc + 4 quy ước code.

## 1. Setup key (bắt buộc đọc trước)

- Copy `local.properties.example` → `local.properties`, điền `sdk.dir` + `GEMINI_API_KEY`.
- `local.properties` **đã nằm trong `.gitignore` → KHÔNG BAO GIỜ commit** (kiểm tra `git status` trước khi `git add`).
- Key rỗng thì không sao: app tự chạy `FakeAiClient`, unit test vẫn chạy được.
- Check nhanh (mỗi PR sửa file này phải xanh): `git status` không hiện `local.properties`.

## 2. Chạy test trước khi mở PR (bắt buộc)

```bash
./gradlew test          # unit test — phải xanh 100%
git diff --stat         # chỉ gồm file bạn cố ý sửa
```

- CI (`.github/workflows/android-test.yml`) cũng chạy `./gradlew test`; PR không xanh = không merge.
- Test mới là bắt buộc cho mọi thay đổi logic (repo/domain/rules). Không nhận "tay em test rồi ok".
- `connectedAndroidTest` chỉ chạy khi có thiết bị/emulator — **không bắt buộc** trong PR thường.

## 3. Quy ước code

| Việc | Quy tắc |
|---|---|
| UI | **Jetpack Compose** (D-1) — không thêm View XML mới |
| DI | **Không Hilt/Koin** — graph dựng tay ở `platform/ServiceGraph.kt` |
| Data | **Room** (7 bảng, schema JSON đã commit trong `app/schemas/`) — đổi entity = thêm Migration |
| Gọi AI | **`HttpURLConnection` + `org.json`** (D-5) — **không Retrofit/OkHttp** |
| Dependency | Không thêm thư mục mới trong MVP; nếu thật sự cần, tách riêng 1 PR và ghi rõ lý do |
| Layer | `ui / domain / data / platform` — domain không import data, data không import ui |
| Ngôn ngữ | Commit + comment viết tiếng Việt, ngắn gọn; tên file/đơn vị tính viết tiếng Anh |

## 4. Quy trình PR

1. Nhánh con từ `main`: `feat/<ngu>-<chude>`, `fix/<ngu>-<chude>`.
2. PR nhỏ (≤ ~300 dòng diff), tự review lại trước khi gửi.
3. Mô tả PR gồm: **mục tiêu / thay đổi chính / cách test** (dán log `./gradlew test`).
4. 1 người review approve là merge; PR nào cũng phải qua review.

## 5. Tham chiếu

- `docs/ROADMAP_NEW_PHASES.md` — phase NP0-NP5, DoD từng phase.
- `docs/PART1_M1_SOFTWARE_DESIGN.md` — quyết định D-1..D-12 (gốc của các quy tắc trên).
- `docs/PART1_M1_TEST_PLAN.md` — ~90 case test; viết test mới theo tên file `*Test.kt` tương ứng.
