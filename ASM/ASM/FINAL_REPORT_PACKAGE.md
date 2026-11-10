# ASM2 — Final Report Package Plan

**Purpose:** turn the working report source into the submitted deliverables without inventing anything.
**Status:** DRAFT DOCX, PDF and 12-slide PPTX/PDF exist in `ASM/ASM/deliverables/draft/`. Final submission artefacts remain gated on the pending inputs.
**Working report source:** `ASM/ASM/ASM2_REPORT_COMPACT.md` (English only; 2,936-word body; tables, figures, references and appendices excluded)
**Evidence pack:** `ASM/ASM/ASM2_REPORT_DRAFT.md` (full tagged draft; keep for traceability and appendices)
**Required inputs:** `ASM/ASM/ASM2_REPORT_INPUTS.md`
**Checklist:** `ASM/ASM/ASM2_SUBMISSION_CHECKLIST.md`
**Prepared:** 2026-11-09

## Front sheet metadata (copy into the supplied template)

| Field | Value |
|---|---|
| Qualification | Pearson BTEC Level 5 Higher National Diploma in Computing |
| Unit | Unit 22: Application Development |
| Student | Nguyen Minh Khang |
| Student ID | BC00720 |
| Class | SE08201 |
| Assessor | Mr. Le Nhat Quang |
| Group number | N/A — individual submission |
| Group members and codes | N/A — individual submission |
| Submission date | `[BLANK: set by the centre]` |
| Final scores | `[PENDING]` |
| Student / assessor signatures and dates | `[PENDING]` |

---

## 0. What is being packaged

| Deliverable | Form | Source |
|---|---|---|
| Report (individual submission) | DOCX → PDF | `ASM2_REPORT_COMPACT.md` |
| Software package | ZIP | `AI Study/` source, debug APK, README, user guide |
| Presentation | 12 slides → PDF | Draft PPTX/PDF exist; rehearsal `[PENDING]` |

The report is an individual submission and must open with Assignment front sheet 2; the group and member fields are N/A.

---

## 1. Source-of-truth map

| Content | Authoritative source | Status |
|---|---|---|
| Problem, requirements, design decisions D-1…D-12 | `docs/PART1_M1_SOFTWARE_DESIGN.md`; Part 1 brief | `[DOC-ONLY]` |
| Test plan and case catalogue | `docs/PART1_M1_TEST_PLAN.md` | `[DOC-ONLY]` |
| JVM test result | `AI Study/app/src/test` (159 `@Test`), baseline XML 152 | 159 tests, 0 failures after the fix `[VERIFIED]` |
| Instrumented result | `real-final/tests-connected-result.xml` | 39 tests, 0 failures after the fix `[VERIFIED]` |
| Runtime session | `ASM/ASM/evidence/pixel-api-35/PIXEL_API35_RUNTIME_EVIDENCE.md` | `[VERIFIED]` |
| Device setup | `ASM/ASM/evidence/AVD_API35_SETUP.md` | `[VERIFIED]` |
| Screenshots | `real-final/` (16 final); root 01–22 demo archive | `[VERIFIED]` |
| Defect register | `docs/BUG_LIST_NP5.md` | `[DOC-ONLY]` |
| Sprint log | `docs/AGILE_SPRINT_LOG.md` | S04–S13 empty `[VERIFIED]` |
| Peer review | Google Form draft + `PEER_REVIEW_FORM.md` | Instrument only; responses `[PENDING]` |
| References | `ASM2_HARVARD_REFERENCES.md` | 17 external + 13 project entries; verification `[PENDING]` |

Rule: when two sources disagree, the verified artefact wins. In particular, quote **159** JVM tests after the fix (152 was the pre-fix baseline), never 143, 76 or 120.

---

## 2. Figure plan

Rendered UML already exists in `docs/uml/png/`. Note the one filename mismatch: `03c_class_security.puml` renders as `03c_class_platform.png`; use the PNG set.

### 2.1 Body figures (six, matching the compact source)

| ID | Content | File | Caption |
|---|---|---|---|
| Figure 1 | Component view: four layers, one module | `docs/uml/png/06_component.png` | Four-layer architecture and module boundaries |
| Figure 2 | Ask → cache → answer flow | `docs/uml/png/04_activity_ask_flow.png` | Exact-hash and similarity reuse before a provider call |
| Figure 3 | Entity relationship diagram | `docs/uml/png/09_erd.png` | Room schema, version 1, seven tables |
| Figure 4 | Welcome, real-final build | `ASM/ASM/evidence/pixel-api-35/real-final/01-welcome.png` | **FINAL** — no simulation banner |
| Figure 5 | Real Gemini answer | `ASM/ASM/evidence/pixel-api-35/real-final/06-ask-gemini-answer.png` | **FINAL** — steps and saved-to-history |
| Figure 6 | Corrected practice feedback | `ASM/ASM/evidence/pixel-api-35/real-final/12-practice-correct-feedback.png` | **FINAL** — option B correct, “Chính xác!” |

