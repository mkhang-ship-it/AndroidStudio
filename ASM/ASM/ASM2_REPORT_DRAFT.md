# ASM2 — Report Draft
## AI Study Mentor: Peer Review, Development and Evaluation

**Unit 22: Application Development — Assignment Part 2 (P4, M3, P5, M4, P6, M5, D2)**
**Document status:** source draft for review. Not a submission copy.
**Prepared:** 2026-11-09
**App under evaluation:** `AI Study/` at commit `79c523a`
**Runtime evidence:** `Pixel_6_API_35` emulator, Android 15 / API 35, captured 2026-11-09; app tree identical to this worktree `[VERIFIED: tree hash e0331693…]`
**Companion documents:** `ASM2_AUDIT_PLAN.md`, `ASM2_EVIDENCE_CHECKLIST.md`, `PEER_REVIEW_FORM.md`, `ASM2_SUBMISSION_CHECKLIST.md`, `FINAL_REPORT_PACKAGE.md`

---

### How to read this draft

Every factual statement is tagged so that the final edit cannot silently promote an assumption into a finding.

| Tag | Meaning |
|---|---|
| `[VERIFIED]` | Re-checked in this working tree on 2026-11-09: file read, command output, or artefact metadata. Safe to submit as fact. |
| `[DOC-ONLY]` | Stated in an existing project document but **not** re-verified in this phase. Keep the source citation, or re-check before submission. |
| `[PENDING]` or `[PENDING: reason]` | Evidence does not exist yet. Must be produced by a human, a device run, or a participant. Do **not** replace with an estimate. |
| `[INSERT FIGURE n: description]` | Placeholder for a figure that must exist before export. |

**Formatting rules fixed by the brief** `[VERIFIED: Assignment part 2.docx]`: the brief says “group report”; this submission is individual, so the front-sheet group and member fields are N/A. Calibri 12, 1.5 line spacing, page numbers, margins left 1.25 cm and right/top/bottom 1 cm, Harvard in-text citation and reference list, final submission as PDF on CMS, cover page = *Assignment front sheet 2*.

**Word budget conflict (decision DEC-01, unresolved)**

| Source | Figure |
|---|---|
| Submission Format, Part 2 brief | 2,000–2,500 words (±10%), excluding tables, diagrams, appendices, references |
| Activity 2, Part 2 brief | 2,500–3,000 words for the development report alone |
| Activity 1, Part 2 brief | Approximately 500 words |

`[PENDING: confirm with tutor which limit governs]`. Working assumption for this draft: Activity 1 ≈ 500 words, Activity 2 ≈ 2,500–3,000 words, Activity 3 ≈ 900–1,200 words, and the whole document is reported with both figures in the introduction. Section targets below use the Activity 2 figure and are marked.

---

## 0. Document control

| Field | Value |
|---|---|
| Document | ASM2 final report — AI Study Mentor |
| Version | 0.1 (source draft) |
| Baseline commit | `79c523a` — “feat: đọc API key từ ai.properties để không bị Android Studio xoá”, 2026-11-02 `[VERIFIED: git log -1]` |
| Branch | `Assignment_Report` (report QA branch); audit baseline `mkhang-ship-it/AUDIT` `[VERIFIED: git rev-parse --abbrev-ref HEAD]` |
| Evidence register | `ASM2_EVIDENCE_CHECKLIST.md` |
| Change rule | No sentence in the final PDF may assert a fact that lacks a `[VERIFIED]` source or an appendix reference |

---

## Front sheet (Assignment front sheet 2 — GROUP)

Completed from the supplied template; group and member fields are N/A — individual submission.

| Field | Entry |
|---|---|
| Qualification | Pearson BTEC Level 5 Higher National Diploma in Computing `[VERIFIED: front-sheet template]` |
| Unit number and title | Unit 22: Application Development `[VERIFIED: front-sheet template]` |
| Assignment title | Evaluate the performance of a business application against its software design document after plan and produce a functional business application `[VERIFIED: Assignment part 2 brief]` |
| Group number | N/A — individual submission |
| Student names and codes | Nguyen Minh Khang (BC00720) — individual submission |
| Class | SE08201 `[VERIFIED: interview]` |
| Assessor name | Mr. Le Nhat Quang `[VERIFIED: interview]` |
| Unit tutor | Đinh Văn Đông `[VERIFIED: Assignment part 2 brief]` |
| Academic year | 2025–2026 `[VERIFIED: Assignment part 2 brief]` |
| Issue date | 1/12/2025 `[VERIFIED: Assignment part 2 brief]` |
| Submission date | `[PENDING: date set by the centre]` |
| Word count (body) | 2,936 (tables, figures, appendices and references excluded; Word raw count including front sheet and tables 4,406) — see DEC-01 |

**Student declaration.** Insert the declaration printed on the front sheet verbatim, including the sentence on AI-tool use, and sign.

`[PENDING: student and assessor signatures and dates; group/member fields N/A — individual submission]`

---

## Table of contents

1. Introduction
2. Activity 1 — Peer review of the problem definition, proposed solution and development strategy (P4, M3)
3. Activity 2 — Development of the functional application (P5, M4)
4. Evaluation against the problem definition and initial requirements (P6)
5. Activity 3 — Critical review of investigation, design, development, testing and risk (M5)
6. Feedback decisions and future improvements (D2)
7. Conclusion
8. Assessment criteria traceability
9. Requirement traceability matrix
10. References
Appendices A–F

**List of tables** — `[PENDING: generate after final pagination]`
**List of figures** — `[PENDING: generate after final pagination]`

---

## 1. Introduction

*Target: 150–200 words.*

This report closes out the BrightPath Learning project begun in Assignment Part 1. Part 1 established the business problem, the target users, a requirement set, a layered architecture and a 14-week Scrum plan; Part 2 reports what was actually built, what a peer review of that plan produced, and how well the delivered Android application meets the commitments made in the design document.

The report follows the three activities in the brief. Activity 1 describes the peer-review instrument and the analysis procedure for the responses. Activity 2 documents the development of **AI Study Mentor**, a local-first Android application that lets a student ask a question by text or image, receive a structured AI explanation, retrieve the answer later, practise with generated questions and track progress. Activity 3 critically reviews the investigation, design, development and testing stages, then justifies which feedback was adopted and which was not.

**Evidence basis and gate.** The application baseline is commit `79c523a` `[VERIFIED]`; a practice-scoring fix was then applied in the evaluation worktree and is covered by tests. Test and build claims are drawn from retained artefacts `[VERIFIED]`. Device evidence exists in three sets on a Pixel 6 emulator (Android 15, API 35): the **demo set** (root 02–19, `FakeAiClient`) and the **pre-final real-key set** (root 20–22) are **DEMO / NON-FINAL**; the authoritative **real-final set** (`real-final/01–16`) was captured on the fixed build with real Gemini `[VERIFIED: real-final/MANIFEST_REAL.txt]`. Anything that still requires a real participant, a timed measurement or broader provider coverage remains `[PENDING]` rather than estimated.

**Word-limit note.** Part 2 states both a 2,000–2,500-word whole-document limit and a 2,500–3,000-word limit for the development report. This draft records both and defers the choice (`DEC-01`). The introduction should state the figure actually used once the tutor confirms.

---

## 2. Activity 1 — Peer review (P4, M3)

*Target: Approximately 500 words. Current status: method and instrument complete, analysis blocked on real responses.*

### 2.1 Objective and approach

The review had four objectives, each tied to a part of the Part 1 design: whether the **problem definition** is clear, whether the **target users** are correctly identified, whether the **requirements** reflect what students actually need, and whether the **proposed solution and 14-week strategy** are feasible for a small junior team. A questionnaire was chosen over interviews because it can be completed asynchronously, yields comparable per-item data, and keeps a written record that can be quoted in Appendix B.

A twelve-item instrument was built: nine five-point Likert items (Q1–Q9) mapped one-to-one onto those four objectives, and three open items (Q10–Q12) covering missing requirements, weaknesses and risks, and suggested improvements. Likert items use the same anchors throughout — `1 = Strongly disagree`, `5 = Strongly agree` — so that responses can be summarised item by item rather than collapsed into a single satisfaction score.

### 2.2 Instrument

The questionnaire exists as a Google Form in draft, titled *AI Study Mentor – Peer Review of Requirements and Proposed Solution*. It is **not published** and contains no responses.

| Item | Text | Type | Anchors |
|---|---|---|---|
| Participant | Participant full name | Short answer | — |
| Participant | Role | Multiple choice | Student · Developer · Teacher/Educator · Other |
| Participant | Job description / study background | Paragraph | — |
| Participant | Review date/context | Short answer | — |
| Q1 | The learning problems that AI Study Mentor aims to solve are clearly defined. | Linear scale 1–5 | Strongly disagree → Strongly agree |
| Q2 | The target users (middle school, high school, and university students) are clearly identified. | Linear scale 1–5 | as above |
| Q3 | The proposed user requirements reflect students' actual learning needs. | Linear scale 1–5 | as above |
| Q4 | The proposed AI Study Mentor application is suitable for addressing the identified learning problems. | Linear scale 1–5 | as above |
| Q5 | The AI question-and-answer feature is useful for supporting students' learning. | Linear scale 1–5 | as above |
| Q6 | The question history and progress tracking features are useful for students. | Linear scale 1–5 | as above |
| Q7 | The selected development tools are appropriate for building the application. | Linear scale 1–5 | as above |
| Q8 | The selected software development methodology is suitable for this project. | Linear scale 1–5 | as above |
| Q9 | The proposed development plan is realistic within the 14-week timeline and limited budget. | Linear scale 1–5 | as above |
| Q10 | What important user requirements or learning problems are missing from the proposed application? | Paragraph | — |
| Q11 | What weaknesses or risks do you identify in the proposed solution or development strategy? | Paragraph | — |
| Q12 | What improvements would you suggest for the AI Study Mentor application or its development plan? | Paragraph | — |

