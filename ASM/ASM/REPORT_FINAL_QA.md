# ASM2 — Report Final QA

**Scope:** report and control Markdown in `ASM/ASM/`, draft report DOCX/PDF, draft presentation PPTX/PDF, the 16-image evidence gate, and the Harvard working list.
**Date:** 2026-11-10.
**Worktree:** `C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment`
**Branch:** `Assignment_Report`.
**Rule applied:** editorial and evidence-gate checks only. No application source was modified, no participant/survey/consent/reference/rehearsal/score/signature value was invented, and no commit or push was performed.

---

## 1. Verdict

| Check | Result |
|---|---|
| Markdown strict UTF-8 decode (25 files) | **PASS** — 0 decode errors |
| Mojibake / replacement characters | **PASS** — 0 in Markdown, DOCX, PDF |
| Credential-shaped values | **PASS** — 0 in binaries and Markdown after cleanup; only empty or placeholder assignments existed before the edit |
| Report source and draft binaries | **PASS** — `ASM2_REPORT_COMPACT.md`; DOCX/PDF exist |
| DOCX page count | **23 pages** (Word COM) |
| PDF page count | **23 pages**; 31,937 extracted characters |
| Vietnamese diacritics | **PASS** — precomposed characters present (`Chính xác`, `Toán học`); 0 replacement characters |
| Front sheet individual metadata | **PASS** — student/ID/class/assessor present; group and member rows N/A — individual submission |
| Front-sheet date, scores, signatures | `[PENDING]` — not invented |
| Evidence gate | **PASS** — 16 authoritative final PNGs; 0 parent demo PNGs in DOCX/PPTX |
| DOCX embedded final images | **16 of 16** final; 3 UML; 1 template logo; 0 root demo |
| PPTX embedded images | 9 final + 3 optional supplementary + 2 UML; 0 root demo |
| Presentation deck | **Rebuilt** — 12 slides / 12 PDF pages; slide 1 `N/A — Individual submission`; presentation date `[PENDING]` |
| Stale claims | **Fixed** in report/control docs; historical snapshots marked superseded |
| Harvard list | Format-only fix; access dates and verification remain `[PENDING]` |
| Final submission | **NOT CLAIMED** — pending gates below |

---

## 2. Commands and actual results

### 2.1 Word / document statistics

```
Word COM: Documents.Open(..., ReadOnly=true)
  ComputeStatistics(2) = 23 pages
  ComputeStatistics(0) = 4,406 words
  ComputeStatistics(3) = 25,465 characters
  Paragraphs = 684; Sections = 3
```

### 2.2 Draft binaries

| Artefact | Bytes | SHA-256 |
|---|---:|---|
| `BC00720_KhangNM_ASM2_DRAFT_REPORT.docx` | 2,137,656 | `26d9e239165da2fe190fc372b08b2b3582fe034566a11ac42060327564415c80` |
| `BC00720_KhangNM_ASM2_DRAFT_REPORT.pdf` | 1,456,542 | `acb3306f1924c470bfa04bef5b407a7b3f498fdf93cbf3be6bea5ce81563b9ab` |
| `BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pptx` | 1,679,122 | `02b7f6e2727073009e90be67bba836192c22ccfd007eec79db811e4aa4f120ad` |
| `BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pdf` | 807,628 | `1b5d03e32549ab0a640ad863ac40f75136b5a65cada30c48e8b1b9fe4be55122` |
| `BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.export-log.json` | 472 | `da9eaf6d50567e62a2d7cfbc1e251f6facd832c5aeab7dd12d8ed15b369c8213` |

### 2.3 DOCX inspection (python-docx)

```
paragraphs 130 | tables 15 | text characters 36,397
inline shapes 22 | embedded media 20
media mapping: 16 real-final PNG, 3 UML PNG, 1 template logo
root demo image hashes: 0
[PENDING occurrences in DOCX text: 55
```

Front sheet page 1 contains the student name, ID, class and assessor; group number and member rows read `N/A — Individual submission`; submission date, final scores and signatures read `[PENDING]`.

### 2.4 PDF inspection (pypdf + PDF text extraction)

```
pages 23 | extracted characters 31,937 | replacement characters 0
[PENDING occurrences: 45
"Chính xác" present: True | "Toán học" present: True
credential-pattern hits: 0
```

### 2.5 Presentation inspection (python-pptx)

```
slides 12 | picture placements 16 | unique embedded media 14
PDF pages 12 | extracted characters 5,556
media mapping: 9 real-final, 3 optional supplementary, 2 UML, 0 root demo
credential-pattern hits: 0 (PPTX and PDF)
```