Only `real-final/` images may appear in the body as evidence. Root files 01–22 are **DEMO / NON-FINAL** and belong in Appendix D with that label. Optional UML additions if space allows: use case (`01_use_case.png`), domain class (`02_class_domain.png`), question state (`08_state_question.png`).

### 2.2 Appendix D screenshot set (16 final + optional supplementary + demo archive)

`ASM/ASM/evidence/pixel-api-35/real-final/` holds sixteen 1080×2400 captures plus `MANIFEST_REAL.txt`; these are the final set. Insert them in filename order in Appendix D. `real-final/optional/` holds image-question, offline error/recovery and login-after-logout captures; label them **supplementary** in a separate subsection and never substitute them for the sixteen. The root folder's 01–22 are demo/pre-final material: keep them in a clearly labelled archive subsection, or omit them, but never caption them as final. Every final caption must state the screen, the requirement demonstrated, `Pixel_6_API_35`, Android 15 / API 35, the fixed APK SHA `c7c8f9e2…`, and real Gemini. Insert at 6.0–6.5 cm width and centre each image.

### 2.3 Measured NFR observations

`ASM/ASM/NFR_MEASUREMENTS.md` (Pixel_6_API_35, fixed APK, 2026-11-10 00:51 +07:00) records two observations; no project threshold or SLO exists, so these are not compliance claims.

| Metric | n | Values (ms) | Summary |
|---|---:|---|---|
| Cold start (`am start -W` TotalTime) | 5 | 5139, 3284, 3108, 3310, 3041 | min 3041, median 3284, max 5139, mean 3576.4 |
| Gemini Ask round trip (POST → HTTP 200) | 3 | 2207, 913, 861 | min 861, median 913, max 2207, mean 1327.0; 3/3 HTTP 200, 0% loss |

NFR2 action count, history open time and offline timing remain unmeasured. Offline was observed functionally: explicit error while offline, successful retry after reconnect, no queue.

---

## 3. Appendix plan

| Appendix | Content | Source | State |
|---|---|---|---|
| A | Questionnaire, 16 items verbatim with anchors and form URL | §2.2 of the draft | Ready |
| B | Participant table and raw responses | Form responses | `[PENDING: no responses]` |
| C | Test evidence: JVM aggregate, instrumented XML, 20-case matrix decision | XML files | JVM and instrumented ready; 20-case selection `[PENDING]` |
| D | Screenshot set: 16 final in `real-final/`; optional image/offline/login captures are supplementary; root 01–22 as demo archive | `ASM/ASM/evidence/pixel-api-35/` | Ready |
| E | Repository and toolchain evidence | Git, `libs.versions.toml`, workflow | Ready except graph/CI screenshots `[PENDING]` |
| F | Measurement log | `ASM/ASM/NFR_MEASUREMENTS.md` | Cold start and Ask round trip measured; action count, history and offline timing `[PENDING]` |

Appendix C.3 decision (full draft): Part 1's M1 test plan already supplies the case catalogue, so no new 20-case list is written. Select twenty existing cases, map them to the FR/NFR matrix, and record expected versus actual. Until actual results exist, keep the column empty.

---

## 4. Tag handling

The compact source carries only `[PENDING]` markers. Convert each into an explicit limitation sentence; none may survive into the PDF. If text is copied from the full draft, apply the wider rules below.

| Tag | Action |
|---|---|
| `[VERIFIED]`, `[VERIFIED: …]` | Delete the tag, keep the statement as normal prose |
| `[DOC-ONLY]` | Delete the tag, keep the statement, and make sure the source document is cited |
| `[PENDING]`, `[PENDING: …]` | Convert into an explicit limitation sentence, or delete the row if the item is not claimed |
| `[INSERT FIGURE n: …]` | Delete once the figure is inserted |
| `[OBSERVED: …]` | Convert to prose in the evaluation, not a tag |
| "Pre-submission honesty checks" section | Internal only — remove before export |

Never convert a `[PENDING]` into a claim. If a section cannot be written, write what is unknown and why.

