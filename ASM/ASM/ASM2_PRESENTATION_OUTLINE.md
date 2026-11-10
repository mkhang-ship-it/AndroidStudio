# ASM2 — Presentation Outline

**Status:** deck built — `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pptx` and `.pdf` (12 slides, DRAFT). Rehearsal, presentation date, group number and final scores are `[PENDING]`.
**Language:** English. **Target:** 12 slides, 10–12 minutes, followed by questions and a demo.
**Source of facts:** `ASM2_REPORT_COMPACT.md` and the real-final evidence set. A slide must not claim more than the report does.
**Final images:** only `ASM/ASM/evidence/pixel-api-35/real-final/` may appear. Root images 01–22 are DEMO / NON-FINAL and must not be used.

---

## Slide 1 — Title and context
- Unit 22: Application Development — Assignment Part 2.
- AI Study Mentor for BrightPath Learning.
- Student: Nguyen Minh Khang (BC00720), class SE08201.
- Assessor: Mr. Le Nhat Quang. Group number and presentation date `[PENDING]`.
- Speaker note: this is an evaluation of a built application against the Part 1 design, not a proposal.

## Slide 2 — Problem definition and target users
- Students working alone get generic explanations that are hard to verify, not tuned to their level and not preserved.
- Target users: middle school, high school and university students; BrightPath is the product owner.
- Constraints: junior team, 14-week MVP, limited budget, API cost ceiling.
- Speaker note: connect each constraint to a design decision shown later.

## Slide 3 — Requirements and priorities
- FR1–FR10 and NFR1–NFR6 from Part 1.
- MoSCoW cut: FR1–FR4, FR6 and FR8 are MVP; FR5, FR7, FR9 and FR10 follow the core loop.
- Speaker note: deferred items are scheduled work, not failures; say so explicitly.

## Slide 4 — Proposed solution and Part 1 design
- Kotlin, Jetpack Compose, Room, single activity, manual dependency graph.
- Local-first storage; Gemini through `HttpURLConnection` and `org.json`.
- Cache before AI: exact hash, then similarity, to control cost.
- Speaker note: design decisions D-1…D-12 are the M4 baseline.

## Slide 5 — Development method and toolchain
- One-week increments with a Definition of Done and a test rule.
- Toolchain: AGP 9.3.3, Kotlin 2.2.10, Compose BOM 2026.02.01, Room 2.7.2, KSP 2.3.12.
- CI runs `./gradlew test`; thirteen UML sources are kept as text.
- Speaker note: state honestly that 10 of 14 sprint entries are empty.

## Slide 6 — As-built architecture and data flow
- Four layers (`ui`, `domain`, `data`, `platform`), one module, 98 Kotlin files.
- Room schema version 1, seven tables.
- Ask flow: normalise → hash → similarity → provider → persist → history.
- Speaker note: refer to `docs/uml/png/06_component.png` and `09_erd.png` if space allows.

## Slide 7 — Demo 1: ask a real question
- `real-final/01-welcome.png` — no simulation banner.
- `real-final/05-ask-input.png`, `real-final/06-ask-gemini-answer.png`.
- Evidence: HTTP 200, `gemini-3.5-flash-lite`, answer saved to history.
- Speaker note: one real response evidences the path, not the quality of the answer.

## Slide 8 — Demo 2: history, detail and progress
- `real-final/07-history-real-question.png`.
- `real-final/08-question-detail.png`.
- `real-final/09-progress-real.png`: 1 question, 15 s, 0%, +7 XP.
- Speaker note: search, bookmark and subject filters were not exercised.

## Slide 9 — Demo 3: practice and corrected feedback
- `real-final/10-practice-generate.png`, `real-final/11-practice-question.png`.
- `real-final/12-practice-correct-feedback.png`: option B correct, “Chính xác!” (Correct!).
- Fix: letter/number keys resolve to option text; seven regression tests.
- Speaker note: the screenshot used an option-text key; the letter-key path is unit-tested.