Slide 1 reads `Group/member fields: N/A — Individual submission` and `Presentation date: [PENDING]`; the string `Group number` has 0 occurrences. The deck was rebuilt from `scripts/build_asm2_presentation.py` (no manual binary edit) and is documented in `PRESENTATION_FINAL_QA.md`.

### 2.6 Software archive (rebuilt after package-doc sync)

```
staging: 243 files / 87 directories, 41,168,661 bytes
ZIP: ASM2_Software_Package.zip, 243 members, 16,680,560 bytes
SHA-256: 340b92e27410090bdf05262e1674274eff1c141eb8ad9f32c87360355e816d90
16 final PNG | 8 optional PNG | 0 parent demo PNG | 0 forbidden paths | 0 key-like hits
```

`PACKAGE_QA.md`, `SOFTWARE_PACKAGE_DRAFT.md`, `README_PACKAGE.txt` and `FILE_LIST.txt` were refreshed to this archive. The archived `PACKAGE_QA.md` points to the external copy for its own ZIP hash (a file cannot contain its own archive hash).

---

## 3. Evidence gate

`ASM/ASM/evidence/pixel-api-35/real-final/` contains exactly **16 PNGs** (`01`–`16`) plus the manifest and logs. The parent evidence folder contains **22 demo/pre-final PNGs**, none of which appears in the DOCX or PPTX or is cited by filename as final in the report source.

| Gate item | Actual state |
|---|---|
| Final PNG count | 16 |
| Optional PNG count | 8 (supplementary only) |
| Parent demo PNG count | 22 (retained; excluded from the report package) |
| DOCX final images embedded | 16 of 16 |
| DOCX root demo images | 0 |
| PPTX root demo images | 0 |
| Report source parent filenames cited | 0 |
| 159 JVM / 39 instrumented / real Gemini | Recorded in the compact; unchanged |
| Cold start / Ask round trip | Measured observations; no SLO claim |
| NFR2 action count, history timing, UI-frame timing | `[PENDING]` |

The compact source names Figures 4–6 by `real-final/` path and describes Appendix D as the 16-image set; the draft lists all 16 filenames. No image was added.

---

## 4. Editorial fixes applied

| File | Fix |
|---|---|
| `ASM2_REPORT_COMPACT.md` | Narrowed the PENDING list to date/scores/signatures/rehearsal/references; `twelve` → `thirteen` JVM classes; APK size corrected to 35,596,239 bytes; user guide marked supplied; login-after-logout and image captures marked supplementary; demo-account procedure retained as PENDING |
| `ASM2_REPORT_DRAFT.md` | Branch updated to `Assignment_Report`; front-sheet completion note, 2,936-word body and signature scope aligned with individual submission; `12` → `13` JUnit classes (two places); APK size corrected (two places); measured timings no longer labelled PENDING; three instrumented runs; NFR1 set to Partial (observation) with UI-frame timing still pending; sanitized APK note; user guide marked supplied |
| `ASM2_SUBMISSION_CHECKLIST.md` | Offline capture ticked as supplementary; deck-production and deck-storage ticked as DRAFT; export section reflects existing draft binaries; software archive items ticked with actual ZIP facts; G4 support-docs state corrected; owner column changed from Group to Student; presenter wording made individual |
| `FINAL_REPORT_PACKAGE.md` | Figures 1–5 → 1–6; software package section replaced with the verified archive layout and checks; owners Group → Student; “every member” → “the student”; updated date |
| `ASM2_REPORT_INPUTS.md` | Purpose says final DOCX/PDF (draft exports exist); package contents marked verified |
| `DRAFT_EXPORT_QA.md` | Status clarifies draft DOCX/PDF exist and no final binary has been produced |
| `PRESENTATION_DRAFT_QA.md` | Replaced with the AUDIT rebuilt-deck QA; slide 1 is individual-submission text and the presentation date remains pending |
| `PRESENTATION_FINAL_QA.md` | Copied from AUDIT; records the rebuilt PPTX/PDF hashes, 12 slides/pages and the media/secret checks |
| `ASM2_PRESENTATION_OUTLINE.md`, `scripts/build_asm2_presentation.py`, deck PPTX/PDF/export log | Copied from AUDIT; deck rebuilt from the corrected script, hashes verified |
| `ASM2_SUBMISSION_CHECKLIST.md` | Copied from AUDIT for the deck metadata; package/offline/user-guide rows reconciled with the verified archive and optional captures; package row points to the new ZIP hash |
| `PACKAGE_QA.md`, `SOFTWARE_PACKAGE_DRAFT.md`, `README_PACKAGE.txt`, `FILE_LIST.txt` | Refreshed for the rebuilt ZIP (243 members, 16,680,560 bytes, hash `340b92e2…`); staging docs synced |
| `ASM2_EVIDENCE_CHECKLIST.md` | Phase note updated to 16 final + optional captures, real Gemini and timings; resolved blockers marked; owners Group → Student |
| `ASM2_AUDIT_PLAN.md` | Added superseded-snapshot banner; NFR1 row corrected to Partial (observation); 20-screenshot gate corrected to the 16-image set |
| `ASM2_IMPLEMENTATION_BASELINE.md` | Added superseded-baseline banner; Pixel API 35 capture status corrected; brief “group report” wording clarified; NFR1/NFR3/NFR5 rows updated; test-count finding updated to 159/13 |
| `ASM2_EVIDENCE_LOG.md` | Added superseded-snapshot banner; device capture status corrected; EV-118 (cold start) and EV-119 (AI latency) marked VERIFIED with measured values |
| `PACKAGE_HANDOVER_CHECKLIST.md` | Notes the archive exists in the Assigment worktree with its hash; AUDIT remains `PENDING` |
| `ASM2_HARVARD_REFERENCES.md` | Entry 4 formatted as `Beck, K. et al. (2001)`; no source added; access dates unchanged |

