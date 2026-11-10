# ASM2 — Submission Checklist

**Companion to:** `ASM2_REPORT_DRAFT.md`, `ASM2_AUDIT_PLAN.md`, `ASM2_EVIDENCE_CHECKLIST.md`
**Purpose:** the single list of what must be true before the report is exported and uploaded.
**Scope of this phase:** DRAFT DOCX/PDF and a 12-slide DRAFT PPTX/PDF exist in `ASM/ASM/deliverables/draft/`; final submission is still gated on the pending inputs.
**Last updated:** 2026-11-09.

**How to use:** an item is ticked only when the artefact exists at a path that can be opened, or a command output has been captured. "Looks fine in the draft" is not evidence.

---

## 0. Current state at a glance

| Area | State | Notes |
|---|---|---|
| Report source | `ASM/ASM/ASM2_REPORT_COMPACT.md` exists | 2,936-word body; full draft retained as evidence pack |
| Draft form | Complete, unpublished, 16 required items | No responses |
| Peer-review data | **Missing** | Blocks P4, M3 and D2 |
| JVM test evidence | **159 tests, 0 failures** (13 classes) | 152 were the baseline; 7 new `PracticeAnswerTest` cases; full-suite log retained |
| Instrumented test evidence | **39 tests, 0 failures, after the fix** | Pre-key, post-key and post-fix XMLs retained |
| Pixel API 35 screenshots | **16 FINAL in `real-final/`; 22 root files DEMO** | Fixed APK `c7c8f9e2…`, real Gemini |
| Real Gemini run | **Verified** | Ask and quiz calls, HTTP 200; logcat in `real-final/` |
| Quiz scoring fix | **Delivered and tested** | Screenshot 12 shows “Chính xác!”; letter-key path unit-tested |
| Support documentation (user guide, demo account) | **Partial** | `USER_GUIDE.md` supplied; demo-account procedure `[PENDING]` |
| NFR measurements | **Record only** | Cold start (n=5, median 3284 ms) and Ask round trip (n=3, median 913 ms, 3/3 HTTP 200) measured in `NFR_MEASUREMENTS.md`; action count, history and offline timing pending |
| Front sheet | **Partial** | Student, ID, class and assessor supplied; group/member fields N/A — individual submission; date, scores and signatures `[PENDING]` |
| DOCX / PDF | **Draft exported** | `deliverables/draft/` — final export gated on pending inputs |

---

## 1. Formatting (brief-mandated)

Source: `Assignment part 2.docx`, Submission Format. `[VERIFIED]`

- [ ] Calibri, size 12, applied to the whole body including tables (tables may use 10–11 pt only if the assessor allows; default to 12)
- [ ] Line spacing 1.5 throughout the body
- [ ] Page numbers on every page except the front sheet
- [ ] Margins: left 1.25 cm; right 1 cm; top 1 cm; bottom 1 cm
- [ ] Headings styled with Word Heading levels so the table of contents can be generated
- [ ] Table of contents generated and updated (`References > Update Table`)
- [ ] List of tables and list of figures generated, or explicitly dropped
- [ ] Figure captions numbered continuously (`Figure 1`, `Figure 2`, …) and referenced in the text
- [ ] Table captions numbered continuously (`Table 1`, …) and referenced in the text
- [ ] Code, file paths and identifiers in a monospaced font (Consolas or Courier New) without changing body size
- [ ] British spelling used consistently (`behaviour`, `organisation`, `analyse`, `prioritise`)
- [ ] Word count recorded and stated in the introduction, with the limit used (`DEC-01`)

**Open decision DEC-01 — word limit**

| Source | Limit |
|---|---|
| Submission Format | 2,000–2,500 words (±10%), excluding tables, diagrams, appendices, references |
| Activity 2 | 2,500–3,000 words for the development report |

- [ ] Tutor confirms which figure governs `[PENDING: ask before final edit]`
- [ ] Word count computed with tables and appendices excluded
- [ ] If the Activity 2 figure is used, the introduction states that the whole-report figure is reported separately

---

## 2. Front sheet (cover page)

Template: `C:\Users\ADMIN\orca\AndroidStudio\ASM\ASM\FRONTSHEET ASM FINAL REPORT (GROUP).docx` `[VERIFIED: exists]`

Supplied values (copy exactly): student **Nguyen Minh Khang**, ID **BC00720**, class **SE08201**, assessor **Mr. Le Nhat Quang**, unit **Unit 22: Application Development**, qualification **Pearson BTEC Level 5 HND in Computing**.