`[VERIFIED: ASM/ASM/PEER_REVIEW_FORM.md — structure read back from the live form, all 16 items required]`
Edit URL: `https://docs.google.com/forms/d/16Hd08rqT475PtOu46Kmxi554dX3TwuaiSb9xiNZ_WmQ/edit`

### 2.3 Participants

The brief requires participant names, roles and job descriptions. Recruitment has not happened yet.

| Participant ID | Name | Role | Job / study context | Date and mode | Consent |
|---|---|---|---|---|---|
| PR-01 | `[PENDING]` | `[PENDING]` | `[PENDING]` | `[PENDING]` | `[PENDING]` |
| PR-02 | `[PENDING]` | `[PENDING]` | `[PENDING]` | `[PENDING]` | `[PENDING]` |
| PR-03 | `[PENDING]` | `[PENDING]` | `[PENDING]` | `[PENDING]` | `[PENDING]` |

Minimum defensible sample: three reviewers, at least one student outside the development team and at least one person with professional development experience. `[PENDING: recruitment — see ASM2_AUDIT_PLAN.md §6]`

### 2.4 Administration and response handling

Procedure to record once responses exist: invitations sent, responses received and valid responses are reported as three separate numbers `[PENDING]`. Raw responses are transcribed verbatim into Appendix B with the participant ID and the date received `[PENDING]`.

### 2.5 Response analysis

**Not yet writable.** The analysis structure is fixed now so it cannot be shaped after the fact:

1. **Per-item summary.** Median and range for Q1–Q9. With fewer than five respondents no percentages and no generalisation beyond the sample.
2. **Weakness identification.** Each weakness must cite at least one quoted response as evidence, then state its impact on the project and a priority.
3. **Thematic grouping.** Q10–Q12 answers are quoted verbatim, then grouped into themes; the count of respondents per theme is reported, not an invented weighting.

`[PENDING: analysis requires real responses — no synthetic data will be entered]`

### 2.6 Opportunities not previously considered (M3)

M3 requires evidence that the review surfaced something new. Two honest outcomes are possible: (a) a requirement or risk that Part 1 did not contain, with an explanation of why it was missed and what it would change; or (b) an explicit statement that the review produced no genuinely new opportunity, together with the limits of a three-person sample.

`[PENDING: derive from §2.5 themes]`

### 2.7 Limitations to state in the final text

The instrument measures opinion about a design document, not the running application. Reviewers see the requirements and architecture, not the built APK, so usability findings from this instrument are predictions. The sample is small and self-selected; the report must not imply statistical representativeness.

---

## 3. Activity 2 — Development (P5, M4)

*Target: 2,500–3,000 words including tables 3–7 and figure captions.*

### 3.1 Design baseline inherited from Part 1

Part 1 fixed twelve architecture decisions (D-1 to D-12) and ten functional requirements. The decisions that shaped the build most are summarised below; the full table remains in `docs/PART1_M1_SOFTWARE_DESIGN.md` `[DOC-ONLY]`.

| ID | Decision | Consequence observed in the build |
|---|---|---|
| D-1 | Jetpack Compose, single activity | Whole UI is Compose; no XML layouts in `ui/` `[VERIFIED: package tree]` |
| D-2 | `navigation-compose` with deep links | Routes include `question/{id}` and `notification/{id}` `[DOC-ONLY]` |
| D-3 | Room as the local source of truth | 7 tables, schema v1 committed `[VERIFIED: app/schemas/.../1.json]` |
| D-5 | `HttpURLConnection` + `org.json`, no Retrofit/OkHttp | Dependency list contains no HTTP client library `[VERIFIED: libs.versions.toml, app/build.gradle.kts]` |
| D-6 | PBKDF2-HMAC-SHA256, 120,000 iterations, per-user salt | `PasswordHasher` `[DOC-ONLY]` (not re-read in this phase) |
| D-7 | EncryptedSharedPreferences session store | `androidx.security:security-crypto 1.1.0-alpha06` is a declared dependency `[VERIFIED: libs.versions.toml]` |
| D-8 | AES-GCM image encryption with a Keystore key | `AesGcmImageStore.store/read` exist `[VERIFIED: data/security/AesGcmImageStore.kt]` |
| D-9 | Exact-hash plus similarity caching to control AI cost | `ReusePolicy` + `QuestionSimilarity` are covered by JVM tests `[VERIFIED: test class list]` |

**Requirement baseline (Part 1).** FR1 authentication; FR2 onboarding; FR3 ask by text and image; FR4 structured AI answer; FR5 improvement content; FR6 history and library; FR7 generated practice; FR8 progress; FR9 gamification; FR10 notifications. Non-functional: NFR1 responsiveness, NFR2 ≤3 actions to submit, NFR3 Android 13+, NFR4 encryption and data minimisation, NFR5 offline reading, NFR6 password policy and lockout. Part 1 also states the MoSCoW cut explicitly: **FR1–FR4, FR6 and FR8 are MVP; FR5, FR7, FR9 and FR10 follow the core loop** `[DOC-ONLY: Part 1 §4]`. The evaluation in §4 uses this cut, so deferred items are not scored as failures.

### 3.2 Agreed scope and what shipped

The status column records what the code supports; what was actually observed on a device is recorded separately in §3.5.1.

| Area | MVP? | Status at `79c523a` | Evidence |
|---|---|---|---|
| Authentication (register, login, logout, delete account, persistent lockout) | Yes | Implemented | `data/repository/AuthRepositoryImpl.kt`, `data/security/LoginThrottle.kt` `[DOC-ONLY]`; lockout persistence has a compiled instrumented case at `androidTest/.../repo/AuthRepositoryTest.kt:204` `[VERIFIED]` |
| Ask by text | Yes | Implemented | `ui/screens/ask/AskScreen.kt`, `data/repository/QuestionRepositoryImpl.kt` `[DOC-ONLY]`; text path observed on device `[VERIFIED: §3.5.1]` |
| Ask by image | Yes | Partial — stored and sent, never re-displayed | `imageStore.read` is called only in `QuestionRepositoryImpl.kt:149`; no UI caller `[VERIFIED: grep]` |
| Structured AI answer | Yes | Implemented | `domain/ai/GeminiAiClient.kt` — `generateContent`, 20,000 ms timeout, retry on 429/5xx `[VERIFIED: lines 128, 133-134, 369, 160]` |
| History, search, bookmark, subject filter | Yes | Implemented | `QuestionAnswerDao.search` uses `LIKE '%' || :keyword || '%'`, not full-text `[VERIFIED: dao lines 38, 42]`; list and detail observed `[VERIFIED: §3.5.1]` |
| Progress summary | Yes | Implemented | `data/repository/ProgressRepositoryImpl.kt` `[DOC-ONLY]`; observed on device `[VERIFIED: §3.5.1]` |
| Onboarding subjects | Yes | **Not implemented** — subjects submitted as an empty set | `OnboardingScreen.kt:132` `submitted = emptySet()` `[VERIFIED: grep]` |
| Improvement content (FR5) | No (post-MVP) | Implemented | `QuestionDetailScreen` `[DOC-ONLY]` |
| Practice (FR7) | No (post-MVP) | Implemented, with a scoring risk | Comparison of option **text** to the answer key at `PracticeScreen.kt:269` `[VERIFIED]` |
| Gamification (FR9) | No (post-MVP) | Partial — no leaderboard | `grep leaderboard` returns no match in `app/src/main` `[VERIFIED]` |
| Notifications (FR10) | No (post-MVP) | Partial | `NotificationRepositoryImpl` `[DOC-ONLY]`; English-only keyword heuristic documented in `docs/BUG_LIST_NP5.md` `[DOC-ONLY]` |

### 3.3 Development process

Development followed the one-week-iteration plan from Part 1, executed as phases NP0–NP5 with a Definition of Done per phase `[DOC-ONLY: docs/ROADMAP_NEW_PHASES.md]`. The sprint log records fourteen week-long sprints; four are populated and ten are blank rows `[VERIFIED: docs/AGILE_SPRINT_LOG.md]`.

| Sprint | Position in the plan | Recorded content |
|---|---|---|
| S01–S03 (weeks 1–3) | Toolchain, CI, Ask→Answer hardening, early auth and notifications | Goal, done, blocker and retrospective recorded `[VERIFIED: file]` |
| S04–S13 (weeks 4–13) | History, progress, AI-cost, gamification, notifications | **Empty rows** `[VERIFIED: file]` |
| S14 (week 14) | Post-MVP polish: renderer, mascot, insights, token metering, lockout persistence | Goal, done, blocker and retrospective recorded `[VERIFIED: file]` |

`[PENDING: reconstruct S04–S13 from commit history, and label it "reconstructed on <date>" in the report — do not present later reconstruction as contemporaneous record-keeping]`

Commit history on the audited branch is linear: 13 commits between 2026-09-30 and 2026-11-02 by a single author `[VERIFIED: git log HEAD]`. The repository as a whole is wider than the audited branch: 36 commits across all refs from four authors, six merge commits, and per-developer branches (`dev_khang`, `dev_tam`, `dev_vinh`, `dev_nguyen`) on the second remote `target` `[VERIFIED: git rev-list --count --all; git shortlog -sne --all; git log --merges --all; git branch -a; git remote -v]`. There are no tags `[VERIFIED: git tag]`. Pull requests cannot be confirmed from a local clone `[PENDING: check the GitHub remote]`.

Continuous integration is configured to run the JVM test suite on pushes and pull requests to `main` or `master`, with JDK 25 and Android SDK platform 37, and with no API key present so the app builds against the offline fake client `[VERIFIED: .github/workflows/android-test.yml]`.

