# USABILITY TEST SCRIPT — NP5 (kịch bản ~15 phút / user)

> Mục tiêu: đo task success + thời gian + cảm nhận (SUS rút gọn 1-5) cho MVP AI Study Mentor.
> Đối tượng: 3-5 users (sinh viên phổ thông/đại học, KHÔNG thuộc nhóm dev).
> Cách chạy: 1 moderator + 1 ghi chép/user. App cài trên 1 thiết bị Android thật hoặc emulator, đã cấu hình API key (hoặc `FakeAiClient` nếu offline test).
> Nguyên tắc (think-aloud): "Hãy nói to mọi suy nghĩ trong khi làm. Bạn không thể làm hỏng app — nếu bế tắc, đó là lỗi của chúng tôi."

## Chuẩn bị (trước khi gặp user, ~5 phút)

- [ ] Cài release/debug APK mới nhất, đăng xuất sẵn, xoá dữ liệu app lần đầu.
- [ ] Chuẩn bị script câu chào + consent (ghi hình/ghi âm có đồng ý).
- [ ] Chuẩn bị mẫu câu hỏi cho Task 3 (dán ra giấy): *"Giải phương trình 2x + 5 = 15 giúp mình"* hoặc câu tiếng Anh tùy user.
- [ ] Mở stopwatch ghi tay cho từng task; chuẩn bị bảng điểm bên dưới.

## Mở đầu (1 phút)

"Cảm ơn bạn tham gia. App này là AI Study Mentor — bạn hỏi bài, AI trả lời, app lưu lại và theo dõi tiến bộ của bạn. Hôm nay chúng tôi test APP, không test BẠN. Bắt đầu nhé."

---

## Task 1 — Đăng ký tài khoản (~2 phút)

**Script cho user:** "Bạn hãy tự cài đặt cho mình một tài khoản mới để bắt đầu dùng app."

**Kết quả mong đợi:** mở app → màn hình auth → đăng ký email + mật khẩu hợp lệ → vào được app (qua onboarding).

**Quan sát:**
- User có tìm được nút "Đăng ký" không? Có nhầm sang "Đăng nhập" không?
- Mật khẩu yếu bị báo lỗi rõ không?
- (Nâng cao, nếu user tự thử) nhập email trùng → báo lỗi có hiểu được không?

---

## Task 2 — Onboarding (~2 phút)

**Script cho user:** "App hỏi vài câu về bạn để cá nhân hoá cách giải thích. Hãy trả lời theo ý bạn nhé."

**Kết quả mong đợi:** chọn cấp học + phong cách giải thích → hoàn tất onboarding → về màn hình chính.

**Quan sát:**
- Các lựa chọn có dễ hiểu không (VD "Sơ cấp / THCS / THPT / Đại học")?
- User có bấm "Bỏ qua" và tiếc không?
- Màn hình chính sau onboarding có rõ "bước tiếp theo" không?

---

## Task 3 — Hỏi một câu hỏi và đọc câu trả lời (~3 phút)

**Script cho user:** "Giả sử bạn đang học bài và mắc câu này. Hãy dùng app để hỏi và đọc lời giải." *(đưa mẫu câu hỏi cho user)*

**Kết quả mong đợi:** vào Ask → gõ/nhận câu hỏi (text hoặc ảnh) → gửi → thấy loading → nhận câu trả lời hoàn chỉnh → mở xem chi tiết.

**Quan sát:**
- User có biết cách gửi câu hỏi không? Nút Send có rõ?
- Loading spinner + timeout có làm user bỏ cuộc không? (ghi thời gian chờ)
- User có đọc hết câu trả lời không? Có phàn nàn "đọc khó hiểu / toàn asterisk" không? *(liên quan bug markdown renderer)*
- User có thử "Giải thích lại" (explanation style khác) không?

---

## Task 4 — Bookmark + tìm trong history (~3 phút)

