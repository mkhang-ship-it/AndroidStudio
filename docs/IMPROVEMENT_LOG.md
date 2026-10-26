# Improvement Log — vòng cải thiện sau MVP (2026-10-26)

> Mục tiêu: nâng điểm P5/M4/P6 bằng thay đổi **nhìn thấy được** + **test/evidence** đi kèm.
> Phạm vi: ~4 giờ, ưu tiên trải nghiệm đọc câu trả lời, animation, dữ liệu học tập và các bug Open trong `BUG_LIST_NP5.md`.

## 1. Tóm tắt thay đổi

| # | Hạng mục | Trước | Sau | File chính |
|---|---|---|---|---|
| 1 | Đọc câu trả lời AI | Raw `\frac`, `\lim`, `$...$`, `**...**` | Markdown + LaTeX → Unicode, phân số xếp tầng bằng Compose inline content | `ui/common/Latex.kt`, `ui/common/MarkdownText.kt` |
| 2 | Hiệu ứng câu trả lời | Text hiện một lần | Chữ hiện dần (typewriter), chạm để hiện toàn bộ | `ui/common/TypewriterText.kt` |
| 3 | Animation | Gần như tĩnh | Nút nhún khi bấm, card trượt vào, progress bar chạy, list item animate | `DuoComponents.kt`, `AskScreen.kt`, `ProgressScreen.kt`, `HistoryScreen.kt` |
| 4 | Mascot | Không có | Mèo tím đội mũ tốt nghiệp vẽ bằng Canvas: nhún, nháy mắt, ve đuôi, sách/bút bay quanh, suy nghĩ/ăn mừng | `ui/common/StudyMascot.kt` |
| 5 | Progress insights | Chỉ có số liệu | Card "Gợi ý cho bạn" sinh offline từ dữ liệu học tập | `domain/rules/ProgressInsights.kt`, `ProgressScreen.kt` |
| 6 | Token metering | `inputTokens = 0` | Đọc `usageMetadata` từ Gemini và lưu xuống DB | `GeminiAiClient.kt` |
| 7 | Thời gian ôn tập | Chỉ ghi khi đóng màn hình, dễ mất; hay hiện 0 phút | Cộng theo từng 15 giây, chỉ khi app resumed | `AskScreen.kt`, `QuestionDetailScreen.kt` |
| 8 | Lockout đăng nhập | Mất khi restart app (BUG-01) | Lưu SharedPreferences, bền vững qua restart | `data/security/LoginThrottle.kt`, `AuthRepositoryImpl.kt` |
| 9 | Luyện tập | Prompt/đáp án hiện raw `$...$` | Render markdown/LaTeX trong câu hỏi, đáp án, giải thích | `PracticeScreen.kt` |
| 10 | Nhãn XP tuần | Chỉ hiện "XP tuần này", thiếu số | "XP tuần này: +N XP" | `ProgressScreen.kt`, `strings.xml` |
| 11 | Theme tổng thể | Xanh lá/xanh dương kiểu Duolingo | Tím #6A4BEE + kem/vàng, đồng bộ mascot; light + dark | `ui/theme/Color.kt`, `DuoComponents.kt`, `ic_launcher_background.xml` |
| 12 | Điều hướng tab Hồ sơ | Mở thông báo deep-link sang Tiến độ rồi bấm Hồ sơ bị kẹt ở Tiến độ | Đóng route con + ưu tiên `popBackStack` khi tab đã có trong stack | `ui/shell/Destinations.kt`, `ui/shell/AppNavHost.kt` |
| 13 | Xoá lịch sử | Chọn tất cả nhưng chỉ xoá 1 bản ghi đại diện; bản trùng hiện lại | Xoá theo `userId` + `questionHash`, dọn hết bản trùng | `QuestionAnswerDao.kt`, `QuestionRepositoryImpl.kt`, `HistoryViewModel.kt` |
| 14 | Số câu luyện tập | Chỉ có 3/5/10 | Ô nhập + nút −/+ cho 1–20 câu, giữ chip nhanh 3/5/10 | `PracticeScreen.kt`, `PracticeViewModel.kt` |
| 15 | Chi tiết thông báo | Bấm thông báo chỉ deep-link hoặc không phản hồi | Màn chi tiết: loại, trạng thái, thời gian, nội dung, nút mở link | `NotificationDetailScreen.kt`, `NotificationDao.kt`, `AppNavHost.kt` |
| 16 | Badge chưa đọc | Icon sao vàng + "0 chưa đọc" khó hiểu | Icon chuông + "N chưa đọc"; 0 → "Đã đọc tất cả" | `NotificationsScreen.kt` |
| 17 | LaTeX trong JSON | `\frac` bị JSON hiểu thành form-feed, `\right` thành CR → công thức hỏng | Repair escape trước khi parse + repair chuỗi đã lưu | `GeminiAiClient.kt`, `Latex.kt` |
| 18 | Debug API | Không thấy body gửi/nhận | Log `GeminiAPI`: request body + raw response, chia chunk, ẩn base64/key | `GeminiAiClient.kt` |
| 19 | Schema trả lời | JSON cũ gộp mọi thứ vào `answerText` | Schema `steps[{title,body,expr}]` + `finalAnswer`; render công thức thành block | `GeminiAiClient.kt`, `GeminiParsingTest.kt` |
| 20 | Nhận diện môn | "hàm số trong Java" bị trả về Toán dù detector đã thấy "java" | Ưu tiên subject người dùng chọn / keyword detector; prompt thêm hint + quy tắc code | `QuestionRepositoryImpl.kt`, `GeminiAiClient.kt`, `SubjectDetector.kt` |
| 21 | Cache nhầm câu | Hỏi "hàm số trong C#" bị trả đáp án Java do `C#`/`C++` mất token | Chuẩn hoá `c#→csharp`, `c++→cpp`; key-token guard cho số liệu + tên ngôn ngữ | `QuestionSimilarity.kt`, `ReusePolicyTest.kt`, `QuestionSimilarityTest.kt` |