- [ ] Front sheet 2 is the first page and is unnumbered
- [x] Group number: N/A — individual submission
- [x] One row: Nguyen Minh Khang (BC00720); additional member rows N/A — individual submission
- [x] Class entered (SE08201)
- [x] Assessor name entered (Mr. Le Nhat Quang)
- [ ] Submission date entered `[BLANK: set by the centre]`
- [ ] Final scores `[PENDING]`
- [ ] Student declaration retained verbatim, including the AI-tool disclosure sentence
- [ ] Every member has signed and dated `[PENDING]`
- [x] Qualification and unit title match the template
- [ ] No member name appears anywhere in the report body without consent

`[PENDING: group allocation, member codes, submission date, scores, signatures]`

---

## 3. Peer-review package (P4)

Instrument: Google Form, draft, 16 required items.
URL: `https://docs.google.com/forms/d/16Hd08rqT475PtOu46Kmxi554dX3TwuaiSb9xiNZ_WmQ/edit` `[VERIFIED]`

- [ ] Form remains unpublished until reviewers are ready
- [ ] Minimum three reviewers recruited; at least one student outside the team and one person with development experience
- [ ] Participants briefed on purpose and on how their words will be used
- [ ] Consent recorded for quoting names and roles
- [ ] Responses collected with dates
- [ ] Raw responses transcribed verbatim into Appendix B, one row per participant per item
- [ ] Per-item median and range computed for Q1–Q9; no percentages if n < 5
- [ ] Q10–Q12 answers quoted verbatim, then themed; respondent counts per theme recorded
- [ ] Weakness table built only from cited responses
- [ ] M3 paragraph written: either a genuinely new opportunity, or an explicit statement that none emerged plus the sample's limits
- [ ] Report states invitations sent, responses received and valid responses as three separate numbers

**Do not:** invent a participant, a score or a quote. If only two reviewers respond, report two and reduce the claims.

---

## 4. Screenshots (P5, P6)

- [x] Target device: Pixel AVD on **API 35** (`Pixel_6_API_35`, Android 15) `[VERIFIED]`
- [x] Fixed APK `c7c8f9e2…` installed and exercised with real Gemini
- [x] Sixteen final captures exist in `real-final/`; root 01–22 are DEMO / NON-FINAL
- [x] Captions state: screen, requirement, device, API level, APK hash, provider mode and status
- [ ] Formula-rendering capture — **not available**, observed answers are plain text
- [ ] Offline capture — **not taken**
- [x] No API key, token or personal data visible in any capture
- [x] Existing 11 images in `docs/screenshots/` kept out of the Pixel set

- [ ] Figures 4–6 use `real-final/` files; root images only in Appendix D with a DEMO label
- [x] Image resolution sufficient for print (1080 px width)

`[VERIFIED: 16 final images in real-final/; optional image/offline/login captures in real-final/optional/ are supplementary. PENDING: formula, loading, OS push]`

---

## 5. Instrumented test and runtime evidence (P6)

- [x] Emulator attached and visible to `adb devices` as `emulator-5554`
- [x] `:app:connectedDebugAndroidTest` executed three times: pre-key, post-key, post-fix
- [x] All three XML reports retained
- [x] Each run is 39 tests, 0 failures, 0 errors, 0 skipped
- [x] Unit suite is 159 tests across 13 classes, 0 failures, including 7 new `PracticeAnswerTest` cases
- [x] Real Gemini verified for an ask and a generated quiz: HTTP 200, `gemini-3.5-flash-lite`
- [x] Practice scoring fix verified by tests and screenshot 12
- [x] Measurement log completed for the measured rows (Appendix F):
  - [x] M-01 cold start to Ask screen — 5139, 3284, 3108, 3310, 3041 ms
  - [x] M-02 AI round trip timing — 2207, 913, 861 ms; 3/3 HTTP 200
  - [ ] M-03 history open with a seeded dataset
  - [ ] M-04 actions to submit
  - [x] M-06 offline behaviour — explicit error, retry after reconnect succeeded; no queue
  - [ ] M-07 lockout persistence across a process restart
  - [x] M-08 MCQ scoring on the real-key build — screenshot 12 “Chính xác!”; 7 unit tests
  - [x] M-09 image round trip — supplementary `optional/image-01-attached`, `image-02-answer`; real Gemini answer observed
- [ ] Every measurement records method, device, date and the number of runs
- [ ] No measurement is copied from an earlier session without its source and date