Historical documents were not rewritten as current evidence; they carry explicit “superseded” banners and point to this record.

---

## 5. Harvard reference review

- External entries: **17**; unpublished project/assessment entries: **13** (P1–P13).
- Alphabetical order is intact.
- One format correction applied (entry 4, `et al.`).
- Access dates remain the Part 1 dates (26 October 2026) and are **not verified** in this pass.
- Entries still flagged `[PENDING: verify]` or with edition/version questions: 12 external entries (1–4, 6, 8, 9, 11–13, 16) plus project/assessment metadata.
- No reference was added, removed, or promoted to “verified”.

---

## 6. Remaining gates (not satisfied)

| Gate | State |
|---|---|
| Peer-review participants, consent and real responses | `[PENDING]` — form is a draft; no responses exist |
| Harvard URLs, access dates and editions | `[PENDING]` — 12 external entries flagged |
| Submission date | `[BLANK: set by the centre]` |
| Final scores | `[PENDING: assessor]` |
| Student and assessor signatures/dates | `[PENDING]` |
| Presentation rehearsal, live demo and timing | `[PENDING]` — DRAFT deck exists |
| Demo-account procedure | `[PENDING]` — user guide supplied |
| NFR2 action count, history open timing, offline timing | `[PENDING]` |
| 100 ms UI-frame timing for NFR1 | `[PENDING]` |
| OS-level push notification permission | `[PENDING: optional]` |
| Subject-mapping defect | `[PENDING]` — observed, unfixed |
| Answer-quality/accuracy study | `[PENDING]` |
| Tutor word-limit decision (`DEC-01`) | `[PENDING]` |
| Pull-request history on the remote | `[PENDING]` |
| Final PDF export and CMS upload | **Not started** |

The draft DOCX/PDF retains 55/45 `[PENDING` tags. They are acceptable in the DRAFT binaries but must be converted to limitation prose or removed before the final export.

---

## 7. Residual issues

1. **Deck rebuilt and consistent** — slide 1 shows `N/A — Individual submission`, presentation date `[PENDING]`, 12 slides/pages; no Group-number text. See `PRESENTATION_FINAL_QA.md`.
2. **Historical audit snapshots** (`ASM2_AUDIT_PLAN.md`, `ASM2_IMPLEMENTATION_BASELINE.md`, `ASM2_EVIDENCE_LOG.md`) retain dated 152-test and older APK-size values inside their original tables. They are marked superseded; current documents use 159 tests and the fixed APK size.
3. **Compact appendix D** refers to the 16-image set without listing every filename; the full draft appendix lists all 16, and the DOCX embeds all 16. No image was added or substituted.
4. **Draft binaries** are not final: pending tags remain and no signature/date/scores exist.
5. **Front-sheet template heading** in the report DOCX reads “ASSIGNMENT GROUP WORK / OBSERVATION RECORD”; this is supplied template wording. Group number and member rows are `N/A — Individual submission`.
6. **`CONTROL_PACK_QA.md`** retains three historical rows that describe the pre-fix state (no final DOCX/PDF, NFR1 not tested, user guide pending). They are marked as an earlier snapshot by the follow-up banner; `REPORT_FINAL_QA.md` is authoritative for current status.

---

## 8. Branch and repository state

```
branch: Assignment_Report
app source changed: none
commit/push: not performed during this QA pass; the delivery commit is authorized separately
```

Modified in this QA pass: report/control Markdown, the rebuilt deck binaries copied from AUDIT, and the software archive. The report DOCX/PDF were inspected and left unchanged; the deck was rebuilt in AUDIT from the corrected script and verified by hash.

**Final status:** report package is editorially consistent and the evidence gate holds; submission is **not complete** because the gates in §6 remain open.