## 2. Chi tiết kỹ thuật

### 2.1. LaTeX + Markdown renderer (BUG-07)

**Vấn đề:** câu trả lời toán học của Gemini chứa `$...$`, `\frac{}{}`, `\lim`, `\Delta`; UI cũ bỏ dấu `$` nhưng giữ nguyên lệnh LaTeX nên người dùng thấy `\frac{df}{dx}` thay vì công thức.

**Giải pháp (không thêm dependency, giữ D-1/D-5):**

- `Latex.kt` chuyển các lệnh phổ thông sang Unicode: Hy Lạp, `\to`, `\infty`, `\sum`, `\int`, `\sqrt`, lũy thừa, chỉ số dưới, `\text`, ma trận đơn giản…
- `splitLatexPieces()` tách `\frac{a}{b}` để UI vẽ **phân số xếp tầng** bằng `InlineTextContent` của Compose (không cần WebView/MathJax).
- `MarkdownText.kt` được viết lại thành parser block + inline:
  - tiêu đề `#`, `##`, `###`
  - đậm `**`, nghiêng `*`, gạch ngang `~~`, code inline `` ` ``
  - danh sách `-`, `1.`, trích dẫn `>`, đường kẻ `---`
  - code fence ``` ``` ```, công thức `$...$`, `$$...$$`, `\(...\)`, `\[...\]`