`[VERIFIED: three instrumented runs, 159 unit tests, real ask and quiz, corrected feedback, cold start and Ask round trip. PENDING: action count, history open, offline timing]`

---

## 6. GitHub and toolchain evidence (M4)

- [ ] Remote URL confirmed: `github.com/mkhang-ship-it/AndroidStudio`
- [ ] Branch and head confirmed: `79c523a` `[VERIFIED]`
- [ ] Commit graph screenshot captured (`git log --graph --oneline --all`)
- [ ] One representative diff screenshot captured (for example `git show 0fefe4d --stat`)
- [ ] CI workflow file screenshot or text excerpt (`./gradlew test` on push/PR)
- [ ] A CI run page screenshot, if a run is publicly visible
- [ ] History facts recorded precisely: no tags; six merge commits across all refs and none on the audited branch; pull requests unconfirmed from the local clone `[VERIFIED / PENDING: check GitHub]`
- [ ] Toolchain table in the report matches `gradle/libs.versions.toml` `[VERIFIED: AGP 9.3.3, Kotlin 2.2.10, Compose BOM 2026.02.01, Room 2.7.2, KSP 2.3.12, Navigation 2.10.2, coroutines 1.10.2]`
- [ ] SDK levels match `app/build.gradle.kts` `[VERIFIED: compile/target 37, min 33, versionName 1.0]`
- [ ] Sprint-log gaps acknowledged: S04–S13 empty `[VERIFIED]`; any reconstruction labelled as reconstructed with a date

`[PENDING: graph, diff and CI run captures]`

---

## 7. Survey and response appendix

- [ ] Appendix A reproduces all 16 items verbatim with anchors
- [ ] Appendix B contains the participant table and the response record
- [ ] Each response row carries participant ID, item ID, verbatim text and date received
- [ ] Consent statement included
- [ ] Aggregation table shows median and range per Likert item
- [ ] No composite "satisfaction score" is invented
- [ ] If the sample is small, the appendix says so and the report does not generalise

`[PENDING: no responses exist]`

---

## 8. Support documentation (P5)

- [ ] README build and run instructions verified by following them on a clean checkout
- [ ] `local.properties` / `ai.properties` instructions reproduce a runnable build
- [x] User guide written: how to ask, read, save, practise, view progress, manage the profile (`ASM/ASM/USER_GUIDE.md`)
- [ ] Demo account procedure documented (how to create or sign in to a demonstration account) `[PENDING]`
- [ ] Known limitations section agreed with the report's §4.6
- [ ] Software package assembled: source archive, debug APK, README, user guide
- [ ] No API key, keystore, token or `.properties` file with secrets inside the archive
- [ ] Archive checked by listing its contents, not by trusting the compression step
- [ ] APK installs on the target device from the package `[VERIFIED in place: install and launch succeeded on Pixel_6_API_35; re-check from the final archive]`

`[PENDING: user guide and demo-account procedure not written]`

---

## 9. Presentation

- [ ] Slide deck produced (brief requires a presentation for Activity 2)
- [ ] Deck covers: problem, requirements, architecture, demo journey, evidence, limitations, future work
- [ ] Live demo path rehearsed on the same build and device used for screenshots
- [ ] Fallback prepared in case the device or network fails (recorded walkthrough or screenshots)
- [ ] Timing checked against the presentation slot
- [ ] Every team member has a speaking part
- [ ] Deck and notes stored in the repository or submission package

`[PARTIAL: 12-slide DRAFT PPTX/PDF exist; rehearsal, demo and timing checks remain PENDING]`

---

## 10. References

- [ ] Harvard in-text citations present for every external claim
- [ ] Reference list alphabetical, consistent, with access dates for online sources
- [ ] Only sources actually consulted are listed
- [ ] Internal project documents cited as project documentation, not as published literature
- [ ] Candidate list in `ASM2_REPORT_DRAFT.md` §10 verified item by item; editions and years corrected where needed
- [ ] No citation is used to support a claim the source does not make
- [ ] AI tools used during preparation are disclosed per the front-sheet declaration

`[PENDING: reference list not finalised]`

---

## 11. Integrity checks before export

**Fact integrity**