**Script cho user:** "Câu trả lời vừa rồi hay quá, hãy lưu lại để sau xem lại. Sau đó tìm lại câu hỏi về [toán/tiếng Anh] trong lịch sử."

**Kết quả mong đợi:** bấm bookmark (hoặc nút lưu) trên câu trả lời → vào History → search bằng keyword → lọc theo môn → mở lại câu đã lưu.

**Quan sát:**
- Nút bookmark có nhìn ra không? Sau khi bấm có feedback (icon/tooltip/toast) không?
- Search có trả đúng kết quả với keyword user gõ (có/không dấu)?
- User có phân biệt được "mục đã lưu" vs "toàn bộ lịch sử" không?
- Filter theo môn có trực quan không?

---

## Task 5 — Xem tiến bộ (progress) (~2 phút)

**Script cho user:** "Hãy xem app báo cáo việc học của bạn như thế nào."

**Kết quả mong đợi:** vào Progress → thấy tổng số câu hỏi, số câu theo môn, độ chính xác quiz, XP/badge hiện có.

**Quan sát:**
- User có hiểu các con số nghĩa là gì không? (hỏi lại: "Theo bạn, con số này nói gì?")
- Có hiểu được XP/badge không? Có motivates không?
- AI-insights (nếu có) có ý nghĩa với user không? *(liên quan bug "insights chưa narrative")*

---

## Kết thúc — SUS rút gọn + tự do (~2 phút)

Trả lời 1-5 (1 = rất không đồng ý, 5 = rất đồng ý):

| # | Câu hỏi SUS rút gọn | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|---|
| 1 | Tôi sẵn sàng dùng app này thường xuyên | | | | | |
| 2 | App quá phức tạp để dùng | | | | | |
| 3 | Tôi thấy app dễ sử dụng | | | | | |
| 4 | Tôi cần hỗ trợ kỹ thuật để dùng app | | | | | |
| 5 | Các chức năng app liền mạch với nhau | | | | | |
| 6 | App quá khó hiểu | | | | | |

Hỏi thêm: "Bạn thích nhất điều gì? Bạn chán nhất điều gì? Nếu chỉ được sửa 1 thứ, bạn sửa gì?" *(gợi ý liệt kê bug → nạp vào `BUG_LIST_NP5.md`)*

---

## BẢNG GHI ĐIỂM (mỗi user 1 bảng — điền sau mỗi session)

| Thông tin | Ghi chú |
|---|---|
| User ID | U1 / U2 / U3 / U4 / U5 |
| Ngày test | YYYY-MM-DD |
| Thiết bị + version APK | |
| Có API key thật? (Y/N) | |

### Kết quả theo task

| Task | Tên | Success (Y/N/Partial) | Thời gian (giây) | Ghi chú quan sát (điểm đau, lời nói think-aloud) |
|---|---|---|---|---|
| 1 | Đăng ký | | | |
| 2 | Onboarding | | | |
| 3 | Hỏi 1 câu + đọc trả lời | | | |
| 4 | Bookmark + tìm history | | | |
| 5 | Xem progress | | | |

**Task success** = user tự hoàn thành không cần hint; **Partial** = cần 1 hint; **Fail** = bỏ cuộc hoặc sai lệch hoàn toàn mục tiêu.

### Tổng hợp (điền sau khi xong 3-5 users)

| Chỉ số | Công thức / Cách tính | Kết quả |
|---|---|---|
| Task success rate | số task success / tổng task (per-task và overall) | |
| Thời gian TB mỗi task | trung bình thời gian (giây) | |
| SUS trung bình | (điểm các câu dương − điểm âm) × 2.5 hoặc trung bình đơn giản 1-5 | |
| Severity bug mới phát hiện | Blocker / High / Med / Low → chuyển vào `BUG_LIST_NP5.md` | |

### Nhận xét tổng thể

- Điểm đau lặp lại (≥2 users gặp):
- Đề xuất fix ưu tiên (liên kết bug ID):
- Điều user khen giữ nguyên:
