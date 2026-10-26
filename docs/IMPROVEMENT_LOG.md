# Improvement Log — vòng cải thiện sau MVP (2026-10-26)

> Mục tiêu: nâng điểm P5/M4/P6 bằng thay đổi **nhìn thấy được** + **test/evidence** đi kèm.
> Phạm vi: ~4 giờ, ưu tiên trải nghiệm đọc câu trả lời, animation, dữ liệu học tập và các bug Open trong `BUG_LIST_NP5.md`.

## 1. Tóm tắt thay đổi

| # | Hạng mục | Trước | Sau | File chính |
|---|---|---|---|---|
| 1 | Đọc câu trả lời AI | Raw `\frac`, `\lim`, `$...$`, `**...**` | Markdown + LaTeX → Unicode, phân số xếp tầng bằng Compose inline content | `ui/common/Latex.kt`, `ui/common/MarkdownText.kt` |
| 2 | Hiệu ứng câu trả lời | Text hiện một lần | Chữ hiện dần (typewriter), chạm để hiện toàn bộ | `ui/common/TypewriterText.kt` |
| 3 | Animation | Gần như tĩnh | Nút nhún khi bấm, card trượt vào, progress bar chạy, list item animate | `DuoComponents.kt`, `AskScreen.kt`, `ProgressScreen.kt`, `HistoryScreen.kt` |
| 4 | Mascot | Không có | Cú mèo vẽ bằng Canvas: nhún, nháy mắt, vỗ cánh, sách/bút bay quanh, suy nghĩ/ăn mừng | `ui/common/StudyMascot.kt` |
| 5 | Progress insights | Chỉ có số liệu | Card "Gợi ý cho bạn" sinh offline từ dữ liệu học tập | `domain/rules/ProgressInsights.kt`, `ProgressScreen.kt` |
| 6 | Token metering | `inputTokens = 0` | Đọc `usageMetadata` từ Gemini và lưu xuống DB | `GeminiAiClient.kt` |
| 7 | Thời gian ôn tập | Chỉ ghi khi đóng màn hình, dễ mất; hay hiện 0 phút | Cộng theo từng 15 giây, chỉ khi app resumed | `AskScreen.kt`, `QuestionDetailScreen.kt` |
| 8 | Lockout đăng nhập | Mất khi restart app (BUG-01) | Lưu SharedPreferences, bền vững qua restart | `data/security/LoginThrottle.kt`, `AuthRepositoryImpl.kt` |
| 9 | Luyện tập | Prompt/đáp án hiện raw `$...$` | Render markdown/LaTeX trong câu hỏi, đáp án, giải thích | `PracticeScreen.kt` |
| 10 | Nhãn XP tuần | Chỉ hiện "XP tuần này", thiếu số | "XP tuần này: +N XP" | `ProgressScreen.kt`, `strings.xml` |

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
  - trạng thái `IDLE` (nhún, nháy mắt, vỗ cánh), `THINKING` (nghiêng đầu, mắt nhìn lên, bong bóng suy nghĩ), `CELEBRATING` (nhảy, dang cánh, sparkles)
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

## 3. Test & build evidence

| Lệnh | Kết quả |
|---|---|
| `.\build.ps1 :app:testDebugUnitTest` | **BUILD SUCCESSFUL** — toàn bộ unit test xanh |
| `.\build.ps1 :app:connectedDebugAndroidTest` | **38/38 test pass** trên `Medium_Phone(AVD)` (DAO, repository, auth lockout, smoke test) |
| `.\build.ps1 :app:assembleDebug` | **BUILD SUCCESSFUL** — APK debug tạo thành công |
| Smoke test tay trên emulator | Hỏi AI → nhận câu trả lời toán có phân số → Lịch sử → Luyện tập → Tiến độ → Hồ sơ; không crash |

**Test case mới:** 28 case
- 18 case `LatexTest`
- 7 case `ProgressInsightsTest`
- 3 case token usage trong `GeminiParsingTest`
- 1 case lockout restart trong `AuthRepositoryTest` (instrumented)

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