- Gemini đôi khi viết thiếu dấu `\` (ví dụ `2 cdot 3`), nên có bước chuẩn hoá các lệnh "trần" an toàn.

**Giới hạn có chủ đích:** không phải trình render LaTeX đầy đủ. Ma trận phức tạp, `\begin{align}`, tikz… vẫn có thể hiển thị chưa đẹp; sẽ ghi vào D2 future.

**Test:** `LatexTest.kt` — 18 case cho chuyển đổi, tách phân số, parser block và tokenizer inline.

### 2.2. Typewriter + animation

- `TypewriterMarkdown` hiện chữ dần trong 0.65–2.2 giây tuỳ độ dài; chạm vào để hiện ngay. Đây là **hiệu ứng reveal**, không phải streaming mạng thật (xem mục 5).
- `DuoButton` scale 0.97 khi nhấn bằng `interactionSource` + spring.
- `DuoProgress` animate giá trị; progress bar môn học chạy từ 0 bằng `Animatable`.
- Card kết quả ở Ask trượt + mở rộng; danh sách Lịch sử dùng `Modifier.animateItem()`.
- Mascot `StudyMascot`:
  - vẽ hoàn toàn bằng Compose Canvas (không cần asset/ảnh, không tăng APK đáng kể)
  - nhân vật hiện tại là **mèo tím đội mũ tốt nghiệp**: tai tam giác, đuôi cong, râu mèo, palette tím/kem/vàng
  - **vì sao đổi:** bản đầu là cú mèo xanh — silhouette và màu xanh dễ bị liên tưởng tới linh vật Duolingo. Bản hiện tại đổi loài, đổi màu, thêm mũ tốt nghiệp để không sao chép thiết kế.
  - trạng thái `IDLE` (nhún, nháy mắt, ve đuôi), `THINKING` (nghiêng đầu, mắt nhìn lên, bong bóng suy nghĩ), `CELEBRATING` (nhảy, đuôi vung mạnh, sparkles)
  - sách và bút chì bay quanh theo quỹ đạo — đúng yêu cầu "vật thể/con vật chuyển động"
  - đọc animation state trong **draw phase** để chỉ invalidate phần vẽ, không recompose cả màn hình mỗi frame
- Kiểm chứng animation trên emulator: chụp 2 frame cách nhau 700ms, vùng mascot (820–1080, 100–420) có **11.201 pixel thay đổi** → animation chạy thật, không phải ảnh tĩnh.
- Gắn mascot vào: Hỏi AI (theo trạng thái loading/kết quả), Luyện tập (đúng/sai), Onboarding.

### 2.3. Progress insights (BUG-03)

- `ProgressInsights.build()` là logic thuần Kotlin, không gọi AI, chạy offline:
  - môn tập trung nhiều nhất
  - môn còn ít câu hỏi
  - tỉ lệ đúng thấp (chỉ kết luận khi đã làm ≥ 3 câu)
  - số câu luyện tập còn lại
  - thời gian ôn tập
- Tối đa 3 insight để card không quá dài; UI hiển thị trong card "Gợi ý cho bạn".
- **Test:** `ProgressInsightsTest.kt` — 7 case, gồm biên accuracy và giới hạn số insight.

### 2.4. Token metering (BUG-08)

- `usageTokens(root)` đọc `usageMetadata.promptTokenCount` và `candidatesTokenCount`.
- `parseAnswer()` nhận token và `QuestionRepositoryImpl` đã lưu `inputTokens`/`outputTokens` xuống `QuestionAnswer` từ trước — nay dữ liệu thật đã chảy vào DB.
- **Test:** 3 case trong `GeminiParsingTest` (đọc usage, thiếu metadata, giữ token khi parse).

### 2.5. Thời gian ôn tập

- Trước: `QuestionDetailScreen` cộng dồn rồi launch trong `onDispose` bằng `rememberCoroutineScope` — scope bị huỷ cùng lúc nên gần như không lưu được; Ask không đếm.
- Sau: cả Ask và Detail cộng 15 giây mỗi lần, chỉ khi lifecycle đang `RESUMED`; app xuống background thì dừng.
- Progress hiển thị "Chưa ghi nhận" / "N giây" / "N phút" thay vì luôn "0 phút".

### 2.6. Lockout bền vững (BUG-01)

- Thêm `LoginThrottle` + `SharedPrefsLoginThrottle` (email đã chuẩn hoá làm key, không lưu mật khẩu).
- `AuthRepositoryImpl` dùng throttle thay cho `ConcurrentHashMap`; lock 5 phút vẫn còn sau khi app bị kill/restart.
- **Test instrumented:** `login_lockout_vanConSauKhiTaoRepositoryMoi` tạo repository mới + throttle mới đọc cùng prefs và xác nhận vẫn bị khoá.

### 2.7. Theme tím + sửa điều hướng Hồ sơ

- **Theme:** đổi toàn bộ `Color.kt` sang palette tím (`#6A4BEE`), kem và vàng; cập nhật cả light/dark, surface container, outline, error. Nút `DuoButton` dùng `onPrimary/onSecondary/onError` thay vì hardcode chữ trắng để giữ tương phản; edge 3D đổi sang tím đậm. Launcher background đổi từ xanh Android sang tím `#7B61FF`.
- **Bug điều hướng:** khi mở thông báo có deep-link sang Tiến độ, tab Hồ sơ bị kẹt ở Tiến độ. Nguyên nhân: màn Thông báo/route con còn nằm trong back stack; `navigateToTopLevel` dùng `saveState`/`restoreState` nên khôi phục cả route con. Sửa trong `Destinations.kt`:
  - pop các route không phải tab trước khi đổi tab;
  - nếu tab đích đã có trong back stack thì `popBackStack(route, inclusive = false)` thay vì `navigate` lại.
