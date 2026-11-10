# ASM2 — Draft Export QA

**Scope:** the generated draft binaries in `ASM/ASM/deliverables/draft/`.
**Status:** DRAFT — NOT FOR FINAL SUBMISSION. No DOCX/PDF was produced for final submission, and no survey findings or verified references are claimed.
**Date:** 2026-11-10.

---

## 1. Outputs

| File | Size |
|---|---|
| `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_REPORT.docx` | 2,087.6 KB |
| `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_REPORT.pdf` | 1,422.4 KB |
| `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_REPORT.export-log.txt` | 1.3 KB |
| `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_REPORT.export-log.json` | 2.1 KB |

**Rerun:**

```
python ASM/ASM/scripts/build_asm2_draft.py
```

The script overwrites the four outputs. It reads only the compact Markdown, the official front-sheet template, the UML PNGs and the real-final screenshots; it never touches app source.

---

## 2. Export method

| Item | Value |
|---|---|
| Source | `ASM/ASM/ASM2_REPORT_COMPACT.md` |
| Source SHA-256 | `dab06f32f13a2d43561d83389ac357556f20582f34a272822cc018e2e28378a4` |
| Template | `C:\Users\ADMIN\orca\AndroidStudio\ASM\ASM\FRONTSHEET ASM FINAL REPORT (GROUP).docx` |
| DOCX builder | python-docx 1.2.0, Python 3.14.5, Windows 11 |
| PDF conversion | Microsoft Word COM, `ExportAsFixedFormat` (PDF), Word 16.0 |
| Page size / style | A4; Calibri 12; 1.5 spacing; margins left 1.25 cm, right/top/bottom 1 cm |
| Header | `DRAFT — NOT FOR FINAL SUBMISSION — BC00720 — ASM2` on every page |
| Page numbers | Body section footer: `Page X of Y` using SECTIONPAGES (body shows `Page 1 of 20`) |
| Layout | Official template occupies pages 1–3 (cover + observation record); report body starts page 4 |
| Timestamp | 2026-11-10T02:48:44 |

---

## 3. DOCX inspection

| Check | Result |
|---|---|
| Paragraphs | 130 |
| Tables | 15 (front sheet 3 + body 11 + Appendix D grid 1) |
| Inline images | 22 |
| Embedded media files | 20 (16 screenshots + 3 UML + 1 template logo) |
| Body headings parsed | 40 |
| Body tables parsed | 11 |
| Body figures inserted | 6, none missing |
| Appendix D images | 16 |
| DRAFT label present | Yes — header and body title block |
| Front-sheet metadata | Nguyen Minh Khang / BC00720 / SE08201 / Mr. Le Nhat Quang present |

**Front-sheet state:** group number and member rows show **N/A — Individual submission**; submission date, final scores and signatures remain visibly `[PENDING]`; assessment boxes are untouched.

---

## 4. Image integrity

Embedded media were hashed and matched against the source files.

| Source set | Matched |
|---|---|
| `Assigment/.../real-final/*.png` | **16 of 16** |
| `Assigment/docs/uml/png/` | 3 (`06_component`, `04_activity_ask_flow`, `09_erd`) |
| Template logo | 1 (`image1.png`) |
| Root demo images | **0** |

The 16 final names embedded:

```
01-welcome.png, 02-register-filled.png, 03-onboarding-step1.png,
04-onboarding-step2.png, 05-ask-input.png, 06-ask-gemini-answer.png,
07-history-real-question.png, 08-question-detail.png, 09-progress-real.png,
10-practice-generate.png, 11-practice-question.png,
12-practice-correct-feedback.png, 13-profile.png, 14-notifications.png,
15-logout-confirm.png, 16-logged-out-welcome.png
```

No root-folder (demo) image hash appears in the package.

---

## 5. PDF inspection

| Check | Result |
|---|---|
| Pages | 23 total: template pages 1–3, report body pages 4–23 (body numbering 1–20) |
| Extracted characters | 31,937 |
| Page 1 | Front sheet with metadata and `[PENDING]` fields, plus the DRAFT header |
| Page 4 | Report title block and body start; footer `Page 1 of 20` |
| Section headings | Introduction, Activity 1, Activity 2, Critical Review, Feedback Decisions, Appendix D, References all found |
| Page-number field | `Page X of Y` present |
| DRAFT label | Present |

---

## 6. Secret scan

| Pattern | Result |
|---|---|
| `AIza…` Google API key | Not found |
| provider-key assignment | Not found |
| `sk-…` key shape | Not found |

The only long hex strings are APK SHA-256 digests, which are build evidence, not secrets.

---

## 7. Pending content

- Source PENDING markers: **30**.
- DOCX text contains **55** occurrences because merged table cells duplicate text; no pending item was removed.
- Pending areas preserved: survey responses and participants, submission date, signatures, scores, reference verification, presentation rehearsal, demo account, remaining NFR rows, optional OS-push capture.

---

## 8. NFR measurements

`ASM/ASM/NFR_MEASUREMENTS.md` now records two measured observations on `Pixel_6_API_35` (fixed APK, 2026-11-10 00:51 +07:00):

| Metric | n | Values (ms) | Summary |
|---|---:|---|---|
| Cold start (`am start -W` TotalTime) | 5 | 5139, 3284, 3108, 3310, 3041 | min 3041, median 3284, max 5139, mean 3576.4 |
| Gemini Ask round trip (POST → HTTP 200) | 3 | 2207, 913, 861 | min 861, median 913, max 2207, mean 1327.0; 3/3 HTTP 200 |

NFR2 action count, history open time and offline timing remain pending. Offline behaviour is a functional observation (explicit error, successful retry, no queue), not a timing claim. No SLO is defined, so the report calls these observations, not compliance.

Optional captures in `real-final/optional/` — image question, offline error/recovery and login after logout — are supplementary and do not replace the sixteen-image final gate.

---

## 9. Word count

| Measure | Value |
|---|---|
| Markdown prose (tables, figures, appendices and references excluded) | **2,936** |
| DOCX/Word count including front sheet and tables | 4,406 |

Report the 2,936 figure with the exclusion method; Word's raw count includes table and front-sheet text.

---

## 10. Remaining blockers

1. Survey responses, participants and consent.
2. Submission date, final scores and signatures (group/member fields are N/A — individual submission).
3. Harvard reference verification (17 external entries, 12 flagged).
4. Presentation rehearsal and timing.
5. Remaining NFR rows: action count, history open, offline timing.
6. Demo-account procedure.
7. Optional: OS-level push capture; formula/loading captures.

No commit or push was performed.