## Slide 10 — Evidence and testing
- 159 JVM tests across 13 classes, 0 failures.
- 39 instrumented tests, 0 failures after the fix (`real-final/tests-connected-result.xml`).
- 16 real-final screenshots; root 01–22 are demo archive.
- Speaker note: tests prove logic and data behaviour, not AI accuracy.

## Slide 11 — Evaluation against requirements (P6)
- Met: FR1, FR4 (one response), FR6 list/detail, FR7 generation/feedback, FR8.
- Partial: FR2 onboarding subjects, FR3 image flow, FR9 leaderboard, FR10 reminders.
- Unverified: FR5 improvement blocks.
- NFR3 met; NFR1 measured observation (cold start median 3284 ms; Ask median 913 ms); NFR2 unmeasured; NFR4–NFR6 partial.
- Speaker note: use the report matrix; do not round partial results up.

## Slide 12 — Critical review, feedback and next steps (M5, D2)
- Strengths: testable domain tier, disclosed trade-offs, measured cost control.
- Weaknesses: onboarding subject gap, image lifecycle, inactive abuse filter, subject mapping.
- Risks: hallucinated content, cost control, privacy scope, sprint-record gaps.
- Peer feedback and decisions: `[PENDING: survey responses]`.
- Backlog: 2FA, image lifecycle, offline queue, abuse filter, server-backed leaderboard.
- Speaker note: close on what is evidenced and what is honestly outstanding.

---

## Rubric coverage map

| Slide | Criterion | What it must demonstrate |
|---|---|---|
| 2, 3, 4 | P4 context | The baseline the reviewers were asked to judge |
| 12 | P4, M3 | Survey instrument exists; responses `[PENDING]`; no invented findings |
| 5, 6, 7, 8, 9 | P5 | The application runs and the core loop works |
| 4, 5 | M4 | A specific design baseline plus tool and methodology evidence |
| 11 | P6 | Requirement-by-requirement status with evidence |
| 12 | M5 | Critical review of design, development, testing and risks |
| 12 | D2 | Feedback decisions and future improvements; survey `[PENDING]` |

---

## Demonstration sequence

1. Launch the fixed APK on `Pixel_6_API_35`; show Welcome (`real-final/01-welcome.png`).
2. Register a controlled demo account (`02-register-filled.png`) and complete onboarding (`03`, `04`).
3. Ask “Explain the Pythagorean theorem”; show the real answer (`05`, `06`).
4. Open History and the question detail (`07`, `08`).
5. Open Progress (`09`).
6. Generate one practice question and answer it correctly (`10`, `11`, `12`).
7. Show Profile, Notifications and Logout (`13`, `14`, `15`, `16`).

Fallback: if the device or network fails, use the real-final screenshots in the same order and say that is what is being shown.

---

## Risks and limitations to state

- One real ask and one real quiz were observed; no accuracy claim is made.
- Image question, offline error/recovery and login-after-logout are supplementary captures in `real-final/optional/`; OS-level push was not exercised.
- Search, bookmark and subject filters were not exercised at runtime.
- NFR1 cold start and Ask round trip are measured observations; NFR2 action count is not measured.
- The quiz screenshot used a text answer key; the letter-key path is covered by unit tests.
- The subject chip showed “Toán học” for a response that declared `other`; unfixed.

---

## Production checklist

- [ ] Group number and presentation date confirmed `[PENDING]`.
- [x] Deck built from this outline; no slide claims more than the report.
- [ ] Real-final screenshots inserted at 1080×2400; only status-bar cropping allowed.
- [ ] Demo account prepared and the fixed APK installed.
- [ ] Live demo rehearsed; fallback screenshots ready.
- [ ] Survey responses added to slide 12 once available `[PENDING]`.
- [ ] Timing checked at 10–12 minutes.