- **Đã kiểm tra tay:** Hồ sơ → Thông báo → Tóm tắt hôm nay (Tiến độ) → bấm Hồ sơ → về đúng Hồ sơ; Back từ Tiến độ → Thông báo → Hồ sơ.

### 2.8. Xoá lịch sử, số câu tuỳ chọn, chi tiết thông báo

- **Xoá lịch sử:** UI gộp câu trùng theo `questionHash`. Trước đây `deleteByIds` chỉ xoá id đại diện nên các bản trùng còn lại hiện lại, cảm giác "chọn tất cả mà chỉ xoá 1 câu". Sửa: `deleteQuestions(userId, ids)` lấy hash của các câu đã chọn rồi gọi `deleteByHashes(userId, hashes)`; có test DAO `deleteByHashes_removesAllDuplicateRowsOfSameQuestion`.
- **Số câu luyện tập:** bỏ giới hạn chip 3/5/10; thêm ô nhập số + nút −/+ cho phép chọn **1–20 câu**, vẫn giữ chip nhanh. `PracticeUiState.MIN_COUNT/MAX_COUNT` dùng chung cho ViewModel và UI.
- **Chi tiết thông báo:** thêm route `notification/{notificationId}`, `NotificationDetailScreen` hiển thị loại, trạng thái đã đọc, thời gian, nội dung và nút "Mở nội dung liên quan" nếu có `deepLink`. Mở chi tiết sẽ đánh dấu đã đọc; danh sách tự cập nhật khi quay lại.
- **Badge thông báo:** bỏ icon ngôi sao vô nghĩa; dùng chuông khi còn chưa đọc, check khi đã đọc hết; text "0 chưa đọc" đổi thành "Đã đọc tất cả".
- **LaTeX trong JSON:** phát hiện câu trả lời bị `.racf`/`ight` do JSON parser biến `\frac` thành form-feed, `\right` thành CR. Sửa hai lớp: repair escape trước khi parse JSON (`repairLatexEscapes`) và repair chuỗi đã lưu trước khi render (`repairLatexControlChars`). Test: `repairLatexEscapes...`, `khoi phuc ky tu dieu khien...`, `parseAnswer sua form feed...`.

### 2.9. Debug log body gửi/nhận Gemini

- Bật log trong `GeminiAiClient` (chỉ `BuildConfig.DEBUG`):
  - `POST <endpoint> (attempt N)`
  - `→ REQUEST BODY`: JSON gửi lên, **ẩn base64 ảnh**, không log API key
  - `← HTTP <code> responseLen=<n>`
  - `← RESPONSE BODY`: raw response
- Logcat cắt message ~4KB nên chia **3.000 ký tự/chunk** với nhãn `[part N]`.
- Xem log: `adb logcat -s GeminiAPI`.

### 2.10. Schema trả lời mới: steps + finalAnswer

- **Prompt** (`answerPrompt`): chuyển sang cấu trúc
  `{subject, steps:[{title, body, expr}], finalAnswer}`. Prompt viết bằng tiếng Anh
  nhưng yêu cầu **trả lời cùng ngôn ngữ với câu hỏi** (không ép tiếng Anh).
- **Parse** (`parseAnswer`): gộp steps thành markdown để lưu `answerText` —
  `### Bước N: title`, body, rồi `$$expr$$` (block công thức căn giữa). `finalAnswer`
  thành `## Đáp án` (hoặc `## Final answer` nếu câu hỏi tiếng Anh). Không đổi schema Room.
- **Nhãn ngôn ngữ:** `answerLanguageIsVietnamese()` nhận diện dấu tiếng Việt và từ khoá
  ASCII ("giai", "phuong", "trinh"…); câu không có chữ ("2 + 2 = ?") mặc định tiếng Việt.
- **Buộc tiếng Việt có dấu:** prompt yêu cầu ghi đầy đủ dấu ở cả `title`, `body`,
  `finalAnswer` kể cả khi người dùng gõ không dấu. Đã test: hỏi "Hai dien tro 6 ohm va
  3 ohm mac song song…" → trả lời "Xác định công thức tính điện trở tương đương…".
- **Subject:** map `math→MATHEMATICS`, `science→SCIENCE`, `code→PROGRAMMING`,
  `history→HISTORY`, `language→LANGUAGE`; `other`/giá trị lạ → fallback môn người dùng chọn.