### 3.4 Architecture and data flow

The delivered application keeps the four-layer, single-module structure from Part 1 `[VERIFIED: package tree under app/src/main/java/com/aistudy/aistudy]`.

| Layer | Package | Contents | Count |
|---|---|---|---|
| UI | `ui/` | Compose screens, ViewModels, shared components, theme | 37 Kotlin files |
| Domain | `domain/` | Models, entities, rules (XP, similarity, insights) | 26 Kotlin files |
| Data | `data/` | Room entities/DAOs, repository implementations, security | 31 Kotlin files |
| Platform | `platform/` | Manual dependency graph (`ServiceGraph.kt`) | 2 Kotlin files |

`[VERIFIED: per-package file counts, 2026-11-09. Two further files sit at the package root (MainActivity.kt, AppApplication.kt), giving 98 Kotlin files and 12,103 lines in total.]`

The persistent model is a single Room database at schema version 1 with seven tables `[VERIFIED: app/schemas/com.aistudy.aistudy.data.db.AppDatabase/1.json]`:

| Table | Columns | Purpose |
|---|---|---|
| `users` | 12 | Account, credential hash and salt, XP and level counters |
| `profiles` | 6 | Education level, explanation style, subject set, onboarding timestamp |
| `question_answers` | 21 | Question, answer, improvement fields, hash, bookmark, review time, token counts |
| `quiz_items` | 16 | Generated practice items and nullable correctness |
| `xp_events` | 5 | Append-only XP ledger |
| `user_badges` | 3 | Composite-key badge awards |
| `notifications` | 8 | Type, title, body, deep link, read flag |

The ask path is the one flow that carries the business constraints. Text is normalised to a SHA-256 hash; an exact match is served from the database; otherwise a similarity check compares the new question with stored candidates, and only if both checks miss does the app call Gemini. A cache hit therefore costs no API call, which is the cost-control mechanism required by the brief. `[DOC-ONLY: docs/PART1_M1_SOFTWARE_DESIGN.md §6; behaviour is exercised by ReusePolicyTest, QuestionSimilarityTest and ReusePolicy classes in the JVM suite]`

`[INSERT FIGURE 1: four-layer architecture diagram — source docs/uml/06_component.puml]`
`[INSERT FIGURE 2: ask-with-cache activity diagram — source docs/uml/04_activity_ask_flow.puml]`
`[INSERT FIGURE 3: entity relationship diagram — source docs/uml/09_erd.puml]`
`[INSERT FIGURE 4: use case overview — docs/uml/01_use_case.puml]`
`[INSERT FIGURE 5: domain class model — docs/uml/02_class_domain.puml]`
`[INSERT FIGURE 6: question state model — docs/uml/08_state_question.puml]`

Thirteen PlantUML sources exist in `docs/uml/` and are rendered from text, so the diagrams can be regenerated and diffed rather than redrawn `[VERIFIED: 13 .puml files]`.

### 3.5 Implemented features and their evidence

| Feature | What a user can do | Where it lives | Verification status |
|---|---|---|---|
| Registration and login | Create an account, sign in, sign out, delete the account | `ui/auth/`, `data/repository/AuthRepositoryImpl.kt` | Register and logout observed on device; 13 instrumented cases pass `[VERIFIED: §3.5.1]` |
| Onboarding | Choose education level and explanation style | `ui/onboarding/OnboardingScreen.kt` | Code present `[VERIFIED: file]`; subject selection missing `[VERIFIED: line 132]` |
| Ask a question | Type up to the validated limit, or attach an image | `ui/screens/ask/AskScreen.kt` | Text entry and answer observed; image question captured as supplementary evidence; image is never redisplayed `[VERIFIED: §3.5.1]` |
| Read an answer | Steps, final answer, formulas rendered from a lightweight LaTeX/Markdown pipeline | `domain/ai/GeminiAiClient.kt`, `ui/common/Latex.kt`, `ui/common/MarkdownText.kt` | Layout observed with simulated output; parser and renderer covered by JVM tests; formula rendering not observed `[VERIFIED]` |
| History | Search, filter by subject, bookmark, reopen, delete | `ui/screens/history/`, `QuestionAnswerDao` | List and reopen observed on device; search and filters not exercised; substring-based `[VERIFIED: §3.5.1; dao]` |
| Practice | Generate 1–20 items from a previous question, answer, see feedback | `ui/screens/practice/` | Generation and feedback observed; grading inconsistency visible `[VERIFIED: §3.5.1]` |
| Progress | Totals, per-subject counts, review time, accuracy, XP, offline insights | `ui/screens/progress/`, `domain/rules/ProgressInsights.kt` | Observed on device; `ProgressInsightsTest` passes `[VERIFIED]` |
| Gamification | XP, level, streak, badges | `data/repository/GamificationRepositoryImpl.kt`, `domain/rules/XpRules.kt` | XP, level, streak and badge observed; leaderboard absent `[VERIFIED: §3.5.1]` |
| Notifications | List, unread state, detail screen, deep link | `ui/notifications/` | List, detail and read state observed on device `[VERIFIED: §3.5.1]` |

Application size at the baseline: 98 Kotlin files and 12,103 lines in `app/src/main` `[VERIFIED: recursive count, 2026-11-09]`.

### 3.5.1 Runtime verification on Pixel 6, API 35

**Provenance.** Evidence was captured on 2026-11-09 in the sibling `Assigment` worktree. The baseline commit is `79c523a`; on top of it the evaluation workstream applied an uncommitted practice-scoring fix (two files under `ui/practice` plus a new `PracticeAnswerTest`). The AUDIT tree remains unmodified. Evidence lives in `ASM/ASM/evidence/pixel-api-35/`; the authoritative set is `real-final/`.

**Three evidence sets.** The demo set (root screenshots 02–19) ran `FakeAiClient` and is **DEMO / NON-FINAL**. The pre-final real-key set (root 20–22) proved the key worked but predates the finished build and is also non-final. The real-final set (`real-final/01–16`) is final: fixed APK, real Gemini, every screen observed before capture `[VERIFIED: real-final/MANIFEST_REAL.txt]`.

**Device.**

| Property | Value |
|---|---|
| adb serial | `emulator-5554` (state `device`) |
| Android release | 15 |
| API level | 35 |
| Model | `sdk_gphone64_x86_64` |
| Device / ABI | `emu64xa` / `x86_64` |
| Fingerprint | `google/sdk_gphone64_x86_64/emu64xa:15/AE3A.240806.043/12960925:userdebug/dev-keys` |
| Emulator version | 37.1.11.0 |
| Resolution / density | 1080×2400 / 420 dpi |
| RAM | 1536 MB |

`[VERIFIED: getprop output and AVD config recorded in evidence/AVD_API35_SETUP.md]`

**Build under test.** Package `com.aistudy.aistudy`, versionCode 1, versionName 1.0, debug; compileSdk and targetSdk 37, minSdk 33. Practice-fix APK SHA-256 `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`; earlier builds were `b33d9241…` (fake client) and `0ac0c05a…` (real key, pre-fix) `[VERIFIED: evidence manifests]`.

**AI provider status.** The real-final build ran with `ai.properties` present; the simulation banner is absent and its key is never printed or stored. Demo screenshots show the banner and are not AI evidence.

**Automated results.** The JVM suite is **159 tests across 13 classes, 0 failures**, including seven new `PracticeAnswerTest` cases; the count is corroborated by 159 `@Test` annotations in `app/src/test` `[VERIFIED: practice-fix-unit-tests.txt; annotation count]`. The connected suite passed **39/39** after the fix (JUnit XML `tests="39" failures="0" errors="0" skipped="0"`, timestamp `2026-11-09T16:03:27`, time 12.305), and had also passed 39/39 pre-key and post-key earlier `[VERIFIED: real-final/tests-connected-result.xml]`.

| Suite | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| `ExampleInstrumentedTest` | 1 | 0 | 0 | 0 |
| `QuestionAnswerDaoTest` | 11 | 0 | 0 | 0 |
| `UserDaoTest` | 5 | 0 | 0 | 0 |
| `AuthRepositoryTest` | 13 | 0 | 0 | 0 |
| `MainShellSmokeTest` | 9 | 0 | 0 | 0 |
| **Total** | **39** | **0** | **0** | **0** |

**Real-final flows — FINAL.** All sixteen files are in `real-final/`.

| # | File | Flow | Observed |
|---|---|---|---|
| 1 | `01-welcome.png` | Launch | Welcome, no simulation banner |
| 2 | `02-register-filled.png` | Registration | Form accepted local input |
| 3 | `03-onboarding-step1.png` | Onboarding 1/2 | Education level options |
| 4 | `04-onboarding-step2.png` | Onboarding 2/2 | Explanation style options |
| 5 | `05-ask-input.png` | Ask input | Question typed |
| 6 | `06-ask-gemini-answer.png` | Real answer | Steps, saved to history; HTTP 200 |
| 7 | `07-history-real-question.png` | History | One saved real question |
| 8 | `08-question-detail.png` | Detail | Full question and real answer |
| 9 | `09-progress-real.png` | Progress | 1 question, 15 s, 0%, +7 XP |
| 10 | `10-practice-generate.png` | Practice setup | Source and count controls |
| 11 | `11-practice-question.png` | Gemini quiz | Real multiple-choice question |
| 12 | `12-practice-correct-feedback.png` | Practice answer | Option B correct, “Chính xác!” |
| 13 | `13-profile.png` | Profile | Level 1, 22 XP, badge |
| 14 | `14-notifications.png` | Notifications | One unread item |
| 15 | `15-logout-confirm.png` | Logout | Confirmation dialog |
| 16 | `16-logged-out-welcome.png` | Logout result | Returned to Welcome |