---

## 5. Word budget and trimming plan

| Measure | Value |
|---|---|
| Compact source body, measured | **2,936 words** (tables, figure captions, references and appendices excluded) |
| Submission limit, whole report (Submission Format) | 2,000–2,500 words ±10%, excluding tables, diagrams, appendices, references |
| Activity 2 development report limit | 2,500–3,000 words |
| Full draft, whole file including tables and appendix scaffolding | approximately 11,500 words — evidence pack only, never exported as-is |

The compact source is inside the Activity 2 limit. Two editorial checks remain:

1. **Confirm the counting method.** The figure above excludes table rows, figure captions, the reference list and the appendix index. Word's own counter includes table text, so report the excluded-items method explicitly rather than quoting Word's raw number.
2. **If the tutor applies the 2,000–2,500 whole-report limit**, roughly 400 words must go. The least damaging cuts are Section 3.3 (process record), Section 5.1 (investigation) and Section 6.5 (strategy evaluation); do not cut the requirement tables, which carry the rubric evidence.

Detail that did not fit the compact body remains in `ASM2_REPORT_DRAFT.md` and in the appendices; nothing was deleted.

---

## 6. DOCX build steps

Work in a copy. Do not edit the source draft in place after the writing pass begins.

1. Copy the supplied template `C:\Users\ADMIN\orca\AndroidStudio\ASM\ASM\FRONTSHEET ASM FINAL REPORT (GROUP).docx` to `ASM/ASM/ASM2_FINAL_REPORT.docx`.
2. Page setup: A4; margins left 1.25 cm, right 1 cm, top 1 cm, bottom 1 cm; apply to the whole document.
3. Define styles before pasting: Normal = Calibri 12, 1.5 line spacing, 6 pt after; Heading 1/2/3 = Calibri bold 16/14/12; Caption = Calibri 10 italic.
4. Complete the front sheet using the metadata block at the top of this file; make it page 1 without a page number. Insert a section break (Next page) and restart numbering at 1 for the body.
5. Paste sections 1–7 from `ASM2_REPORT_COMPACT.md` in order. Apply heading styles as you go so the contents page can be generated.
6. Strip the tags per §4.
7. Insert Figures 1–5 from §2.1 at the marked positions; centre each; set screenshot width to 6.0–6.5 cm. Insert Tables 1–9 where the compact source places them and keep their captions.
8. Add captions with References → Insert Caption. Keep automatic numbering; a manual "Figure 1" typed as text will drift.
9. Add cross-references to figures and tables in the prose (References → Cross-reference).
10. Build the table of contents (References → Table of Contents) and the list of figures/tables.
11. Add page numbers at the bottom centre, with "Link to Previous" off for the front-sheet section.
12. Appendices A–F, each starting on a new page, each with a heading at Heading 1.
13. References section in Harvard style, alphabetical, access dates for online sources.
14. Review → Language → English (United Kingdom); run spelling and grammar.
15. Review → Word Count; record the body figure and the counting method.
16. Save, then inspect the document in print layout at 100% for orphaned headings and split tables.

---

## 7. PDF export and verification

1. File → Save As → PDF, using the DOCX as the only source.
2. Open the PDF and check: front sheet first and unnumbered; page numbers continuous; fonts embedded; figures not cropped; tables not split mid-row.
3. Check that the table of contents page numbers match the body.
4. Confirm no internal tag text survives: search the PDF for `[VERIFIED`, `[PENDING`, `[DOC-ONLY`, `[INSERT FIGURE`.
5. Confirm the evidence gate holds: Figures 4–6 are `real-final/` captures, and every root-folder caption is marked `DEMO / NON-FINAL`.
6. Confirm the API key does not appear: search for `AIza` and for `GEMINI_API_KEY=`.
7. Check the word count claim against 2,936 and confirm the exclusion method (tables, figures, references, appendices).
8. Confirm tables are numbered 1–9 and figures 1–6 with no gaps.
9. Open the PDF on a second machine or a different viewer.
10. Upload to `http://cms.btec.edu.vn/` and retain the receipt.

---

## 8. Software package contents

```
ASM2_Software_Package.zip
├── source/            AI Study source, no build folders, no local.properties, no ai.properties
├── app-debug.apk      debug build, versionName 1.0
├── README.md          build and run instructions
├── USER_GUIDE.md      supplied at ASM/ASM/USER_GUIDE.md
└── EVIDENCE/          JVM XML, instrumented XML, screenshot manifest
```

Checks before zipping:

- [ ] List the archive contents and read them; do not trust the compression step
- [ ] No `local.properties`, `ai.properties`, `.keystore`, `.jks` or token files
- [ ] No `build/` directories, no `.gradle/` caches
- [ ] APK installs from inside the archive on a clean emulator
- [ ] README reproduces a runnable build without editing source

---

## 9. Remaining user inputs

| # | Input | Why it is needed | Owner | Blocks |
|---|---|---|---|---|
| 1 | Front-sheet completion: submission date, final scores, signatures | Student, ID, class and assessor supplied; group/member fields N/A — individual submission; date, scores and signatures `[PENDING]` | Student / assessor | Export |
| 2 | Reviewer recruitment and real responses | P4, M3, D2, Appendices A–B | Group | Sections 2 and 6.1 |
| 3 | Consent for names, roles and quotations | Ethical use of participant data | Group | Appendix B |
| 4 | Tutor decision on the word limit (`DEC-01`) | The compact body is 2,936 words, inside the Activity 2 limit; if the 2,000–2,500 whole-report limit governs, about 400 words must be cut | Tutor | Export |
| 5 | Remaining measurements: action count, history open, offline timing | Cold start (median 3284 ms) and Ask round trip (median 913 ms) are measured; NFR2 and the remaining rows stay pending | Tester with the emulator | P6 completeness |
| 6 | Remaining optional capture: OS-level push permission | Login-after-logout, image question and offline error/recovery are captured in `real-final/optional/` as supplementary evidence | Group | Nothing mandatory |
| 7 | Demo-account procedure only (the user guide is supplied) | Completes P5 support documentation | Group | Package |
| 8 | Presentation rehearsal and live demo (12-slide DRAFT deck and PDF supplied) | Activity 2 delivery | Group | Presentation |
| 9 | Reference verification: 17 external entries plus project documents in `ASM2_HARVARD_REFERENCES.md` | Confirm URLs, access dates and editions before export | Group | Section 10 |
| 10 | Final read-through and sign-off by every member | Ownership of the submitted text | Group | Upload |

Item 5 is needed only for the unmeasured NFR2/history rows; item 6 is optional. Items 1, 2, 4, 7 and 8 are required.

---

## 10. Execution order

1. Tutor decides the word limit; record the decision in the document control table.
2. Complete the front sheet from the metadata block; group/member fields are N/A — individual submission.
3. Recruit reviewers; publish the form only when reviewers are ready; collect responses; write §2.5–2.6 and §6.1.
4. Cold start and Ask round trip are measured; run only the remaining rows (action count, history open, offline timing) and fill Appendix F. Do not estimate.
5. Real-final set is complete (`real-final/01–16`). Login-after-logout, image and offline error/recovery are captured in `real-final/optional/` as supplementary evidence; OS push remains optional.
6. Write the user guide and the demo-account procedure.
7. Editorial pass on the compact: convert each `[PENDING]` into an explicit limitation sentence, insert Figures 1–6 and Tables 1–9, and build the contents page.
8. Export the DOCX and the PDF; run the checks in §7.
9. Build the software package; verify the archive.
10. Rehearse the presentation with the same build and device.
11. Upload and retain the receipt.

---

## 11. Integrity checks specific to this package

- No participant, score, quote or measurement appears that was not actually collected.
- The reported body word count matches the measured 2,936 and states the exclusion method.
- Every screenshot is captioned `Pixel_6_API_35`, Android 15, API 35, with its status and, for the final set, the fixed APK SHA `c7c8f9e2…`.
- Only the sixteen `real-final/` images are treated as final; root 01–22 are captioned DEMO / NON-FINAL.
- No simulated answer is presented as a Gemini response.
- The 39-test result cites the post-fix XML (and the earlier runs); the 159-test result cites the annotation count and the full-suite log.
- "Implemented, unverified" or "demo-only" is used wherever no final run or test exists.
- No claim that all user data is encrypted; no claim about answer accuracy.
- The form remains unpublished until reviewers are ready; it is never filled with synthetic responses.
- The API key, if ever configured, never appears in the report, the package or a screenshot.

---

## 12. Do not

- Do not invent survey responses to unblock Activity 1.
- Do not present simulated answers as model output.
- Do not convert a limitation into a satisfied requirement for tidiness.
- Do not quote the 143, 76 or 120 test figures.
- Do not caption an image with a device it did not come from.
- Do not include the internal honesty-check list in the submitted PDF.
- Do not modify app source to make a screenshot or a test result look better.