- **Difficulty:** schema mới không trả về → `null`, UI tự ẩn chip "Độ khó".
- **FR5 giữ nguyên:** 5 mục "Học thêm" vẫn dùng prompt riêng, sinh khi người dùng bấm.
- **Đã kiểm tra tay:** hỏi bài rơi tự do → 5 bước + công thức `v = g·t` + mục "Đáp án";
  Lịch sử → Chi tiết hiển thị đúng, môn "Khoa học", 5 mục FR5 hiện nút "Tạo nội dung này".

### 2.11. Sửa nhận diện môn và cache nhầm câu (Java/C#)

- **Nhầm môn:** "hàm số trong Java" từng bị trả về Toán. `SubjectDetector` đã thấy
  "java" nhưng `answer.subject` của AI lại được ưu tiên khi lưu. Sửa: `detectedSubject`
  (override của người dùng → keyword detector) được ưu tiên; prompt thêm subject hint
  và quy tắc "hàm/phương thức trong ngôn ngữ lập trình (Java, C#, C++, Python…) là code".
- **Cache nhầm:** `C#`/`C++` bị bỏ dấu câu thành token `c` rồi bị loại vì 1 ký tự, nên
  "hàm số trong C#" và "hàm số trong Java" trùng token nhau. Sửa: chuẩn hoá
  `C#→csharp`, `C++→cpp`, `F#→fsharp`, `.NET→dotnet`; giữ token `c`/`r`; thêm
  **key-token guard** — chỉ tái dùng khi tập số liệu + tên ngôn ngữ trùng nhau.
- **Render code:** model đặt code vào `expr`; parser nhận diện code (dấu `;`, `//`,
  keyword, ngoặc) và render thành code fence thay vì công thức LaTeX.
- **Tự sửa dữ liệu cũ:** nếu môn keyword detector phát hiện khác `subjectCode` của bản
  ghi cache, `ReusePolicy.select` bỏ qua bản ghi đó và gọi AI lại. Đã mô phỏng bằng cách
  sửa row Java trong DB thành `MATHEMATICS`: hỏi lại → có POST mới, lưu môn "Lập trình";
  hỏi lần nữa → cache hit, không tốn thêm lượt gọi.

**Ma trận test tay 9/9 pass** (`ai_matrix.py`, emulator `Medium_Phone`):

| # | Câu hỏi | Môn | Cache | Kết quả |
|---|---|---|---|---|
| 1 | `Giai phuong trinh x^2 - 5x + 6 = 0` | Toán học | Không | PASS |
| 2 | `Giai phuong trinh x^2 - 5x + 6 = 1` (khác số cuối) | Toán học | Không | PASS |
| 3 | `Quang hop o thuc vat la gi` | Khoa học | Không | PASS |
| 4 | `Chien tranh the gioi thu hai ket thuc nam nao` | Lịch sử | Không | PASS |
| 5 | `Phan tich bai tho Dong chi cua Chinh Huu` | Ngôn ngữ | Không | PASS |
| 6 | `Ham trong Python la gi` | Lập trình | Không | PASS |
| 7 | `Tinh dao ham cua x^4 tai x = 2` | Toán học | Không | PASS |
| 8 | Lặp y hệt câu 1 | Toán học | **Có** | PASS |
| 9 | Câu 1 + từ dừng "cua" | Toán học | **Có** | PASS |

- Case 2 và 7 chứng minh không còn tái dùng đáp án khi khác số liệu.
- Case 8/9 chứng minh cache vẫn hoạt động đúng với câu lặp và câu thêm từ dừng.
- Case 6 cho thấy code Python được render thành code block, giữ nguyên `def ten_ham(...)`.
- Case "hàm số trong Java" và "hàm số trong C#" (test tay trước ma trận) đều trả môn
  "Lập trình", nội dung riêng, không lấy đáp án của nhau.

## 3. Test & build evidence

| Lệnh | Kết quả |
|---|---|
| `.\build.ps1 :app:testDebugUnitTest` | **BUILD SUCCESSFUL** — toàn bộ unit test xanh |
| `.\build.ps1 :app:connectedDebugAndroidTest` | **39/39 test pass** trên `Medium_Phone(AVD)` — XML ghi 39 test, 0 failure (Gradle chỉ cảnh báo bước thu output từ emulator) |
| `.\build.ps1 :app:assembleDebug` | **BUILD SUCCESSFUL** — APK debug tạo thành công |
| Smoke test tay trên emulator | Hỏi AI → nhận 5 bước + block công thức + "Đáp án" → Lịch sử → Chi tiết; không crash |
| Ma trận hỏi AI (9 câu) | **9/9 pass**: đúng môn (Toán/Khoa học/Lịch sử/Ngôn ngữ/Lập trình), không cache nhầm, cache đúng câu lặp |
| Tự sửa cache cũ sai môn | Sửa row Java trong DB thành Toán → hỏi lại gọi AI mới, lưu "Lập trình"; hỏi lần nữa → cache hit |

