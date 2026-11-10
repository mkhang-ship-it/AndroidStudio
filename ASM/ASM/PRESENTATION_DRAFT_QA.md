# ASM2 — Draft Presentation QA

**Status:** DRAFT — NOT FOR FINAL SUBMISSION. The deck is built and exported; it has not been rehearsed and no survey findings are claimed.
**Date:** 2026-11-10.

---

## 1. Outputs

| File | Size |
|---|---|
| `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pptx` | 1,639.7 KB |
| `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pdf` | 788.4 KB |
| `deliverables/draft/BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.export-log.json` | 0.5 KB |

**Rerun:** `python ASM/ASM/scripts/build_asm2_presentation.py`

---

## 2. Export method

| Item | Value |
|---|---|
| Builder | python-pptx 1.0.2, Python 3.14.5 |
| Slide size | 16:9, 13.333 × 7.5 in |
| PDF conversion | Microsoft PowerPoint COM, `SaveAs(ppSaveAsPDF=32)`, PowerPoint 16.0 |
| PDF result | 12 pages |
| DRAFT label | Footer on all 12 slides, title slide badge, PDF text contains `DRAFT` |
| Speaker notes | Present on all 12 slides |

---

## 3. Slide list

| # | Slide | Pictures |
|---:|---|---:|
| 1 | Title and context | 0 |
| 2 | The problem: generic, unverified, lost | 2 |
| 3 | Requirements and priorities | 0 |
| 4 | Proposed solution and frozen Part 1 design | 0 |
| 5 | Development method and toolchain | 0 |
| 6 | As-built architecture and data flow | 2 (UML) |
| 7 | Demo 1: real question and real Gemini answer | 3 |
| 8 | Demo 2: history, detail and progress | 3 |
| 9 | Demo 3: practice and corrected feedback | 3 |
| 10 | Evidence and testing | 0 |
| 11 | Evaluation against requirements | 0 |
| 12 | Critical review, feedback and next steps | 3 (supplementary) |

Total picture placements: **16**; unique embedded media: **14**.

---

## 4. Image integrity

Embedded media hashes were matched against the source folders.

| Source | Matched |
|---|---|
| `real-final/*.png` (core evidence) | **9** — `01-welcome`, `05-ask-input`, `06-ask-gemini-answer`, `07-history-real-question`, `08-question-detail`, `09-progress-real`, `10-practice-generate`, `11-practice-question`, `12-practice-correct-feedback` |
| `docs/uml/png` | **2** — `06_component`, `09_erd` |
| `real-final/optional/*.png` | **3** — `offline-01-error`, `login-02-success`, `image-02-answer` (slide 12 only, labelled “Supplementary evidence — not part of the 16-image final gate”) |
| Root demo images | **0** |
| Unmatched media | 0 |

The 16-image final gate remains unchanged. Optional captures are supplementary and appear only on the limitations/evidence slide.

---

## 5. Fact check on the deck

| Claim | Value in deck | Source |
|---|---|---|
| JVM tests | 159 tests, 13 classes, 0 failures | `MANIFEST_REAL.txt`, unit logs |
| Instrumented tests | 39/39 after the fix | `tests-connected-result.xml` |
| Final screenshots | 16 | `real-final/` |
| Cold start | 3041, 3108, 3284, 3310, 5139 ms; median 3284; n=5 | `NFR_MEASUREMENTS.md` |
| Gemini Ask round trip | 861, 913, 2207 ms; median 913; 3/3 HTTP 200 | `NFR_MEASUREMENTS.md` |
| NFR2 action count | Not measured | `NFR_MEASUREMENTS.md` |
| Offline | Explicit error; retry after reconnect; no queue | `NFR_MEASUREMENTS.md` |
| Practice fix | 7 regression tests; screenshot 12 | `tests-unit-practice.txt` |

No SLO or compliance claim is made for NFR1; the slide says “observations only”.

---

## 6. Pending content

- Group number and presentation date: `[PENDING]` on slide 1.
- Peer-review responses and adopted decisions: `[PENDING]` on slide 12.
- Rehearsal and timing: not performed; not claimed.
- Front-sheet signatures/scores: outside the deck.

---

## 7. Secret scan

| Pattern | PPTX | PDF |
|---|---|---|
| `AIza…` | Not found | Not found |
| `provider key=` | Not found | Not found |
| `sk-…` | Not found | Not found |

Only APK SHA-256 digests appear as evidence identifiers; they are not credentials.

---

## 8. Cross-package scan

Scanned: report DOCX, report PDF, deck PPTX, deck PDF, and all `ASM/ASM/*.md` files.

| Package | API-key patterns | Root demo used as final |
|---|---|---|
| Report DOCX | None | No — 0 root-image hashes embedded |
| Report PDF | None | No |
| Presentation PPTX | None | No — 0 root-image hashes embedded |
| Presentation PDF | None | No |
| Markdown sources | No assigned credential; 3 files contain the literal scan string `provider key=` in QA instructions/results, and the evidence note records `length=39` only | No — root 01–22 labelled DEMO / NON-FINAL |

No `AIza…`, `sk-…`, `AKIA…`, `ghp_…`, `xox…` or private-key block matched any binary or Markdown file.

---

## 9. Blockers

Survey responses, presentation date, group number, rehearsal and final reference verification remain outstanding. No app source change, commit or push was made.