`[VERIFIED: real-final/MANIFEST_REAL.txt; spot-checked 06 and 12 visually]`

**Practice-scoring fix.** The defect compared the selected option text with a stored key that could be a letter (`"A"`) or number. `QuizItem.correctAnswerText()` now resolves a letter or number to the matching option text, and `isCorrectAnswer()` compares against it in both the ViewModel and the UI. `PracticeAnswerTest` covers letter key, numeric key, text key, case/trim handling and two ViewModel cases (7 tests, all passing). Screenshot 12 shows corrected end-to-end feedback; because that Gemini response stored option text, the letter-key path rests on the unit tests, not the screenshot.

**Observed limitations — do not over-claim.**

- **Image question** and **login after logout** were captured after the final gate as supplementary evidence in `real-final/optional/`; they do not replace the sixteen-image final set.
- **Offline behaviour** was exercised: a new question fails with the explicit message “Không gọi được AI. Kiểm tra kết nối mạng rồi thử lại.” and succeeds after reconnection. There is no queue, so the question is not deferred.
- **OS-level push permission** was not exercised.
- **Answer quality** rests on one real ask and one real quiz; no accuracy claim is made.
- **Subject mapping** returned `other` for a geography question earlier while the chip showed “Toán học”; the defect is observed, not fixed.
- **Timings.** Cold start and the Gemini Ask round trip are measured (Table 6); action count, history open time and offline timing remain unmeasured.

`[INSERT FIGURE 7: real-final welcome, no simulation banner — evidence/pixel-api-35/real-final/01-welcome.png]`
`[INSERT FIGURE 8: real Gemini answer — evidence/pixel-api-35/real-final/06-ask-gemini-answer.png]`
`[INSERT FIGURE 9: corrected practice feedback — evidence/pixel-api-35/real-final/12-practice-correct-feedback.png]`

### 3.6 Toolchain evidence

| Tool | Version or role | Evidence |
|---|---|---|
| Android Gradle Plugin | 9.3.3 | `gradle/libs.versions.toml` `[VERIFIED]` |
| Kotlin | 2.2.10 | same `[VERIFIED]` |
| KSP | 2.3.12 | same `[VERIFIED]` |
| Jetpack Compose BOM | 2026.02.01 | same `[VERIFIED]` |
| Room | 2.7.2 | same `[VERIFIED]` |
| Navigation Compose | 2.10.2 | same `[VERIFIED]` |
| Coroutines test | 1.10.2 | same `[VERIFIED]` |
| Compile / target SDK | 37 / 37; `minSdk` 33; `versionName` 1.0 | `app/build.gradle.kts` `[VERIFIED]` |
| JUnit 4 | 4.13.2 | `libs.versions.toml` `[VERIFIED]` |
| Git | Branch `mkhang-ship-it/AUDIT`, HEAD `79c523a` | `git log` `[VERIFIED]` |
| GitHub Actions | `gradlew test` on push/PR to main | `.github/workflows/android-test.yml` `[VERIFIED]` |
| PlantUML | 13 `.puml` sources | `docs/uml/` `[VERIFIED]` |
| Gemini API | `generateContent`, 20 s timeout, retry with backoff | `GeminiAiClient.kt` `[VERIFIED]` |

M4 asks for evidence that the preferred tools were *used*, not merely chosen. The strongest artefact here is the test suite: 13 JUnit classes that exercise the domain rules, the JSON parser, the similarity guard, the LaTeX renderer and the abuse filter `[VERIFIED: test directory]`. The weaker artefacts are the sprint log (10 empty weeks) `[VERIFIED]` and the absence of a review trail on the audited branch `[VERIFIED]`; both must be stated plainly rather than glossed.

### 3.7 Methodology in practice

The team worked in one-week increments with a phase Definition of Done and a rule that every logic change ships with a test `[DOC-ONLY: AI Study/CONTRIBUTING.md]`. That rule is visible in the artefact set: thirteen test classes for a codebase of this size, and an explicit prohibition on calling the real AI provider from tests, with a hand-written fake client instead `[VERIFIED: CONTRIBUTING.md; FakeAiClientTest exists]`.

What the history does and does not show matters for M4. Across all refs there are per-developer branches and six merge commits, which is genuine evidence of branch-based integration `[VERIFIED]`. The audited branch itself is linear, with a single author and no merges `[VERIFIED]`, and no pull-request record can be confirmed from a local clone `[PENDING: check GitHub]`. The report should therefore describe branch-and-merge integration as real, and should not claim a pull-request review workflow that the evidence does not show.

### 3.8 Challenges and defect history

Concrete difficulties, each with the fix that is claimed:

| # | Difficulty | Resolution recorded | Status |
|---|---|---|---|
| 1 | Missing API key blocked end-to-end testing | Hand-written `FakeAiClient` returning fixture JSON; app stays usable offline | `[DOC-ONLY: docs/IMPROVEMENT_LOG.md]` |
| 2 | Gemini returned LaTeX escapes that broke JSON parsing (`\frac` became a control character) | Repair pass before parsing plus repair of already-stored strings | `[DOC-ONLY]`; parser tests exist `[VERIFIED]` |
| 3 | Cache returned an answer for a different but similar question (C# vs Java) | Token normalisation (`c# → csharp`) and a key-token guard | `[DOC-ONLY]`; `QuestionSimilarityTest` exists `[VERIFIED]` |
| 4 | Login lockout was in memory and vanished on restart | Persisted throttle in shared preferences | `[DOC-ONLY]`; case compiled at `AuthRepositoryTest.kt:204` `[VERIFIED]` |
| 5 | Review-time tracking lost writes | Lifecycle-aware 15-second accrual in Ask and detail screens | `[DOC-ONLY]` |
| 6 | Deleting a merged history row left duplicates visible | Delete by user and question hash | `[DOC-ONLY]`; case compiled at `QuestionAnswerDaoTest.kt:209` `[VERIFIED]` |

Defect register status: 22 entries, 18 fixed and 4 open at the freeze `[DOC-ONLY: docs/BUG_LIST_NP5.md]`. The four open items are the English-only mistake-alert keywords, the missing global leaderboard, 2FA without a verification flow, and the absence of two-way sync.

`[PENDING: verify the four open items against the code once more immediately before submission; two of them were re-confirmed this phase — leaderboard absent and 2FA schema-only]`

Number discipline: the project documents disagree with each other. `docs/IMPROVEMENT_LOG.md` says 143 unit tests, `docs/TEST_REPORT_NP5.md` says 76, and Part 1 mentions 120. The baseline measured **152 tests across 12 classes**; after the practice fix the suite is **159 tests across 13 classes** `[VERIFIED: @Test annotation count; practice-fix-unit-tests.txt]`. The report must quote the current 159 and never the superseded figures.

### 3.9 Support documentation and deliverable package

| Item | Location | Status |
|---|---|---|
| Build and test instructions | `README.md`, `AI Study/CONTRIBUTING.md` | `[VERIFIED: present]` |
| API-key handling without committing secrets | `AI Study/ai.properties.example`, `local.properties.example`, `BuildConfig` field | `[VERIFIED: files and build script]` |
| Architecture decisions D-1…D-12 | `docs/PART1_M1_SOFTWARE_DESIGN.md` | `[VERIFIED: file]` |
| Test plan | `docs/PART1_M1_TEST_PLAN.md` | `[VERIFIED: file]` |
| UML set | `docs/uml/` (13 sources) | `[VERIFIED]` |
| Debug APK | `AI Study/app/build/outputs/apk/debug/app-debug.apk`, 35,596,239 bytes (33.95 MiB), written 2026-11-09 16:19 | `[VERIFIED]` |
| User guide | `ASM/ASM/USER_GUIDE.md` | `[VERIFIED: supplied]` |
| Demo account procedure | `[PENDING: not supplied]` | — |
| Screenshot set | 16 final in `real-final/`; root 01–22 demo archive | `[VERIFIED: ASM/ASM/evidence/pixel-api-35/real-final/]` |

The APK is a debug build signed with the debug key. That is acceptable for demonstration and marking, but the report should say so rather than implying a release-signed artefact.

---

## 4. Evaluation against problem definition and requirements (P6)

*Target: 900–1,200 words plus the two matrices.*

### 4.1 Evaluation basis and method

The application is evaluated at commit `79c523a` `[VERIFIED]` against the requirement set in Part 1 `[DOC-ONLY]`. Three kinds of evidence are kept apart, because conflating them is the most common way this section becomes dishonest:

| Evidence class | What it can support | Current supply |
|---|---|---|
| Automated test result | Correctness of pure logic (rules, parsing, similarity) | 159 JVM tests, 0 failures; 39 instrumented after the fix `[VERIFIED]` |
| Code inspection | Existence and shape of a behaviour | Performed for the findings in §4.6 `[VERIFIED]` |
| Runtime observation | Whether a feature works for a user | Sixteen final flows (real-final) `[VERIFIED]`; cold start and Ask round trip measured, action count and history timing `[PENDING]` |

A requirement is marked **Met** only when a test or an observed run demonstrates the end-to-end behaviour. Where only code exists, the status is **Implemented, unverified**. Where the code demonstrably does not do the thing, the status is **Partial** or **Not met** with the file reference.

### 4.2 Against the problem definition

Part 1 framed the problem as: a student working alone cannot get an explanation of a specific exercise at the right level, loses the explanation after the session, and cannot tell whether they are improving `[DOC-ONLY]`.

Taken as four user-visible outcomes:

| Outcome | Delivered? | Comment |
|---|---|---|
| Ask a specific question in the student's own words or as a photo | Partly | Real text answer observed in real-final; the image question is captured as supplementary evidence and the stored image is never displayed again `[VERIFIED: §3.5.1]` |
| Receive an explanation structured for study, not just an answer | Yes for one response | Real Gemini steps and final answer observed once; quality across queries is unmeasured `[VERIFIED: §3.5.1]` |
| Keep the explanation and find it again | Yes | History and detail observed in real-final; local Room storage `[VERIFIED: §3.5.1]` |
| See whether performance is improving | Yes | Progress observed in real-final: one question, 15 seconds, 0% accuracy, +7 XP `[VERIFIED: §3.5.1]` |

The problem definition is therefore addressed in the built artefact at the level of capability. The unresolved part is empirical: no user has yet used the build under observation, so the claim that it *reduces* study friction is a design hypothesis, not a finding.

### 4.3 Functional requirements assessment

Status vocabulary: **Met** (demonstrated by test or observed run), **Implemented, unverified** (code present, no execution evidence), **Partial** (some of the requirement missing), **Not met**, **Not tested** (the requirement is behavioural and no test or run exists).

| Req | Commitment | Status | Basis |
|---|---|---|---|
| FR1 | Register, login, session, five-failure lockout | **Met** | Register and logout observed in real-final; 13 instrumented auth cases pass, including `login_lockout_vanConSauKhiTaoRepositoryMoi` `[VERIFIED: §3.5.1; XML]` |
| FR2 | Onboarding: level, subjects, style | **Partial** | Onboarding completed on device, but subjects are still submitted empty `[VERIFIED: OnboardingScreen.kt:132; §3.5.1]` |
| FR3 | Ask by text **and image** | **Partial** | Real text answer observed; image question captured as supplementary evidence, and a stored image is never redisplayed `[VERIFIED: §3.5.1; imageStore.read only in repository]` |
| FR4 | Structured answer with subject and steps | **Met (one response)** | A real Gemini response with steps and a final answer was observed `[VERIFIED: §3.5.1]` |
| FR5 | Simplified, alternative, key concepts, mistakes, follow-ups | Implemented, unverified | Detail screen exists; the five generated blocks were not exercised `[DOC-ONLY]` |
| FR6 | History, search, bookmark, subject filter | **Met for list and detail** | Saved question and answer reopened; search, bookmark and filter not exercised `[VERIFIED: §3.5.1; DAO LIKE search]` |
| FR7 | Generated practice with feedback | **Met for generation and feedback** | Gemini quiz generated and the correct option marked “Chính xác!”; letter-key path covered by 7 unit tests `[VERIFIED: §3.5.1]` |
| FR8 | Progress: totals, subjects, accuracy, review time | **Met** | Totals, per-subject count, review time and XP observed after real activity `[VERIFIED: §3.5.1]` |
| FR9 | XP, levels, badges, leaderboard | **Partial** | XP, level, streak and badge observed; leaderboard absent `[VERIFIED: §3.5.1; grep]` |
| FR10 | Reminders, mistake alerts, summaries | **Partial** | Notification list observed; reminder generation remains open `[DOC-ONLY]` |

### 4.4 Non-functional requirements assessment

Cold start and the Gemini Ask round trip were measured on the fixed APK (Table 6). Action count, history open time and offline timing remain unmeasured. Offline behaviour was exercised functionally: a new question fails with an explicit connection error and succeeds after reconnection, with no queue `[VERIFIED: NFR_MEASUREMENTS.md; real-final/optional/]`.

| NFR | Commitment | Status | Basis |
|---|---|---|---|
| NFR1 | UI responds within 100 ms, progress during AI call | **Measured (observation)** | Cold start n=5: 3041, 3108, 3284, 3310, 5139 ms (median 3284, min 3041, max 5139); Gemini Ask round trip n=3: 861, 913, 2207 ms (median 913, min 861, max 2207), 3/3 HTTP 200; no SLO defined `[VERIFIED: NFR_MEASUREMENTS.md]` |
| NFR2 | Question submittable in at most three actions | **Not tested** | Action count not measured `[PENDING]` |
| NFR3 | Android 13 (API 33) or later | **Met** | `minSdk = 33` `[VERIFIED: app/build.gradle.kts]` |
| NFR4 | Encryption at rest, data minimisation, GDPR/COPPA | **Partial** | Password hashing, encrypted session and encrypted image files exist; Room body columns are plaintext; the abuse filter is implemented but never called `[VERIFIED: AbuseFilter referenced only by its own file]` |
| NFR5 | Saved answers readable offline | **Partial** | Room serves saved data offline; offline submission shows an explicit error and retries successfully after reconnection; there is no connectivity observer or pending queue `[VERIFIED: NFR_MEASUREMENTS.md; no ConnectivityObserver/PendingSync matches]` |
| NFR6 | Password strength, optional 2FA, lockout | **Partial** | Strength and lockout exist; the 2FA column and setter exist but nothing reads them `[VERIFIED: twoFactorSecret only in User.kt and UserDao.kt]` |

### 4.5 Feature quality versus user expectations

Two quality questions must be separated. The first is technical correctness: does the pipeline behave as specified? Evidence exists at three levels — 159 passing JVM tests, 39 instrumented tests green after the fix, and sixteen real-final flows against Gemini `[VERIFIED: §3.5.1]`. The second is user acceptance: does it help a student? That still requires the peer review (§2) and a usability session `[PENDING]`, so the report must present acceptance as assumed, not demonstrated.

Two residual quality risks remain:

- **Practice scoring — fixed.** The earlier defect compared the chosen option's text with a key that could be a letter `[VERIFIED: PracticeScreen.kt]`. The fix normalises the key before comparison; seven `PracticeAnswerTest` cases pass and real-final screenshot 12 shows “Chính xác!” for the correct option.
- **Subject mapping.** A real provider response returned `"subject": "other"` while the UI chip displayed “Toán học”, an observed defect that is not fixed `[VERIFIED: §3.5.1]`.

### 4.6 Gaps and residual limitations

| Finding | Consequence for the user | Location |
|---|---|---|
| Onboarding collects no subjects | Profile subject set is empty until edited later, so personalisation is weaker than designed | `OnboardingScreen.kt:132` `[VERIFIED]` |
| Attached image never re-displayed | A student cannot reopen the photo they asked about | `QuestionRepositoryImpl.kt:149` is the only reader `[VERIFIED]` |
| Abuse filter never called | Blocked-content protection is present in code but inactive at runtime | `domain/rules/AbuseFilter.kt` only reference `[VERIFIED]` |
| Image files never deleted on account removal | Encrypted images survive account deletion | `AesGcmImageStore.delete` has no caller `[VERIFIED]` |
| No offline queue | A question asked without connectivity fails outright | No `ConnectivityObserver`/`PendingSync` `[VERIFIED]` |
| 2FA schema only | Enabling 2FA would not protect anything | `User.kt:39-40`, `UserDao` `[VERIFIED]` |
| No leaderboard | Gamification is device-local | `grep` no match `[VERIFIED]` |
| Room body columns unencrypted | The Part 1 claim that all user data is encrypted is only partly met | Schema inspection `[VERIFIED]` |
| Ten sprint entries empty | Process evidence is thinner than the plan implies | `docs/AGILE_SPRINT_LOG.md` `[VERIFIED]` |

Each of these is a legitimate, evidenced gap. None of them is a reason to reword the design document; the report should present them as the difference between the commitment and the delivery, which is exactly what P6 asks for.

---

## 5. Activity 3 — Critical review (M5)

*Target: 900–1,200 words.*

### 5.1 Contribution of the systems investigation

The investigation stage produced two things that visibly shaped the build: the decision to keep everything local, and the decision to treat AI cost as a first-class constraint.

Keeping the application local-first removed an entire backend from a 14-week schedule with a junior team, and made NFR5 (offline reading) almost free because Room is the only source of truth. The cost was stated at the time — no synchronisation, no cross-device leaderboard, no server-side validation — and those are precisely the open items in §4.6. The investigation did not hide the trade-off; the delivery honoured it.

Treating AI cost as a constraint produced the caching layer and its similarity guard. The guard matters more than the cache: an early version reused an answer for a different question that happened to share most of its words, and the fix introduced token normalisation and a key-token guard `[DOC-ONLY: docs/IMPROVEMENT_LOG.md §2.11; covered by QuestionSimilarityTest]`. That is a case where investigating the cost model exposed a correctness risk that the functional requirements alone would not have surfaced.

Where the investigation fell short: it did not produce a usability baseline. No interview notes or task timings from real students appear in the repository, so the requirement priorities rest on the brief and on reasoned argument rather than on observed student behaviour. That weakens the connection between the problem statement and the requirement set.

### 5.2 Critical review of the design stage

**What the design got right.** The four-layer split with a domain layer that does not import Android infrastructure turned out to be the single most valuable decision. It allowed 159 JVM tests to run without an emulator `[VERIFIED]`, which is what made a test-first rule survivable for a small team with limited hardware.

**What it got wrong or left vague.** Three things stand out.

1. *The onboarding design and the onboarding code disagree.* The design specified level, subjects and style. The implementation collects level and style and submits an empty subject set `[VERIFIED: OnboardingScreen.kt:132]`. The defect survived to freeze because no test asserted the subject set after onboarding.
2. *Search was designed as a first-class feature but implemented as a substring match.* The design document acknowledges the `LIKE` limitation `[DOC-ONLY]`, so this is a disclosed trade-off rather than a hidden one — but the report should be clear that "search" in the delivered app is a filter, not an index.
3. *The security scope was over-promised in one sentence.* Part 1 says user data is encrypted; the database body columns are not `[VERIFIED: schema]`. The correct framing is that credential material, the session and image files are protected, while question and answer text rely on Android sandboxing.

A design-stage risk that materialised: the assumption that a single developer-facing AI provider could be treated as reliable. The mitigation — typed errors, retry with backoff, a 20-second timeout, and a fake client for offline work — is sound, but it does not address content accuracy, which remains unmeasured.

### 5.3 Critical review of the development stage

Tooling fit was good. Compose, Room, manual dependency injection and `HttpURLConnection` with `org.json` produced a build that needs no network to compile and no annotation-processing framework beyond Room/KSP `[VERIFIED: build files]`. Removing Retrofit and Hilt kept the dependency surface small enough that a junior team could reason about the whole graph.

Methodology fit was partial. One-week increments with a Definition of Done worked; the record does not. Ten of fourteen sprint entries are empty `[VERIFIED: AGILE_SPRINT_LOG]`. The wider repository shows per-developer branches and six merges `[VERIFIED]`, but the audited branch is a single-author linear history `[VERIFIED]`, so attribution and review evidence for the delivered code is thin. The honest reading is that the team practised test-gated commits and phase-level increments, and did not maintain sprint records contemporaneously.

Two process weaknesses had direct quality costs. First, the audited branch carries no review trail — a single author, straight-line history — and that shows up as defects a second reader would likely have caught: the empty subject set and the text-versus-key quiz comparison are both one-line issues visible in a diff. Second, the delivered code is a consolidated snapshot: 13 commits by one author against 36 commits and four authors across the wider repository `[VERIFIED]`, which makes a change hard to attribute to a person or a review.

### 5.4 Critical review of the testing stage

The JVM suite is the strongest part of the evidence. 159 tests across thirteen classes, all passing, covering the exact areas where silent defects hide: similarity thresholds, XP level boundaries, LaTeX repair, JSON parsing, insight generation, the abuse filter and the practice answer key `[VERIFIED: annotation count; practice-fix-unit-tests.txt]`. The instrumented suite has 39-test runs that are green after the fix on the API 35 emulator `[VERIFIED: XML]`.

The weaknesses are equally clear and should be stated without euphemism:

| Weakness | Why it matters | Evidence |
|---|---|---|
| The instrumented suite has been executed three times, on an emulator only | The happy path is demonstrated on three runs; there is no evidence from physical hardware | 39 tests, 0 failures on `Pixel_6_API_35`, pre-key, post-key and post-fix `[VERIFIED]`; physical device `[PENDING]` |
| The smoke test is structural, not behavioural | `MainShellSmokeTest` asserts destination metadata rather than user journeys, which is why the practice grading inconsistency escaped both suites | Test names in the JUnit XML `[VERIFIED]` |
| No test asserts the onboarding subject set | Allowed FR2 to be delivered incomplete | `OnboardingScreen.kt:132` `[VERIFIED]` |
| No Compose behaviour test for the ask-to-answer path | The most important journey is covered only by a manual run | Test directory listing `[VERIFIED]`; runtime flow 4 `[VERIFIED]` |
| No test for image lifecycle | Allowed the "stored but never shown / never deleted" pair of defects | `imageStore.read` single caller `[VERIFIED]` |
| No accuracy, privacy or offline test | NFR4 and NFR5 rest on inspection, not on evidence | §4.4 `[VERIFIED]` |
| Test-count claims in project documents disagree | 143, 76 and 120 all appear; the current measured figure is 159 | `[VERIFIED]` |

The deeper critique is one of *shape*: the suite is heavy on pure functions and light on integration, and the sixteen device flows were executed by hand rather than scripted. That closes the "does it run at all" question for the happy path but creates little regression protection; the practice grading defect is the proof, because it reached the freeze in a flow no UI test covered, and the fix is now guarded by unit tests rather than by a UI test.

### 5.5 Risk management review

Part 1 registered ten risks; the build makes some of them observable:

| Risk (Part 1) | What actually happened | Residual exposure |
|---|---|---|
| R1 AI cost overrun | Cache plus similarity guard implemented; token counts now read from the response `[DOC-ONLY]` | Budget guard still not enforced by measured daily spend `[PENDING]` |
| R2 provider outage | Typed errors, retry with backoff, 20-second timeout `[VERIFIED: GeminiAiClient]` | No offline queue, so the user loses the question rather than queuing it `[VERIFIED]` |
| R3 hallucinated content | Not mitigated; only operational failures are handled. One real response was observed, which is far too few to assess accuracy | **Highest unresolved risk** — disclose it, do not invent numbers |
| R5 privacy | Session and images encrypted; database body columns plaintext; images not deleted on account removal `[VERIFIED]` | Retention failure is a genuine compliance gap |
| R6 schedule | MVP scope delivered; post-MVP items partially delivered early | Sprint records do not support a claim of controlled slippage `[VERIFIED: empty rows]` |
| R7 junior capability | Mitigated by convention documents and a fake AI client `[VERIFIED: CONTRIBUTING.md]` | Review discipline was not practised on the audited branch `[VERIFIED]` |

A risk that was **not** in the register and should have been: dependency on a single model provider whose response schema changed during development. The parser was rewritten mid-project to a `steps`/`finalAnswer` structure `[DOC-ONLY: IMPROVEMENT_LOG §2.10]`, which is exactly the kind of churn a pinning and contract-test strategy would bound.

### 5.6 Impact on reliability, usability, security and effectiveness

| Attribute | Action taken | Observed effect | Limit |
|---|---|---|---|
| Reliability | Typed error taxonomy, retry with backoff, fake client, 159 passing JVM tests, 39 instrumented after the fix | Failure modes are surfaced deliberately; the loop completes against the real provider | Emulator-only; small provider sample `[PENDING]` |
| Usability | Structured answers, corrected practice feedback, progress insights | Real-final flows cover the whole interface; the correct option is now marked “Chính xác!” | No usability session has been run `[PENDING]` |
| Security | PBKDF2, encrypted session, AES-GCM images, persistent lockout | Credential and image handling is defensible; lockout cases pass on device | Database body unencrypted; abuse filter inactive; 2FA absent `[VERIFIED]` |
| Effectiveness | The whole loop completes against the real provider | The product hypothesis can now be tested | Small provider sample; no evidence yet that it improves learning outcomes |

---

## 6. Feedback decisions and future improvements (D2)

*Target: 700–900 words.*

### 6.1 Peer-review feedback decisions

`[PENDING: requires §2.5 responses]`

The table below is fixed in advance; it is filled only with real feedback.

| FB ID | Feedback (verbatim, participant ID) | Decision | Section or module affected | Commit and test | Justification |
|---|---|---|---|---|---|
| FB-01 | `[PENDING]` | Adopt / modify / defer / reject | `[PENDING]` | `[PENDING]` | `[PENDING]` |

The distinction the brief insists on — feedback acted upon versus feedback not acted upon — must be preserved: "planned" is not "done", and a deferral needs a stated condition for revisiting.

### 6.2 Improvements already made and evidenced

Separate from peer feedback, several improvements were made in response to the team's own testing and review. These are adoptions with an evidence trail, and they belong in D2 because the criterion asks for justified improvement, not only for peer-driven change.

| Change | Problem it addressed | Evidence | Residual limit |
|---|---|---|---|
| LaTeX/Markdown rendering for answers | Answers displayed raw markup, which made mathematics unreadable | `ui/common/Latex.kt`, `MarkdownText.kt`; `LatexTest` in the 159-test suite `[VERIFIED: files and test class]` | Not a full LaTeX engine; matrices and multi-line alignments degrade |
| Persistent login lockout | Lockout state vanished when the process died | `data/security/LoginThrottle.kt`; case at `AuthRepositoryTest.kt:204` | Passed in the 39-case instrumented run `[VERIFIED]` |
| Token usage capture | Cost could not be measured because tokens were always zero | `GeminiAiClient.usageTokens` reads `usageMetadata` `[VERIFIED: line 379]` | No budget enforcement |
| Offline progress insights | Progress was numbers only, with no interpretation | `domain/rules/ProgressInsights.kt`; `ProgressInsightsTest` `[VERIFIED]` | Template-based, not model-generated |
| Duplicate-aware history deletion | Deleting merged questions left duplicates behind | `deleteByHashes` and its DAO test `[DOC-ONLY]` | — |
| Dependency and secret hygiene | API key could be lost or committed | Key read from `ai.properties` → `local.properties` → environment; `BuildConfig` field `[VERIFIED: app/build.gradle.kts]` | Fixed debug APK embeds the key at build time; the package ships a sanitized offline APK with no key `[VERIFIED: PACKAGE_QA.md]` |
| Practice answer-key normalisation | A correct option could be marked wrong when the key was a letter | `PracticeViewModel` normalisation; 7 `PracticeAnswerTest` cases; fixed APK `c7c8f9e2…`; real-final screenshot 12 | Letter-key path is unit-tested, not UI-tested |

### 6.3 Items not acted upon

No peer-review feedback exists yet (see §2.5), so this table records the known defects and scope decisions that were deliberately not acted upon. Peer-feedback rows are added to §6.1 when responses arrive; the two registers must not be merged.

| Item | Why not acted upon | Revisit condition |
|---|---|---|
| Global or friends leaderboard | Needs a server, authentication of submitted scores and anti-cheat; out of scope for a local-first MVP | When a backend exists and score submission can be validated server-side |
| True two-factor authentication | Requires secret generation, enrolment UI and login enforcement; the schema alone is misleading and is treated here as a known gap | Before any real student data is collected |
| Two-way synchronisation | Needs a conflict policy (last-write-wins versus merge) and an outbox; not a coding problem alone | When multi-device use is a stated requirement |
| Server-side streaming of answers | The provider returns one JSON object containing several fields; streaming would require a plain-text protocol plus a second metadata call, raising cost | When per-answer latency becomes the dominant complaint |

Each rejection is a scope decision, not an oversight. The report should say so explicitly, because an unexplained omission reads as a defect.

### 6.4 Opportunities and future development

| Priority | Opportunity | Rationale | Evidence basis |
|---|---|---|---|
| 1 | Complete 2FA verification | A schema-only 2FA is worse than none: it implies protection that does not exist | `User.kt:39-40` `[VERIFIED]` |
| 2 | Show the attached image and manage its lifecycle | Two defects share one cause: the image store is write-mostly | `imageStore.read`/`delete` callers `[VERIFIED]` |
| 3 | Offline queue with explicit user feedback | Turns a hard failure into deferred work | No queue exists `[VERIFIED]` |
| 4 | Activate the abuse filter | Implemented and unit-tested but never wired in | `AbuseFilter.kt` sole reference `[VERIFIED]` |
| 5 | Server-side leaderboard | Completes FR9 as written | No leaderboard code `[VERIFIED]` |
| 6 | Streaming answers | Improves perceived latency | Requires protocol change `[DOC-ONLY]` |

These are genuine opportunities surfaced by evidence rather than restatements of Part 1 requirements, with the exception of the leaderboard, which Part 1 did specify and which is therefore better described as an unmet requirement than as a new idea.

### 6.5 Evaluation of the improvement strategy

The strategy that produced the most value was *make the reading experience correct before making the feature list longer*. Rendering formulas and repairing the JSON escaping turned a technically working answer into a usable one, and the accompanying tests make the fix durable.

The least effective strategy was late consolidation. With no review step on the audited branch, two one-line defects reached freeze; one was caught and fixed after review, the other remains open. Neither would have survived a second reader.

The trade-off the team chose — breadth of features over depth of runtime verification — is defensible for a marked coursework deliverable, but it is the reason P6 contains so many "Implemented, unverified" rows. Naming that trade-off honestly is stronger than claiming coverage that the evidence does not support.

---

## 7. Conclusion

*Target: 200–250 words.*

AI Study Mentor delivers the core loop the BrightPath scenario asked for: a student can ask a question, receive a structured explanation, keep it, revisit it and see evidence of progress. The design decisions taken in Part 1 — local-first storage, a layered architecture with a pure domain tier, and a cache that treats AI calls as a cost — held up in implementation and are visible in the code, the database schema and the test suite.

The evidence supports a precise claim. 159 JVM tests pass `[VERIFIED]`. The instrumented suite is green at 39/39 after the practice fix on a Pixel 6 emulator with Android 15 (API 35) `[VERIFIED]`, and had also passed before and after the key was configured. Final runtime evidence is the sixteen-image real-final set, which records the whole loop against real Gemini on the fixed build `[VERIFIED: real-final/MANIFEST_REAL.txt]`. Cold start (median 3284 ms, n=5) and the Gemini Ask round trip (median 913 ms, n=3, 3/3 HTTP 200) are measured observations with no defined SLO; action count and history timing remain `[PENDING]`. No usability session has been run `[PENDING]`, and the peer-review responses that P4, M3 and D2 depend on do not yet exist `[PENDING]`. The report must present those as outstanding work rather than as completed activity.

Three defects remain: onboarding captures no subjects, images are stored but never shown or deleted, and the abuse filter is not wired in. Subject mapping is a fourth, observed on a real provider response. The practice grading defect found earlier is fixed and covered by seven regression tests, and the report records that change rather than the earlier failure.

---

## 8. Assessment criteria traceability

| Criterion | Wording (abridged) | Where addressed | Status | Evidence that exists |
|---|---|---|---|---|
| **P4** | Conduct a peer review of problem definition, solution and strategy; document feedback | §2.1–2.7; Appendix A, B | **Not yet met** — instrument ready, review not conducted | Live draft form, 16 required items `[VERIFIED]`; participant table `[PENDING]` |
| **M3** | Interpret peer feedback and identify opportunities not previously considered | §2.5–2.6, §6.4 | **Not yet met** — blocked on responses | Method fixed; findings `[PENDING]` |
| **P5** | Develop a functional business application with support documentation | §3.2–3.9; Appendix E | **Partial** — application runs on the target device and the real-final loop is verified; support documentation incomplete | APK installed and exercised on API 35; 159/159 unit and 39/39 connected after the fix `[VERIFIED]`; user guide supplied; demo procedure `[PENDING]` |
| **M4** | Evidence of preferred tools, techniques and methodologies | §3.3, §3.6, §3.7, Appendix E | **Met on tools; partial on methodology evidence** | Versions, CI workflow, 13 UML sources, 13 test classes `[VERIFIED]`; sprint log 10/14 empty `[VERIFIED]` |
| **P6** | Review performance against problem definition and initial requirements | §4.1–4.6, §9 | **Partial** — runtime evidence for core flows; NFR measurements pending | Device flows and code-level matrix `[VERIFIED]`; cold start and Ask round trip measured; action count and history timing `[PENDING]` |
| **M5** | Critically review design, development and testing, including risks | §5.1–5.6 | **Drafted** | Findings grounded in file references `[VERIFIED]` |
| **D2** | Justify improvements made because of feedback, and feedback not acted upon | §6.1–6.5 | **Partial** — self-evidenced improvements complete, peer feedback pending | Change table with file references `[VERIFIED]`; FB rows `[PENDING]` |

---

## 9. Requirement traceability matrix

Consolidated view for P6. Statuses follow the vocabulary in §4.3.

| ID | Requirement | Status | Evidence | Appendix ref |
|---|---|---|---|---|
| FR1 | Registration, authentication, session, lockout | **Met** | Register observed in the real-key session; both 39-case runs include login, lockout and logout cases `[VERIFIED]` | C, D |
| FR2 | Onboarding: level, subjects, style | **Partial** | Onboarding completes on device; `OnboardingScreen.kt:132` sends empty subjects `[VERIFIED]` | C, D |
| FR3 | Ask by text and image | **Partial** | Real text answer observed; supplementary image question captured; image never redisplayed `[VERIFIED]` | C, D |
| FR4 | Structured AI answer | **Met (one response)** | Real Gemini response with steps observed once; HTTP 200 recorded `[VERIFIED]` | C, D |
| FR5 | Improvement content (5 kinds) | Implemented, unverified | Detail screen exists; generated blocks not exercised `[DOC-ONLY]` | C |
| FR6 | History, search, bookmark, subject filter | **Met for list and detail** | Saved question and answer reopened in real-final; search/bookmark/filter not exercised `[VERIFIED]` | C, D |
| FR7 | Generated practice with feedback | **Met for generation and feedback** | Gemini quiz and corrected “Chính xác!” feedback; letter-key path covered by 7 unit tests `[VERIFIED]` | C, D |
| FR8 | Progress tracking | **Met** | Progress observed after real activity `[VERIFIED]` | C, D |
| FR9 | XP, levels, badges, leaderboard | **Partial** | XP, level, streak and badge observed; leaderboard absent `[VERIFIED]` | C, D |
| FR10 | Reminders, mistake alerts, summaries | **Partial** | Notification list observed; reminder generation unverified `[DOC-ONLY]` | C, D |
| NFR1 | UI response within 100 ms during AI call | **Partial (observation)** | Cold start n=5 (median 3284 ms) and Ask round trip n=3 (median 913 ms, 3/3 HTTP 200) are measured; a dedicated 100 ms UI-frame measurement is still pending `[PENDING: UI-frame timing]` | F |
| NFR2 | ≤3 actions to submit | **Not tested** | No action count captured `[PENDING]` | F |
| NFR3 | Android 13+ | **Met** | `minSdk = 33`; executed on API 35 `[VERIFIED]` | E |
| NFR4 | Encryption and data minimisation | **Partial** | Body columns plaintext; abuse filter unused; images not deleted `[VERIFIED]` | C |
| NFR5 | Offline reading | **Partial** | Code-level: no queue or connectivity observer; offline error/recovery observed, with no timing `[VERIFIED]` / `[PENDING]` | C, F |
| NFR6 | Password policy, 2FA, lockout | **Partial** | Lockout passes instrumented tests; 2FA is schema-only `[VERIFIED]` | C |

---

## 10. References

`[PENDING: complete before submission. Harvard style, alphabetical, with access dates for online sources.]`

**Internal project sources** (cite as project documentation, not as published literature):

| Ref | Source |
|---|---|
| [P1] | `docs/PART1_M1_SOFTWARE_DESIGN.md` — architecture decisions D-1…D-12, schema, ask flow |
| [P2] | `docs/PART1_M1_TEST_PLAN.md` — test strategy and case catalogue |
| [P3] | `docs/BUG_LIST_NP5.md` — 22-entry defect register |
| [P4] | `docs/IMPROVEMENT_LOG.md` — post-MVP improvement record |
| [P5] | `docs/AGILE_SPRINT_LOG.md` — 14-sprint log |
| [P6] | `docs/D2_FUTURE_NP5.md` — future work rationale |
| [P7] | `README.md`, `AI Study/CONTRIBUTING.md` — build and contribution rules |
| [P8] | `ASM2_EVIDENCE_CHECKLIST.md`, `PEER_REVIEW_FORM.md` — evidence register and instrument |

**Candidate external sources** — verify edition, year and access date before use; do not cite a source that is not actually consulted:

| Ref | Candidate | Used for |
|---|---|---|
| [E1] | Android Developers (2026) *Guide to app architecture* | Justification of the layered architecture and unidirectional dependencies |
| [E2] | Android Developers (2026) *Save data locally with Room* | Local-first storage and schema versioning |
| [E3] | Google (2026) *Gemini API: generateContent reference* | Response schema, timeout and retry design |
| [E4] | Sommerville, I. (2016) *Software Engineering*. 10th edn. Harlow: Pearson | Risk management and iterative process critique |
| [E5] | OWASP (2026) *Mobile Application Security Verification Standard* | Security gap assessment (NFR4) |
| [E6] | Nielsen, J. (2012) *Usability 101* | Framing the usability evidence that is still missing |

---

## Appendix index

### Appendix A — Peer-review questionnaire
Full item text, anchors and live form URL. Source: `ASM/ASM/PEER_REVIEW_FORM.md` `[VERIFIED]`. Reproduction: paste the 16-item table from §2.2.

### Appendix B — Participants and raw responses
`[PENDING: no participants recruited and no responses received. Do not insert sample or illustrative answers.]`
Required content once available: participant table (name, role, description, date, mode, consent) and one row per participant per item with verbatim text.

### Appendix C — Test evidence
C.1 JVM suite `[VERIFIED]`:
- Command: `& "AI Study\gradlew.bat" -p "AI Study" :app:testDebugUnitTest --offline --console=plain`
- Baseline: 12 retained XML files aggregate to **152 tests, 0 failures, 0 errors, 0 skipped**
- After the practice fix: **159 tests across 13 classes, 0 failures**, verified by 159 `@Test` annotations and the retained full-suite log `practice-fix-unit-tests.txt`
- The seven new cases are `PracticeAnswerTest` (letter key, numeric key, text key, trim/case, two ViewModel cases, display resolution); its focused XML records 7 tests, 0 failures
- Classes (13): the twelve baseline classes plus `PracticeAnswerTest`

C.2 Instrumented suite `[VERIFIED]`:
- Command: `gradlew.bat connectedDebugAndroidTest --no-daemon --console=plain`
- Device: `Pixel_6_API_35(AVD) - 15`
- Run 1 (pre-key): 39/39, `2026-11-09T12:25:40`, build 29 s
- Run 2 (post-key): 39/39, `2026-11-09T12:56:48`, build 50 s
- Run 3 (post-fix): 39/39, `2026-11-09T16:03:27`, time 12.305 `[real-final/tests-connected-result.xml]`
- Per suite (all runs): `ExampleInstrumentedTest` 1, `QuestionAnswerDaoTest` 11, `UserDaoTest` 5, `AuthRepositoryTest` 13, `MainShellSmokeTest` 9
- Retained: `connectedDebugAndroidTest-result.xml`, `connectedDebugAndroidTest-after-key-result.xml`, `connectedDebugAndroidTest-practice-fix-result.xml` and the matching `.txt` logs

C.3 Twenty-case evaluation matrix — **decision pending audit**:
Part 1's M1 test plan already defines a case catalogue (TC-AUTH, TC-ONB, TC-ASK, TC-ANS, TC-HIS, TC-QUI, TC-PRO, TC-GAM, TC-NOT, TC-SEC, TC-OFF, TC-PRV, TC-NFR), so a fresh 20-case list is **not** required. The appendix should instead select twenty of those cases that map to the FR/NFR rows in §9 and record expected versus actual results. `[PENDING: selection and execution]`

### Appendix D — Screenshots

The authoritative set is `ASM/ASM/evidence/pixel-api-35/real-final/` (16 images, 1080×2400), captured on `Pixel_6_API_35` with the practice-fix APK and real Gemini. It is indexed by `real-final/MANIFEST_REAL.txt`.

| File | Flow | Observed |
|---|---|---|
| `01-welcome.png` | Launch | Welcome, no simulation banner |
| `02-register-filled.png` | Registration | Form accepted local input |
| `03-onboarding-step1.png` | Onboarding 1/2 | Education level options |
| `04-onboarding-step2.png` | Onboarding 2/2 | Explanation style options |
| `05-ask-input.png` | Ask input | Question typed |
| `06-ask-gemini-answer.png` | Real answer | Steps, saved to history; HTTP 200 |
| `07-history-real-question.png` | History | One saved real question |
| `08-question-detail.png` | Question detail | Full question and real answer |
| `09-progress-real.png` | Progress | 1 question, 15 s, 0%, +7 XP |
| `10-practice-generate.png` | Practice setup | Source and count controls |
| `11-practice-question.png` | Gemini quiz | Real multiple-choice question |
| `12-practice-correct-feedback.png` | Practice answer | Option B correct, “Chính xác!” |
| `13-profile.png` | Profile | Level 1, 22 XP, badge |
| `14-notifications.png` | Notifications | One unread item |
| `15-logout-confirm.png` | Logout | Confirmation dialog |
| `16-logged-out-welcome.png` | Logout result | Returned to Welcome |

**Root files 01–22 are DEMO / NON-FINAL.** Root 02–19 used `FakeAiClient`; root 20–22 were real-key captures that predate the finished build. They are retained, not deleted, and must not be captioned as final.

**Coverage gaps in the final set** — do not claim these visually:

- Loading state during an AI call (NFR1).
- History search, bookmark filter and subject filter.
- Notification permission or OS-level delivery.
- A rendered mathematical formula; the observed answers are plain text.

**Supplementary captures (not final evidence).** `real-final/optional/` holds image-question (`image-01-attached`, `image-02-answer`), offline error and recovery (`offline-01-error`, `offline-02-recovered`), and login after logout (`login-01-screen`, `login-02-success`, `login-03-profile`). They are functional observations and must be captioned “supplementary”, never substituted for the sixteen final images.

An earlier set of eleven images in `docs/screenshots/` came from a `Medium_Phone` AVD on Android 16 (API 37). They may be used only if every caption names that device; they must not be blended into the Pixel set.

### Appendix E — Repository and toolchain evidence

| ID | Evidence | Value | Status |
|---|---|---|---|
| E-01 | Remotes | `origin`: `github.com/mkhang-ship-it/AndroidStudio`; second remote `target`: `github.com/QuyTamIT/AIStudy.git` | `[VERIFIED: git remote -v]` |
| E-02 | Branch and head | `mkhang-ship-it/AUDIT` @ `79c523a` | `[VERIFIED]` |
| E-03 | Commits on branch | 13, 2026-09-30 → 2026-11-02 | `[VERIFIED]` |
| E-04 | Commits across all refs | 36 | `[VERIFIED]` |
| E-05 | Authors | mkhangsip (25), Le Quy Tam (9), NCnguyenn (1), trungvinh-52hz (1) | `[VERIFIED: shortlog --all]` |
| E-06 | CI workflow | `.github/workflows/android-test.yml`, `./gradlew test` | `[VERIFIED]` |
| E-07 | Toolchain versions | AGP 9.3.3, Kotlin 2.2.10, Compose BOM 2026.02.01, Room 2.7.2, KSP 2.3.12 | `[VERIFIED]` |
| E-08 | SDK levels | compile/target 37, min 33, versionName 1.0 | `[VERIFIED]` |
| E-09 | APK | 35,596,239 bytes, debug | `[VERIFIED]` |
| E-10 | UML | 13 `.puml` sources | `[VERIFIED]` |
| E-11 | Schema | version 1, 7 tables | `[VERIFIED]` |
| E-12 | Tags | none | `[VERIFIED: git tag]` |
| E-13 | Merge commits | 6 across all refs; none on the audited branch | `[VERIFIED]` |
| E-14 | Pull requests | cannot be confirmed from a local clone | `[PENDING: check the GitHub remote]` |
| E-15 | Commit graph screenshot | — | `[PENDING]` |
| E-16 | CI run page screenshot | — | `[PENDING]` |

### Appendix F — Measurement log

`ASM/ASM/NFR_MEASUREMENTS.md` records two measured observations on the fixed APK (`c7c8f9e2…`), Pixel_6_API_35, 2026-11-10 00:51 +07:00. No NFR threshold or SLO is defined, so these are observations, not compliance claims.

| ID | Metric | Method | Status |
|---|---|---|---|
| M-01 | Cold start to Ask screen | `adb shell am start -W`, five runs | **MEASURED** — 5139, 3284, 3108, 3310, 3041 ms; min 3041, median 3284, max 5139, mean 3576.4 |
| M-02 | AI round trip | `adb logcat` POST → HTTP 200 timestamps, three questions | **MEASURED** — 2207, 913, 861 ms; min 861, median 913, max 2207, mean 1327.0; 3/3 HTTP 200 |
| M-03 | History open time with N rows | Seeded dataset, timed | `[PENDING]` |
| M-04 | Actions to submit a question | Manual count against NFR2 | `[PENDING]` |
| M-05 | Submit path at 2,000 / 2,001 characters | Manual boundary check | `[PENDING]` |
| M-06 | Offline behaviour | Airplane mode, ask a new question, record the message verbatim | **OBSERVED (no timing)** — explicit error; retry after reconnect returned HTTP 200 |
| M-07 | Lockout persistence | Five failures, kill app, reopen | `[PENDING]` |
| M-08 | MCQ scoring with a real key | Answer a known-correct option, record outcome | **VERIFIED** — `12-practice-correct-feedback.png` “Chính xác!”; seven `PracticeAnswerTest` cases |
| M-09 | Image round trip | Ask with an image, reopen the saved item | **SUPPLEMENTARY** — `optional/image-01-attached`, `image-02-answer`; real Gemini answer observed |

---

## Pre-submission honesty checks

Run this list against the final PDF:

1. No participant name, role, score or comment appears unless it came from a real respondent.
2. No measurement appears without a device, a method and a date.
3. No screenshot is captioned with a device other than the one it came from.
4. Only the sixteen `real-final/` images are used as final runtime evidence; root 01–22 are labelled DEMO / NON-FINAL wherever they appear.
5. Any "39/39" or "39 tests" statement cites the retained JUnit XML (all runs) and the `Pixel_6_API_35` device.
6. The test count quoted is 159, not 152, 143, 76 or 120.
7. No claim is made about the factual accuracy of AI-generated answers.
8. No claim is made that all user data is encrypted.
9. Every "Met" row in §9 points at a test result or an observed final run, not a demo flow.
10. Feedback discussed in D2 is traceable to a participant ID and a date.
11. The API key does not appear in any artefact, screenshot or log excerpt.