- [ ] The test count quoted is 159; the figures 152 (baseline), 143, 76 and 120 appear only where the conflict itself is discussed
- [ ] Every "Met" status in the traceability matrix points to a test result or an observed run
- [ ] "Implemented, unverified" is used wherever only code exists
- [ ] No claim about AI answer accuracy
- [ ] No claim that all user data is encrypted
- [ ] No claim of "39/39 instrumented tests pass" without a retained report
- [ ] No invented participant, response, measurement, timestamp or screenshot
- [ ] Every AI-related screenshot is labelled as `FakeAiClient` output; none is presented as a Gemini response
- [ ] Every screenshot caption states `Pixel_6_API_35`, Android 15, API 35 and commit `79c523a`
- [ ] No claim about latency, cache-hit ratio or model accuracy is made from the simulated run

**Evidence integrity**

- [ ] Every figure has a source file that can be reopened
- [ ] Every screenshot caption matches the device it came from
- [ ] Command outputs quoted in the report were captured in this project, with the command shown
- [ ] File paths cited in the report exist at the baseline commit

**Content integrity**

- [ ] P4 states the peer-review method and reports what was actually received
- [ ] M3 either presents a new opportunity or states honestly that none emerged
- [ ] M4 shows tool *activity*, not just a version table
- [ ] P6 distinguishes functional from non-functional verification
- [ ] M5 critiques design, development and testing, including risks
- [ ] D2 covers adopted feedback **and** feedback not acted upon, with reasons
- [ ] Limitations are stated where the brief expects evaluation, not buried in an appendix

**Confidentiality**

- [ ] API key absent from every file in the package
- [ ] Participant personal data used only with consent
- [ ] No unrelated personal data in screenshots or log excerpts

---

## 12. Export and upload (do not start yet)

This phase stops at the source draft. When the gates above are met:

- [ ] Export DOCX with the front sheet as page 1
- [ ] Export PDF from the DOCX, not from a different editor
- [ ] Spot-check the PDF: fonts embedded, page numbers present, tables not split awkwardly, figures not cropped
- [ ] Check the PDF page count and that the table of contents links or numbers are correct
- [ ] Verify the PDF opens on a second machine
- [ ] Upload to `http://cms.btec.edu.vn/`
- [ ] Confirm upload success and retain the receipt
- [ ] Keep the DOCX source in the repository for future revision

`[BLOCKED until sections 3, 5 (measurements), 6, 8, 9 and 10 are complete]`

---

## 13. Blocker register

| # | Blocker | Blocks | Owner |
|---|---|---|---|
| 1 | No peer reviewers recruited | P4, M3, D2, Appendices A–B | Group |
| 2 | Partial measurements: cold start and Ask round trip done; action count and history open pending | NFR2, M-03/M-04, Appendix F remainder | Tester |
| 3 | Remaining optional capture: OS push; login-after-logout, image and offline error/recovery are supplementary in `real-final/optional/` | Not blocking; stated as limitations | Group |
| 4 | Subject-mapping defect observed on a real response, unfixed | FR4 quality claim | Group |
| 5 | Word-limit conflict unresolved (`DEC-01`) | Whole document | Tutor |
| 6 | Sprint log S04–S13 empty | M4 strength | Group |
| 7 | Demo-account procedure missing (user guide supplied) | P5 support documentation | Group |
| 8 | Front-sheet data missing | Cover page | Group |
| 9 | Presentation not produced | Activity 2 deliverable | Group |

---

## 14. Gate summary

| Gate | Condition to pass | Current |
|---|---|---|
| G1 Format | Every formatting item in §1 ticked | Partly — draft DOCX/PDF exist (23 pages); final formatting checks pending |
| G2 Front sheet | Complete, signed, unnumbered first page | Not met |
| G3 P4 | Questionnaire plus real participant table plus raw responses | Not met — instrument only |
| G4 P5 | APK installs on target device; screenshots complete; support docs written | Partly — fixed APK verified; 16 final captures; support docs missing |
| G5 M4 | Each tool has activity-level evidence; sprint gaps acknowledged | Partly |
| G6 P6 | Matrix with expected and actual results; no unevidenced "Met" | Partly — real-final loop verified; cold start and Ask round trip measured; NFR2 pending |
| G7 M5 | Risk and testing critique names real gaps | Drafted |
| G8 D2 | Every adopted change has a commit and a test; every rejection has a reason | Partly — peer feedback missing |
| G9 Integrity | All checks in §11 ticked | Not met |
| G10 Evidence gate | Only real-final screenshots cited as final; all root captures labelled DEMO | Met — 16 final, root 01–22 marked DEMO |
| G11 Submission | PDF uploaded to CMS with receipt | Not started |
