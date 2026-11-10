# ASM2 — Report Inputs Required Before Export

**Purpose:** the single list of what the user must still supply before the working report can be exported to DOCX/PDF.
**Working report:** `ASM/ASM/ASM2_REPORT_COMPACT.md` — English only, 2,936 prose words (tables, figures, appendices and references excluded).
**Status:** inputs below are `[PENDING]` unless marked supplied. Nothing in this file may be filled with invented values.

---

## 1. Already supplied (do not change without instruction)

| Field | Value |
|---|---|
| Student | Nguyen Minh Khang |
| Student ID | BC00720 |
| Class | SE08201 |
| Assessor | Mr. Le Nhat Quang |
| Unit | Unit 22: Application Development |
| Qualification | Pearson BTEC Level 5 Higher National Diploma in Computing |
| Report language | English only |
| Baseline | `BC00720_KhangNM_Assignment_Part1.pdf` and the Part 1 project docs |
| Cover template | `C:\Users\ADMIN\orca\AndroidStudio\ASM\ASM\FRONTSHEET ASM FINAL REPORT (GROUP).docx` |
| Evidence set | `ASM/ASM/evidence/pixel-api-35/real-final/` (16 images, final) |
| Build under test | Practice-fix APK, SHA-256 `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71` |

---

## 2. Required inputs

### 2.1 Peer-review survey export

| Field | Required content | Status |
|---|---|---|
| Responses file | Google Form export (CSV/XLSX) or a link to the sheet, with one row per participant | `[PENDING]` |
| Participant count | Invitations sent, responses received, valid responses (three numbers) | `[PENDING]` |
| Participant identifiers | PR-01… matching Appendix B | `[PENDING]` |
| Participant profiles | Full name, role, job/study description, date and mode | `[PENDING]` |
| Consent | Confirmation that names, roles and quotations may be used | `[PENDING]` |
| Raw answers | Q1–Q12 verbatim per participant, including open-text answers | `[PENDING]` |

### 2.2 Front sheet completion

| Field | Status |
|---|---|
| Group number | N/A — individual submission |
| Group members and student codes (one row each) | N/A — individual submission |
| Submission date | `[BLANK: set by the centre; do not use the current date]` |
| Final scores | `[PENDING: assessor]` |
| Student signature(s) and date | `[PENDING]` |
| Assessor signature and date | `[PENDING]` |

### 2.3 Harvard reference list

| Field | Required content | Status |
|---|---|---|
| Final reference list | Alphabetical, Harvard style, with access dates for online sources | `[PENDING]` |
| External sources used | Only sources actually consulted | `[PENDING]` |
| Internal documents | Cite as project documentation (design doc, test plan, bug log, sprint log, D2 notes) | `[PENDING: confirm citation form]` |
| AI-tool disclosure | Wording required by the front-sheet declaration | `[PENDING]` |

### 2.4 Presentation

| Field | Required content | Status |
|---|---|---|
| Slide deck | File (PPTX/PDF) covering problem, requirements, architecture, demo, evidence, limitations, future work | `[SUPPLIED: deliverables/draft/]` |
| Rehearsal | Date and build/device used for the live demo | `[PENDING]` |
| Fallback | Recorded walkthrough or screenshot backup | `[PENDING]` |

### 2.5 NFR measurements (Appendix F)

A verified record exists at `ASM/ASM/NFR_MEASUREMENTS.md`. Cold start and Gemini Ask round-trip are measured observations; action count, history timing and offline timing remain required. Functional offline error/recovery is captured separately.

| Metric | Method | Status |
|---|---|---|
| Cold start to Ask screen | `adb shell am start -W`, five runs; min/median/max | `[VERIFIED: 3041, 3108, 3284, 3310, 5139 ms; median 3284]` |
| AI round-trip time | `adb logcat -s GeminiAPI` timestamps, three real questions | `[VERIFIED: 861, 913, 2207 ms; median 913; 3/3 HTTP 200]` |
| History open, N rows | Seeded dataset, timed | `[PENDING]` |
| Actions to submit | Manual count against NFR2 | `[PENDING]` |
| Offline behaviour | Airplane mode, ask a question, record message verbatim | `[VERIFIED: functional error/recovery; timing PENDING]` |
| Lockout persistence | Five failures, kill app, reopen | `[PENDING]` |
| MCQ scoring on real key | Answer a known-correct option; record outcome | `[VERIFIED: real-final/12-practice-correct-feedback.png plus 7 unit tests]` |
| Image round trip | Ask with an image, reopen the saved item | `[VERIFIED: supplementary image capture; redisplay remains partial]` |

### 2.6 Support documentation

| Field | Required content | Status |
|---|---|---|
| User guide | How to register, ask, read, save, practise, view progress, manage the profile | `[SUPPLIED: ASM/ASM/USER_GUIDE.md]` |
| Demo account procedure | Controlled demo account and how to reproduce the walkthrough | `[PENDING]` |
| Package contents | Source archive, debug APK, README, user guide | `[PENDING: assemble]` |

### 2.7 Optional evidence gaps

These are stated as limitations in the report and are not required for submission.

| Item | Status |
|---|---|
| Login-after-logout capture | `[VERIFIED: supplementary real-final/optional/]` |
| Image/camera question flow | `[VERIFIED: supplementary real-final/optional/]` |
| OS-level push notification permission | `[PENDING: optional]` |
| Formula-rendering and loading-state captures | `[PENDING: optional]` |

### 2.8 Decision

| Decision | Status |
|---|---|
| Word limit: Activity 2 (2,500–3,000) or Submission Format (2,000–2,500) — `DEC-01` | `[PENDING: tutor]` |

If the 2,000–2,500 limit applies, about 400 words must be cut; the safest cuts are the process record (§3.3), the investigation (§5.1) and the strategy evaluation (§6.5).

---

## 3. How to supply an input

1. Put survey exports and consent in `ASM/ASM/evidence/survey/`.
2. Put the presentation in `ASM/ASM/presentation/`.
3. Put user-guide and demo-account documents in `ASM/ASM/support/`.
4. Record measurements in a plain text or Markdown file with device, date, method and number of runs.
5. Tell the report owner which items are complete; then the working source is updated and the QA pass is re-run.

---

## 4. Do not

- Do not invent survey responses, participant names or quotes.
- Do not fill the submission date with the current date.
- Do not claim measurements that were not taken.
- Do not add references that were not consulted.
- Do not mark a `[PENDING]` item complete without the artefact above.
