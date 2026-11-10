# ASM2 — Report Source (Working Deliverable)

**Status:** single working report source, English only. Generate DOCX/PDF from this file when the inputs in `ASM2_REPORT_INPUTS.md` are supplied. The full draft (`ASM2_REPORT_DRAFT.md`) remains the evidence pack.
**Unit 22: Application Development — Assignment Part 2**
**Application:** AI Study Mentor (`AI Study/`, commit `79c523a`)
**Evidence gate:** final runtime evidence is the real-final set, `ASM/ASM/evidence/pixel-api-35/real-final/01–16`, captured on the fixed build with real Gemini. Root files 01–22 are **DEMO / NON-FINAL**; retained, not deleted.
**Formatting to apply in Word:** Calibri 12 pt, 1.5 line spacing, page numbers, margins left 1.25 cm and right/top/bottom 1 cm; Harvard citations and reference list; front sheet 2 as page 1.
**Word target:** 2,500–3,000 words excluding tables, figures, appendices and references. Measured body: 2,936 words (tables, figures, references and appendices excluded).

## Front sheet metadata

The required cover page is the supplied template `FRONTSHEET ASM FINAL REPORT (GROUP).docx`; it is inserted as page 1 of the DOCX, not reproduced here. The table below must be copied into it exactly.

| Field | Value |
|---|---|
| Qualification | Pearson BTEC Level 5 Higher National Diploma in Computing |
| Unit number and title | Unit 22: Application Development |
| Assignment | Assignment Part 2 — Evaluate the performance of a business application against its software design document after plan and produce a functional business application |
| Student name | Nguyen Minh Khang |
| Student ID | BC00720 |
| Class | SE08201 |
| Assessor name | Mr. Le Nhat Quang |
| Group number | N/A — individual submission |
| Group members and codes | N/A — individual submission |
| Submission date | `[BLANK: set by the centre]` |
| Final scores | `[PENDING: assessor entry]` |
| Student signature / date | `[PENDING]` |
| Assessor signature / date | `[PENDING]` |
| Word count (body) | 2,936, excluding tables, figures, appendices and references |

Evidence tags are kept only where a statement is not yet supportable. `[PENDING]` marks work that no artefact covers; it must not be converted into a claim.

---

## 1. Introduction

This report concludes the BrightPath Learning project begun in Assignment Part 1. Part 1 defined the problem, the target users, ten functional requirements, six non-functional requirements, a layered architecture and a fourteen-week Scrum plan. Part 2 reports the peer review of that plan (P4, M3), the development of the application (P5, M4), its evaluation against the initial requirements (P6), a critical review of the process (M5) and the decisions taken on feedback (D2).

The application is a local-first Android app that lets a student ask a question by text or image, receive a structured AI explanation, keep it, practise with generated questions and track progress. It is evaluated at commit `79c523a`, with one post-freeze fix to practice scoring applied and tested separately. Three evidence classes are kept separate: automated tests (159 JVM tests, 13 classes; 39 instrumented tests, green after the fix), code inspection, and device behaviour. Final runtime evidence is the sixteen-image real-final set captured with real Gemini on the fixed build; earlier root screenshots are demo material and are not cited as final. Survey responses, participant data, front-sheet date, scores and signatures, the presentation rehearsal and the final reference list remain `[PENDING]`.

---

## 2. Activity 1 — Peer Review (P4, M3)

### 2.1 Approach

The review covered four questions: whether the problem definition is clear, whether the target users are correctly identified, whether the requirements match students' needs, and whether the proposed solution and fourteen-week strategy are feasible for a small junior team. A structured questionnaire was chosen because it is asynchronous, comparable across items and quotable in the appendix.

The instrument has sixteen required fields: four participant fields (name, role, job or study background, review context) and twelve review items. Nine are five-point Likert items using one set of anchors, `1 = Strongly disagree` to `5 = Strongly agree`, so that responses can be reported item by item instead of being collapsed into a single score. Three are open items covering missing requirements, weaknesses and risks, and suggested improvements.