**Test case (unit):** 143 test
- 19 case `LatexTest`
- 7 case `ProgressInsightsTest`
- 27 case `GeminiParsingTest` (schema steps/finalAnswer, code block, token usage, repair LaTeX JSON)
- 24 case `QuestionSimilarityTest` (C#/C++, key-token guard, số liệu)
- 15 case `SubjectDetectorTest` (Java/C#/C++ là Lập trình)
- 12 case `ReusePolicyTest` (cache + guard môn)
- 1 case lockout restart trong `AuthRepositoryTest` (instrumented)
- 1 case xoá theo hash trong `QuestionAnswerDaoTest` (instrumented)

**Sửa test sai có sẵn:** `QuestionAnswerDaoTest.countBySubject_groupsAndCountsPerSubject` insert 4 row nhưng assert `countByUser == 3`; đã sửa thành 4. Đây là assertion sai, không phải lỗi DAO.

## 4. Screenshots

| Màn hình | File |
|---|---|
| Hỏi AI (mascot + gợi ý) | `screenshots/01_ask.png` |
| Lịch sử | `screenshots/02_history.png` |
| Chi tiết câu trả lời toán | `screenshots/03_answer_math.png` |
| Công thức phân số khi cuộn | `screenshots/03b_answer_math_formula.png` |
| Tiến độ + insights | `screenshots/04_progress.png` |
| Luyện tập | `screenshots/05_practice.png` |
| Feedback đúng + mascot | `screenshots/06_practice_feedback.png` |
| Hồ sơ | `screenshots/07_profile.png` |
| Onboarding + mascot | `screenshots/08_onboarding.png` |
| Danh sách thông báo | `screenshots/09_notifications.png` |
| Chi tiết thông báo | `screenshots/10_notification_detail.png` |

Ảnh tiêu biểu:

![Hỏi AI](screenshots/01_ask.png)
![Chi tiết câu trả lời toán](screenshots/03_answer_math.png)
![Tiến độ + insights](screenshots/04_progress.png)

## 5. Điều còn thiếu / trung thực về "streaming"

- **Chưa streaming mạng thật.** Gemini đang được gọi bằng `generateContent` và trả **một JSON** chứa nhiều trường (`answerText`, `difficulty`, `subject`…). Nếu stream SSE thì phải parse JSON dở dang hoặc đổi prompt sang plain text và gọi thêm lần 2 để lấy metadata. Trong 4 giờ, phương án typewriter cho trải nghiệm gần giống, rủi ro thấp và không phá schema.
- **LaTeX chưa đầy đủ:** chưa hỗ trợ `align`, ma trận phức tạp, `\begin{cases}`, tikz; phân số lồng trong phân số sẽ fallback `a/b`.
- **2FA, leaderboard global, sync 2 chiều** vẫn là D2 future (đúng phạm vi MVP).
- **BUG-02** (`MISTAKE_ALERT` keyword tiếng Anh) chưa sửa trong vòng này.
- Accessibility của mascot: Canvas là trang trí, chưa có content description; nếu cần WCAG đầy đủ nên thêm nhãn hoặc đánh dấu decorative.

## 6. Cách tái hiện

```powershell
# Build + unit test
.\build.ps1 :app:testDebugUnitTest

# Cài lên emulator đang chạy
.\build.ps1 :app:assembleDebug
adb install -r "R:\app\build\outputs\apk\debug\app-debug.apk"

# Instrumented test (38 case)
.\build.ps1 :app:connectedDebugAndroidTest

# Nếu Gradle báo "Address already in use" khi mở daemon (Windows cạn ephemeral port):
R:\gradlew.bat -p R:\ --no-daemon :app:connectedDebugAndroidTest
```

> Lưu ý: script `build.ps1` tự tạo ổ `R:` trỏ vào thư mục dự án vì AGP/gradlew không xử lý tốt đường dẫn có dấu tiếng Việt.
