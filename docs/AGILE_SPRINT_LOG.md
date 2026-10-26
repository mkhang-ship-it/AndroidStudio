# AGILE SPRINT LOG — AI Study Mentor (14 sprint / 14 tuần)

> Evidence cho **P5/M4/P6** (quy trình Agile + methodology).
> Sprint = 1 tuần (Tuần 1 → S01, ..., Tuần 14 → S14).
> Phase map theo `docs/ROADMAP_NEW_PHASES.md` (NP0-NP5). Mốc ngày ghi `YYYY-MM-DD`.
>
> **Cách điền (mỗi sprint, sau khi sprint đóng):**
> - **Goal**: 1-2 câu — mục tiêu sprint này (lấy từ DoD phase).
> - **Done**: danh sách việc + PR/commit + test tương ứng (phải kèm test, không "dùng thử ok").
> - **Blocker**: vướng gì, ai gỡ, gỡ khi nào; để trống nếu không có → ghi `—`.
> - **Retro**: 1 điểm giữ lại / 1 điểm cần cải thiện cho sprint sau (tiếng Việt).

| Sprint | Tuần | Phase chính | Phase đang song song (//) |
|---|---|---|---|
| S01 | 1 | NP0 Training + CI | — |
| S02 | 2 | NP0 Training + CI | NP1 bắt đầu |
| S03 | 3 | NP1 Ask→Answer hardening | — |
| S04 | 4 | NP1 Ask→Answer hardening | NP2 bắt đầu |
| S05 | 5 | NP1 Ask→Answer hardening | NP2 |
| S06 | 6 | NP2 AI-cost + observability | NP3 bắt đầu |
| S07 | 7 | NP3 History + Progress | NP2 đóng |
| S08 | 8 | NP3 History + Progress | — |
| S09 | 9 | NP3 History + Progress | NP4 bắt đầu |
| S10 | 10 | NP4 FR9/10 + NFR chặn | — |
| S11 | 11 | NP4 FR9/10 + NFR chặn | NP5 bắt đầu |
| S12 | 12 | NP5 Đóng băng MVP + Evidence | — |
| S13 | 13 | NP5 Đóng băng MVP + Evidence | — |
| S14 | 14 | NP5 Đóng băng MVP + Evidence | Nộp bài |

---

## S01 — Tuần 1 — NP0

> Ngày đóng sprint: 2026-10-15.

| Mục | Nội dung |
|---|---|
| Goal | Cài nền cho junior: đóng băng roadmap phase mới, quy trình đóng góp, CI chạy test tự động, API key chỉ nằm ở `local.properties` (không commit). |
| Done | `docs/ROADMAP_NEW_PHASES.md` (gap đã verify + NP0-NP5 + stretch); skeleton `docs/AGILE_SPRINT_LOG.md` 14 sprint; `AI Study/CONTRIBUTING.md`; `AI Study/local.properties.example`; `.github/workflows/android-test.yml` (CI chạy `gradlew test`); xác nhận `gradlew test` xanh (8 unit-test files: `GeminiParsingTest`, `FakeAiClientTest`, `ReusePolicyTest`, `ConvertersTest`, `XpRulesTest`, `QuestionSimilarityTest`, `AbuseFilterTest`, `ExampleUnitTest`). |
| Blocker | — |
| Retro | Giữ: mỗi phase phải có DoD kèm test tự động. Cải thiện: chưa có PR thật/branch-per-task → sprint sau chia nhỏ changeset theo feature để review được. |

## S02 — Tuần 2 — NP0 (chuyển tiếp NP1)

| Mục | Nội dung |
|---|---|
| Goal | Bắt đầu NP1: đường Ask→AI→Answer không crash khi thiếu key / lỗi mạng; timeout + retry backoff; state loading/error/retry/offline đầy đủ. |
| Done | `GeminiAiClient.kt` (timeout 20s, retry exponential backoff cho 429/5xx, map exception → `AiError`); `FakeAiClient.kt` (fallback khi thiếu key); `AiError.kt`, `AiClient.kt` (response model có token fields); `AskScreen.kt` + `QuestionDetailScreen.kt` (states loading/error/retry/offline); test: `GeminiParsingTest`, `FakeAiClientTest`, `ReusePolicyTest`. |
| Blocker | Thiếu API key thật để test E2E → dùng `FakeAiClient` + JSON fixtures thay thế; key thật do mentor cấp qua `local.properties`. |
| Retro | Giữ: `FakeAiClient` + fixture cho phép dev/test không tốn AI-cost. Cải thiện: chưa đo latency thật, chưa có Compose UI test cho đủ 6 state. |

## S03 — Tuần 3 — NP1

| Mục | Nội dung |
|---|---|
| Goal | Hoàn tất hardening NP1 + kéo sớm các việc NP4 (auth/notify) vì code nền đã có. |
| Done | NP1: mọi answer đi qua `ReusePolicy` (`ReusePolicy.kt`), similarity VN không dấu (`QuestionSimilarity.kt`). NP4-early: `AuthRepositoryImpl.kt` (+`withTransaction` register, catch `SQLiteConstraintException` báo trùng email đẹp, lockout 5 lần sai — đang in-memory `ConcurrentHashMap`); `NotificationRepositoryImpl.kt` (sinh `REVIEW_REMINDER`/`MISTAKE_ALERT`, `postDailySummaryIfDue` idempotent), wire vào `MainShell.kt`; `QuestionRepositoryImpl.kt` (record XP `REVIEW_ANSWER`); `OnboardingScreen.kt` (age-gate COPPA <13); `AbuseFilter.kt`; test: `AuthRepositoryTest` (instrumented), `AbuseFilterTest`, `MainShellSmokeTest`. |
| Blocker | Lockout chưa persist (in-memory, mất khi app restart — chuyển sang Room migration ở stretch, xem `BUG_LIST_NP5.md`). |
| Retro | Giữ: kéo NP4 sớm khi phụ thuộc đã sẵn → giảm nợ sprint sau. Cải thiện: lockout không được để in-memory vượt qua sprint đóng; cần test case restart-process từ đầu. |

## S04 — Tuần 4 — NP1 (NP2 song song)

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S05 — Tuần 5 — NP1

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S06 — Tuần 6 — NP2 (NP3 song song)

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S07 — Tuần 7 — NP3

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S08 — Tuần 8 — NP3

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S09 — Tuần 9 — NP3 (NP4 song song)

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S10 — Tuần 10 — NP4

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S11 — Tuần 11 — NP4 (NP5 song song)

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S12 — Tuần 12 — NP5

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S13 — Tuần 13 — NP5

| Mục | Nội dung |
|---|---|
| Goal | |
| Done | |
| Blocker | |
| Retro | |

## S14 — Tuần 14 — NP5 + post-MVP polish

> Ngày đăng: 2026-10-26.

| Mục | Nội dung |
|---|---|
| Goal | Nâng trải nghiệm đọc lời giải và độ hoàn thiện demo: LaTeX/markdown, chữ hiện dần, mascot chuyển động, insights offline, token metering, lockout bền vững, đo thời gian ôn tập. |
| Done | `Latex.kt` + rewrite `MarkdownText.kt` (markdown + công thức Unicode + phân số inline); `TypewriterText.kt`; `StudyMascot.kt` (Canvas: mèo tím đội mũ tốt nghiệp + sách/bút bay, mood IDLE/THINKING/CELEBRATING — đổi từ cú mèo xanh để tránh liên tưởng Duolingo); animation nút/card/progress/list; `ProgressInsights.kt` + card gợi ý; `usageTokens()` + token test; `LoginThrottle` + `SharedPrefsLoginThrottle` + instrumented restart test; đo thời gian ôn tập lifecycle-aware ở Ask/Detail; render LaTeX trong Practice; sửa nhãn XP tuần; đổi theme tím + fix tab Hồ sơ sau deep-link thông báo; xoá lịch sử theo hash; số câu luyện tập 1–20; màn chi tiết thông báo; schema trả lời mới `steps[{title,body,expr}]` + `finalAnswer`, render `expr` thành block công thức hoặc code fence; sửa nhận diện môn Java/C# và cache nhầm `C#`/`C++` bằng key-token guard. Test: `LatexTest` (19), `ProgressInsightsTest` (7), `GeminiParsingTest` (27), `QuestionSimilarityTest` (24), `SubjectDetectorTest` (15), `ReusePolicyTest` (12), 1 case lockout restart, 1 case xoá theo hash; `connectedDebugAndroidTest` **39/39 pass**; ma trận hỏi AI tay **9/9 pass**. Evidence: `docs/IMPROVEMENT_LOG.md`, 11 screenshot trong `docs/screenshots/`. |
| Blocker | Chưa streaming mạng thật (Gemini trả JSON một lần → dùng typewriter); LaTeX chưa hỗ trợ align/ma trận phức tạp; BUG-02 (keyword tiếng Anh) chưa sửa. |
| Retro | Giữ: mỗi thay đổi UI đều kèm test/evidence + screenshot. Cải thiện: lần sau tách PR nhỏ hơn (renderer / animation / security) để review dễ hơn; cần đo frame time khi bật nhiều animation trên máy thật. |