### 2.2 Instrument

Table 1: Review instrument, sixteen required fields (Q1–Q9 Likert, Q10–Q12 open).

| Item | Focus | Wording (short) |
|---|---|---|
| Q1 | Problem definition | The learning problems are clearly defined |
| Q2 | Target users | Middle school, high school and university students are clearly identified |
| Q3 | Requirements | The requirements reflect students' actual learning needs |
| Q4 | Solution fit | The application suits the identified learning problems |
| Q5 | Solution value | The AI question-and-answer feature is useful |
| Q6 | Solution value | History and progress tracking are useful |
| Q7 | Strategy | The development tools are appropriate |
| Q8 | Strategy | The methodology suits the project |
| Q9 | Strategy | The plan is realistic in fourteen weeks and on budget |
| Q10 | Requirements | What requirements or learning problems are missing? |
| Q11 | Solution and strategy | What weaknesses or risks do you identify? |
| Q12 | Improvement | What improvements would you suggest? |

The form exists as an unpublished Google Form draft and contains no responses. It must not be published or filled with synthetic answers.

### 2.3 Participants and responses

Participant names, roles, job descriptions, dates and consent records are `[PENDING]`. The minimum defensible sample is three reviewers, including at least one student outside the development team and one person with professional development experience. Invitations sent, responses received and valid responses must be reported as three separate numbers `[PENDING]`.

### 2.4 Analysis plan

Responses will be summarised per item using median and range, with no percentages below five respondents. Open answers will be quoted verbatim against a participant identifier and grouped into themes with respondent counts. Each weakness will cite a quoted response, its impact and a priority.

### 2.5 Opportunities not previously considered (M3)

This section is `[PENDING]` because it depends on the responses. Two outcomes are acceptable: a requirement or risk that Part 1 did not contain, with an explanation of why it was missed; or an explicit statement that the review surfaced no genuinely new opportunity, together with the limits of a three-person sample. The review is not permitted to be written backwards from a desired conclusion.

### 2.6 Limitation

The instrument measures opinion about a design document, not the running application; reviewers do not see the APK, so usability findings are predictions. Peer opinion is therefore separated from device evidence.

---

## 3. Activity 2 — Development (P5, M4)

### 3.1 Baseline inherited from Part 1

Part 1 fixed twelve architecture decisions and a MoSCoW cut: FR1–FR4, FR6 and FR8 were the minimum viable product, with FR5, FR7, FR9 and FR10 following the core loop. The decisions that shaped the build most were Jetpack Compose with a single activity, Room as the local source of truth, `HttpURLConnection` with `org.json` instead of a networking library, PBKDF2 password hashing with a per-user salt, encrypted session storage, AES-GCM encryption for attached images, and a caching layer that reuses an answer when a new question is an exact or close match. The build inherits the four-layer, single-module structure: `ui`, `domain`, `data` and `platform`, with a manual dependency graph in `ServiceGraph.kt`.

Figure 1: four-layer architecture — `docs/uml/png/06_component.png`
Figure 2: ask and cache flow — `docs/uml/png/04_activity_ask_flow.png`
Figure 3: entity relationship diagram — `docs/uml/png/09_erd.png`

### 3.2 Scope and delivery

Table 2: Requirement scope and delivery status at `79c523a`.

| Area | MVP | Status at `79c523a` |
|---|---|---|
| Registration, login, logout, lockout | Yes | Register and logout observed in real-final; 13 instrumented auth cases pass |
| Ask by text | Yes | Real Gemini answer observed in real-final |
| Ask by image | Yes | Partial: image stored and transmitted, never redisplayed |
| Structured AI answer | Yes | Real Gemini response with steps observed |
| History, search, bookmark, filter | Yes | List and detail observed in real-final; search and filters not exercised |
| Progress summary | Yes | Observed in real-final |
| Onboarding subjects | Yes | Not implemented: subjects submitted empty |
| Improvement content (FR5) | Post-MVP | Implemented; generated blocks not exercised |
| Practice (FR7) | Post-MVP | Gemini quiz and corrected “Chính xác!” feedback observed; scoring fix covered by 7 unit tests |
| Gamification (FR9) | Post-MVP | Partial: XP, level, streak and badge observed; no leaderboard |
| Notifications (FR10) | Post-MVP | Partial: list observed; reminder generation unverified |

The delivered codebase is 98 Kotlin files and 12,103 lines in `app/src/main`. The database is Room schema version 1 with seven tables: `users`, `profiles`, `question_answers`, `quiz_items`, `xp_events`, `user_badges` and `notifications`.

### 3.3 Process and methodology

Development followed one-week increments with a Definition of Done per phase and a rule that every logic change ships with a test. The rule is visible: thirteen JVM test classes and a ban on calling the real provider from tests, with a hand-written fake client.

The record is weaker than the process. The sprint log contains fourteen weeks; four entries are complete and ten are empty. The audited branch carries thirteen commits between 30 September and 2 November 2026 by a single author, in a straight line. The wider repository holds thirty-six commits from four authors and six merges, but the delivered code is a consolidated snapshot that cannot be attributed commit by commit. There are no tags, and pull requests cannot be confirmed from a local clone `[PENDING: check the GitHub remote]`. Continuous integration runs the JVM suite with no API key present, so it builds against the offline fake client.

### 3.4 Architecture and data flow

The ask path is where the business constraints meet. The question text is normalised and hashed; an exact hash match is served from the database; otherwise a similarity policy compares the new question with stored candidates; only if both checks miss does the application call the provider. A cache hit therefore costs no API call, which is the cost-control mechanism the brief requires. The similarity policy is guarded so that a difference in a number or a programming-language name forces a fresh call rather than a reuse.

Thirteen PlantUML sources are rendered from text, so the diagrams are reviewable as diffs.

### 3.5 Runtime verification

Three sets exist: the demo set (root 02–19, fake client) and the pre-final real-key set (root 20–22) are **DEMO / NON-FINAL**; `real-final/01–16` is authoritative — fixed APK, real Gemini, every screen observed before capture.

The build is the practice-fix APK (`c7c8f9e2…`), real key configured, no simulation banner. The real-final flow covers welcome, registration, onboarding, a real question, history, detail, progress, Gemini practice, corrected feedback, profile, notifications and logout.

The JVM suite is 159 tests across 13 classes, zero failures, including seven new `PracticeAnswerTest` cases. The connected suite passed 39/39 after the fix (JUnit XML `tests="39" failures="0" errors="0" skipped="0"`, timestamp 2026-11-09T16:03:27). Both the ask call and the quiz call are logged as `POST …:generateContent` with `HTTP 200` and `modelVersion gemini-3.5-flash-lite`; the ask returned 592 tokens.

The fix resolves a stored letter `A`–`D` or number `1`–`4` to the matching option text before comparison. Screenshot 12 shows the correct-option tick and “Chính xác!” (“Correct!”); the model stored option text there, so the letter-key path rests on the seven unit tests.

Figure 4: welcome on the fixed real-key build — `ASM/ASM/evidence/pixel-api-35/real-final/01-welcome.png`
Figure 5: real Gemini answer and saved-to-history confirmation — `ASM/ASM/evidence/pixel-api-35/real-final/06-ask-gemini-answer.png`
Figure 6: corrected practice feedback — `ASM/ASM/evidence/pixel-api-35/real-final/12-practice-correct-feedback.png`

Limitations: OS-level push permission was not exercised; the quiz screenshot uses a text answer key while the letter-key path rests on unit tests; and NFR2 (action count) is still unmeasured. Supplementary captures for the image question, offline error and recovery, and login after logout are in `real-final/optional/`; they are supplementary and do not replace the sixteen-image gate.

### 3.6 Toolchain evidence

Table 3: Toolchain versions and roles, verified from the build files.

| Tool | Version or role |
|---|---|
| Android Gradle Plugin | 9.3.3 |
| Kotlin | 2.2.10 |
| KSP | 2.3.12 |
| Compose BOM | 2026.02.01 |
| Room | 2.7.2 |
| Navigation Compose | 2.10.2 |
| Coroutines test | 1.10.2 |
| SDK levels | compile 37, target 37, min 33 |
| CI | GitHub Actions, `./gradlew test` |
| Diagrams | PlantUML, 13 sources |

M4 asks for evidence that the tools were used, not merely selected. The strongest artefact is the test suite; the weakest is the sprint record, which is incomplete and is reported as such.

### 3.7 Difficulties and fixes

Table 4: Development difficulties and their recorded resolutions.

| Difficulty | Resolution |
|---|---|
| No API key blocked end-to-end testing | Hand-written fake client with fixture responses; app remains usable offline |
| Provider returned LaTeX escapes that broke JSON parsing | Repair pass before parsing and repair of stored strings |
| Cache returned an answer for a different question sharing most words | Token normalisation and a key-token guard |
| Login lockout was held in memory and lost on restart | Persisted throttle in shared preferences |
| Review-time tracking lost writes | Lifecycle-aware accrual only while the app is resumed |
| Deleting a merged history row left duplicates visible | Delete by user and question hash |

The defect register records twenty-two entries, eighteen fixed and four open at the freeze: English-only mistake-alert keywords, no global leaderboard, two-factor authentication without a verification flow, and no two-way synchronisation.

### 3.8 Support documentation

Build and test instructions, contribution rules, the design document, the test plan, thirteen UML sources, a user guide (`USER_GUIDE.md`) and a debug APK (fixed build, 35,596,239 bytes) are present. A demonstration-account procedure remains `[PENDING]`.

---

## 4. Evaluation Against the Problem Definition and Initial Requirements (P6)

### 4.1 Method

A requirement is met only when a test or observed run demonstrates it; code alone is implemented but unverified, and missing behaviour is partial. Evidence: 159 JVM tests across 13 classes, 39 instrumented tests green after the fix, and the sixteen-screen real-final session. Tables 5 and 6 record the status.

### 4.2 The problem definition

Part 1 framed the problem as a student working alone who cannot obtain a level-appropriate explanation, loses it after the session and cannot tell whether they are improving. The real-final session demonstrates the whole loop against Gemini: a question is entered, answered with steps, saved, reopened from history, and reflected in progress; practice generates a quiz from that question and marks the correct option correctly. The capability is therefore evidenced end to end. The claim that it reduces study friction remains a hypothesis, because no student has used the build under observation.

### 4.3 Functional requirements

Table 5: Functional requirement status; the runtime basis is the real-final set.

| Requirement | Status | Basis |
|---|---|---|
| FR1 Authentication | Met | Register, logout and login-after-logout observed; 13 instrumented auth cases pass |
| FR2 Onboarding | Partial | Completes on device; subjects submitted empty |
| FR3 Text and image questions | Partial | Real text answer observed; image question captured as supplementary evidence; image never redisplayed |
| FR4 Structured answer | Met (one response) | Real Gemini response with three steps and a final answer |
| FR5 Improvement content | Implemented, unverified | Detail screen present; generated blocks not exercised |
| FR6 History and library | Met for list and detail | Saved question and answer reopened; search, bookmark and filters not exercised |
| FR7 Practice | Met for generation and feedback | Gemini quiz generated; correct option marked “Chính xác!”; letter-key path covered by 7 unit tests |
| FR8 Progress | Met | Totals, time, accuracy and XP observed after real activity |
| FR9 Gamification | Partial | XP, level, streak and badge observed; no leaderboard |
| FR10 Notifications | Partial | List observed; reminder generation unverified |

Three results reflect design scope: FR5 and FR7 were scheduled after the core loop, and FR9's leaderboard requires a server. Two are genuine shortfalls: onboarding collects no subjects, and the image is stored but never displayed again.

### 4.4 Non-functional requirements

Table 6: Non-functional requirement status. Measured rows are observations on the emulator; no NFR threshold or SLO is defined in the project.

| Requirement | Status | Basis |
|---|---|---|
| NFR1 Responsiveness | Measured (observation) | Cold start n=5: 3041, 3108, 3284, 3310, 5139 ms (median 3284, max 5139); Gemini ask round trip n=3: 861, 913, 2207 ms (median 913), 3/3 HTTP 200 |
| NFR2 Three actions to submit | Not tested | Action count not measured `[PENDING]` |
| NFR3 Android 13 or later | Met | `minSdk` 33; executed on API 35 |
| NFR4 Encryption and minimisation | Partial | Credentials, session and images protected; database body plaintext; abuse filter never called |
| NFR5 Offline reading | Partial | Room reads saved data offline; a new question offline shows an explicit connection error and succeeds after reconnect; there is no queue or connectivity observer |
| NFR6 Password policy, 2FA, lockout | Partial | Strength and lockout pass; two-factor authentication is schema-only |

### 4.5 Quality and gaps

The interface is coherent and the whole loop completes against the real provider. Two residual risks remain. First, the real session returned `subject: other` for a question the UI labelled “Toán học”, an observed subject-mapping defect. Second, answer quality cannot be judged from one response and is not claimed. The practice scoring defect is fixed and covered by seven regression tests; screenshot 12 shows the corrected feedback.

Nine gaps remain evidenced: onboarding captures no subjects; images are never redisplayed or deleted; the abuse filter is never called; there is no offline queue; two-factor authentication has no verification; there is no leaderboard; database body columns are unencrypted; ten sprint entries are empty; and the documentation quotes three test counts, of which the measured 159 is authoritative.

---

## 5. Activity 3 — Critical Review (M5)

### 5.1 Systems investigation

The investigation contributed two decisions that visibly shaped the build: local-first storage and treating AI cost as a constraint. Removing the backend also made offline reading almost free, because Room is the only source of truth. The trade-off was stated at the time — no synchronisation, no cross-device leaderboard, no server-side validation — and those are exactly the open items now.

Treating cost as a constraint produced the cache and its similarity guard, and the guard matters more than the cache: an early version reused an answer for a different question that shared most of its words, and the fix introduced token normalisation and a key-token guard. Investigating cost therefore exposed a correctness risk that the functional requirements alone would not have surfaced. The investigation fell short in one respect: it produced no usability baseline, so requirement priorities rest on the brief and on reasoning rather than on observed student behaviour.

### 5.2 Design

The four-layer split, with a domain tier that does not import Android infrastructure, was the single most valuable decision because it allows the rules to be tested without an emulator. Three design weaknesses are visible in the delivered code. The onboarding design and the implementation disagree: level and style are collected, subjects are not. Search was designed as a first-class feature but implemented as a substring filter, which the design document discloses as a trade-off. And one sentence over-promised security: question and answer text are not encrypted in the database, although credentials, the session and image files are protected. The correct framing is protection of the most sensitive material, not all user data.

A design-stage assumption also failed to hold: a single provider was treated as operationally reliable. Typed errors, retry with backoff and a timeout address availability, but nothing addresses content accuracy, which remains unmeasured.

### 5.3 Development

Tooling fit was good: Compose, Room, manual injection and a minimal HTTP stack produce a build that needs no network to compile. Methodology fit was partial: increments and Definition of Done worked, the record did not. Ten empty sprint entries and a single-author consolidated branch mean the collaboration trail M4 asks for is thin, and two one-line defects reached the freeze that a second reader would likely have caught; one was subsequently fixed, the other remains. The honest reading is that the team practised test-gated commits and phase-level increments, and did not maintain sprint records contemporaneously.

### 5.4 Testing

The JVM suite is the strongest evidence: 159 tests across thirteen classes, covering similarity thresholds, XP boundaries, LaTeX repair, JSON parsing, insight generation, the abuse filter and the practice answer key. The instrumented suite has 39-test runs that are green after the fix on API 35.

The weaknesses are structural. The suite is heavy on pure functions and light on integration; the smoke test asserts destination metadata rather than user journeys; there is no Compose behaviour test for the ask-to-answer path, no image-lifecycle test, and no accuracy, privacy or offline test. The practice grading defect is the proof of that gap: it reached the freeze because no UI test covered the flow, and the fix is guarded only by unit tests. All device runs are emulator-only, with no physical device.

### 5.5 Risks and quality attributes

The registered risks that materialised are provider outage, privacy scope, schedule control and review discipline. Cost overrun is mitigated by the cache but not bounded by measured daily spend. The highest unresolved risk is hallucinated content, which no mechanism addresses. A risk that was not registered is dependence on a single provider whose response schema changed during development, which is the kind of churn a contract test would bound.

Reliability improved through typed errors, retries and green suites; the real-provider path is verified for both an ask and a generated quiz, though the sample is small. Usability improved through structured answers, corrected practice feedback and offline insights, yet no usability session has been run. Security is defensible for credentials, sessions and images, and incomplete for stored text, abuse filtering and two-factor authentication. Effectiveness can now be tested, because the loop completes end to end; there is still no evidence that it improves learning outcomes.

---

## 6. Feedback Decisions and Future Improvements (D2)

### 6.1 Peer-review feedback

Peer feedback decisions are `[PENDING]` until responses exist. The decision table is fixed in advance: each feedback item records the verbatim comment and participant identifier, the decision (adopt, modify, defer, reject), the affected module, the commit and test, and a justification. "Planned" is not "done", and every deferral needs a condition for revisiting.

### 6.2 Improvements adopted

Table 7: Improvements adopted on the team's own evidence.

| Change | Problem | Evidence |
|---|---|---|
| Markdown and LaTeX rendering | Answers displayed raw markup | Renderer classes; tests in the JVM suite |
| Persistent login lockout | Lockout lost on restart | Throttle class; case passed in the 39-test run |
| Token metering | Cost could not be measured | Provider client reads usage metadata |
| Offline progress insights | Progress was numbers only | Insight rules; dedicated test class |
| Hash-based history deletion | Duplicate rows survived deletion | DAO deletion case |
| Key handling outside version control | Key could be lost or committed | Properties lookup with build config field |
| Practice answer-key normalisation | A correct option could be marked wrong when the key was a letter | `PracticeViewModel` normalisation; 7 new `PracticeAnswerTest` cases; fixed build SHA `c7c8f9e2…` |

Each improvement was adopted on the team's own evidence and has a test or code reference; D2 asks for justified change, not only peer-driven change.

### 6.3 Not acted upon

Table 8: Scope decisions and known defects deliberately not acted upon at the freeze.

| Item | Reason | Revisit condition |
|---|---|---|
| Global or friends leaderboard | Needs a server, score validation and anti-cheat | When a backend exists |
| True two-factor authentication | Needs enrolment and login enforcement; schema alone is misleading | Before real student data is collected |
| Two-way synchronisation | Needs a conflict policy and an outbox | When multi-device use is required |
| Streaming answers | The provider returns one JSON object with several fields | When latency becomes the dominant complaint |

No peer feedback has been rejected because none was received; this table records scope decisions, and the registers must not be merged.

### 6.4 Opportunities

Evidence-based opportunities, in priority order: complete two-factor verification, because a schema-only feature implies protection that does not exist; display attached images and manage their lifecycle; add an offline queue; activate the abuse filter; and only then consider a server-backed leaderboard and streaming. The leaderboard is an unmet Part 1 requirement, not a new idea.

### 6.5 Evaluation of the strategy

The most valuable strategy was making the reading experience correct before lengthening the feature list: rendering and JSON repair turned a working answer into a usable one, and the tests make the fix durable. The least effective was late consolidation, which let two one-line defects reach the freeze; one was caught and fixed after review, the other is still open. The chosen trade-off — breadth of features over depth of runtime verification — is defensible for coursework but explains why P6 still carries partially verified rows. Naming it is stronger than overclaiming coverage.

---

## 7. Conclusion

AI Study Mentor delivers the core loop the scenario required: ask, understand, keep, revisit and see progress. The Part 1 decisions on local-first storage, a testable domain tier and cost-aware caching held up in implementation and are visible in the code, the schema and the tests. The evidence supports a precise claim: 159 JVM tests and 39 instrumented tests pass, and sixteen final screenshots document the loop against real Gemini on Android 15.

The claim stops there. Only one real answer and one real quiz were observed, so answer quality is not established; cold start (median 3284 ms) and Ask round trip (median 913 ms) are measured observations with no defined SLO, while action count and history timing are not measured; and four defects remain — empty onboarding subjects, image lifecycle, an inactive abuse filter and subject mapping. The practice scoring defect is fixed and tested.

---

## Assessment criteria traceability

Table 9: Assessment criteria coverage in this report.

| Criterion | Where addressed | Status |
|---|---|---|
| P4 Peer review | Section 2; Appendix A | Instrument ready; responses `[PENDING]` |
| M3 Interpret feedback | Sections 2.5, 6.1 | `[PENDING]` responses |
| P5 Functional application | Sections 3, 4; Appendix C | Met in artefact; full real-final loop verified; user guide supplied, demo account `[PENDING]` |
| M4 Tools and methodology | Sections 3.3, 3.6 | Tools evidenced; sprint gaps disclosed |
| P6 Evaluation | Section 4 | Partial; cold start and Ask round trip measured, action count pending |
| M5 Critical review | Section 5 | Complete |
| D2 Feedback and improvements | Section 6 | Self-evidenced improvements complete; peer feedback `[PENDING]` |

## References

`[PENDING: final citation check. The working list is ASM2_HARVARD_REFERENCES.md — 17 external sources from Part 1 plus 13 project documents; URLs, access dates and editions still require verification.]`

## Appendix index

| Appendix | Content | Source |
|---|---|---|
| A | Full questionnaire, 16 fields with anchors and form link | `PEER_REVIEW_FORM.md`; draft §2.2 |
| B | Participant table and raw responses | `[PENDING]` |
| C | Test evidence: 159 JVM tests (13 classes), 39 instrumented tests after the fix, JUnit XML | Runtime evidence folder; draft Appendix C |
| D | 16 final images in `real-final/`; optional image/offline/login captures in `real-final/optional/` are supplementary; root 01–22 DEMO/NON-FINAL | `ASM/ASM/evidence/pixel-api-35/real-final/` |
| E | Repository and toolchain evidence, commit facts | Draft Appendix E |
| F | Measurement log: cold start and Ask round trip measured; action count, history and offline timing pending | `ASM/ASM/NFR_MEASUREMENTS.md` |

## Remaining placeholders in this source

| Item | Status |
|---|---|
| Peer-review participants, responses and analysis | `[PENDING]` |
| Front sheet: submission date, scores and signatures; student, class and assessor supplied; group/member fields N/A — individual submission | `[PENDING]` |
| NFR1 UI-response timing, NFR2 action count and the remaining Appendix F rows | `[PENDING]` |
| Presentation rehearsal and live demo (12-slide DRAFT deck and PDF exist) | `[PENDING]` |
| Reference list | `[PENDING]` |
| User guide | Supplied: `ASM/ASM/USER_GUIDE.md` |
| Demo-account procedure | `[PENDING]` |
| Pull-request history on the remote | `[PENDING]` |
| Login-after-logout capture | Supplementary: `real-final/optional/login-01…03`; not part of the 16-image gate |
| Image/camera question flow | Supplementary: `real-final/optional/image-01…02`; image redisplay still missing |
| OS-level notification permission | `[PENDING]` |
| Subject-mapping fix (model returned `other`, UI showed “Toán học”) | `[PENDING]` |
| Answer-quality or accuracy study | `[PENDING]` |
