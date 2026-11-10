# ASM2 — Control Pack QA

**Scope:** all Markdown documents in `ASM/ASM/` and the draft binaries in `ASM/ASM/deliverables/draft/`.
**Date:** 2026-11-10.
**Environment:** Windows 11, Python 3.14.5, PowerShell 5.1.
**Rule:** read-only checks. No app source was modified, and no commit or push was performed.

> **Follow-up (2026-11-10):** rows below describe the earlier snapshot. Current stale-claim corrections, binary inspection and the open gates are recorded in `REPORT_FINAL_QA.md`.

**What this record does not cover:** the software ZIP and packaged APK now exist in the Assigment worktree (`ASM2_Software_Package.zip`, recorded in `PACKAGE_QA.md`). They are not present in AUDIT, so ZIP inspection, APK install and packaged-APK key scanning remain `PENDING` in `PACKAGE_HANDOVER_CHECKLIST.md` and `PACKAGE_REVIEW_TEMPLATE.md`.

---

## 1. Files checked

| Set | Count | Detail |
|---|---:|---|
| Markdown documents | 18 | 15 existing plus `PACKAGE_HANDOVER_CHECKLIST.md`, `PACKAGE_REVIEW_TEMPLATE.md` and `CONTROL_PACK_QA.md` |
| Draft binaries | 7 | report DOCX/PDF/export logs, deck PPTX/PDF/export log |
| Evidence images | 46 | 16 `real-final` + 22 root demo + 8 optional |

The three new package documents are included in the scan below.

---

## 2. UTF-8 and encoding

Method: strict UTF-8 decode on the raw bytes; count U+FFFD replacement characters and common mojibake sequences; record a byte-order mark when present.

| Result | Count |
|---|---:|
| Strict UTF-8 decode OK | 18 / 18 |
| Decode failures | 0 |
| Replacement characters | 0 |
| Mojibake sequences | 0 |
| UTF-8 BOM present | 1 — `ASM2_REPORT_INPUTS.md` |

`ASM2_REPORT_INPUTS.md` carries a BOM. It still decodes as valid UTF-8 and contains no replacement characters; this is an observation, not a failure, and is not changed by this task. The report source `ASM2_REPORT_COMPACT.md` has no BOM.

DOCX and PPTX parts were also decoded: `xml_repl=0` for both packages.

---

## 3. Secret-pattern scan

Patterns: Google `AIza` key shape, assigned `GEMINI_API_KEY`, OpenAI `sk-`, AWS `AKIA`, GitHub `ghp_/gho_/ghs_/ghu_/ghr_`, Slack `xox[baprs]-`, private-key block.

| Surface | Key-shaped match | Assigned value | Other token patterns |
|---|---|---|---|
| 18 Markdown files | 0 | 0 | 0 |
| Report DOCX XML | 0 | 0 | 0 |
| Report PDF bytes | 0 | 0 | 0 |
| Deck PPTX XML | 0 | 0 | 0 |
| Deck PDF bytes | 0 | 0 | 0 |
| Export logs (TXT/JSON) | 0 | 0 | 0 |

**Literal mentions, not credentials:** the string `AIza` appears in six documents as part of a scan instruction, result or checklist — `DRAFT_EXPORT_QA.md`, `FINAL_REPORT_PACKAGE.md`, `PRESENTATION_DRAFT_QA.md`, `PACKAGE_HANDOVER_CHECKLIST.md`, `PACKAGE_REVIEW_TEMPLATE.md` and `CONTROL_PACK_QA.md`. None is a 39-character key value, and none is assigned to `GEMINI_API_KEY`.

**Method limitation:** PDF checks are raw-byte scans. PDF text streams are usually compressed, so this is a secondary check. The DOCX/PPTX XML scan is authoritative for document text.

---

## 4. Draft binary inspection

| Binary | Pages / media | Real-final matches | Root demo matches | XML replacement chars | Secrets |
|---|---|---:|---:|---:|---|
| `BC00720_KhangNM_ASM2_DRAFT_REPORT.docx` | 20 media | **16 unique** | **0** | 0 | none |
| `BC00720_KhangNM_ASM2_DRAFT_REPORT.pdf` | 23 pages | n/a | n/a | n/a | none |
| `BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pptx` | 14 media | **9 unique** | **0** | 0 | none |
| `BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pdf` | 12 pages | n/a | n/a | n/a | none |

Media hashes were computed from the embedded files and matched against the source PNG hashes.

- The report DOCX embeds all 16 final images exactly once and no root demo image.
- The deck PPTX embeds 9 final images and no root demo image; the remaining media are 2 UML PNGs and 3 optional captures, consistent with `PRESENTATION_DRAFT_QA.md`.
- No unmatched media hash was found.

---

## 5. Final-evidence gate

| Check | Result |
|---|---|
| `real-final/*.png` count | **16** |
| Exact 16-name set | **MATCH** — missing 0, extra 0 |
| Root demo PNGs | 22, kept as DEMO / NON-FINAL |
| Optional PNGs | 8, under `real-final/optional/` |
| Root demo image cited as final in Markdown | **0 hits** |
| Report DOCX root demo hash | 0 |
| Deck PPTX root demo hash | 0 |

The 16 final names match `FINAL_QA.md` §8 and `FINAL_REPORT_PACKAGE.md` §2.2. Optional captures remain supplementary.

---

## 6. Path checks

19 required paths were tested. All exist:

| Path group | Result |
|---|---|
| Report sources (`ASM2_REPORT_COMPACT.md`, `ASM2_REPORT_DRAFT.md`, `FINAL_REPORT_PACKAGE.md`, `ASM2_SUBMISSION_CHECKLIST.md`) | 4 / 4 |
| Support and input docs (`NFR_MEASUREMENTS.md`, `USER_GUIDE.md`, `PEER_REVIEW_FORM.md`, `ASM2_HARVARD_REFERENCES.md`, `ASM2_PRESENTATION_OUTLINE.md`) | 5 / 5 |
| Build scripts | 2 / 2 |
| Draft binaries | 4 / 4 |
| Evidence manifests and test logs | 4 / 4 |

`ROOT/DEMO IMAGE CITATIONS IN DOCS`: 0 hits. No document references a root demo path as if it were final.

---

## 7. Local APK scan (not the packaged APK)

The AUDIT tree contains a local debug build at `AI Study/app/build/outputs/apk/debug/app-debug.apk`. It was scanned as a read-only proxy; it is **not** the APK being packaged in the Assigment worktree.

| Check | Observed |
|---|---|
| Size | 35,157,795 bytes |
| SHA-256 | `b33d924197c1773387f18aa65557e95155a4d4116fadc1a68f6d4d8664027ec1` |
| ZIP entries | 170 |
| `AIza` key shape | 0 |
| `AIza` byte sequence | 0 |
| `GEMINI_API_KEY` | 2 occurrences — field name only, no value |
| `local.properties` in APK | absent |
| `ai.properties` in APK | absent |
| `.jks` / `.keystore` in APK | absent |

The hash is the pre-fix build digest, not the fixed build `c7c8f9e2…`. The packaged APK must be hashed and scanned again from the actual archive.

---

## 8. Stale-claim fixes

Only the requested categories were changed: "no DOCX/PDF", "no deck", "no timings", and group-submission wording.

| File | Before | After | Reason |
|---|---|---|---|
| `FINAL_QA.md` | `No DOCX/PDF yet` / package planning only | Draft DOCX/PDF and PPTX/PDF exist under `deliverables/draft/` | Draft exports now exist |
| `FINAL_QA.md` | front-sheet "group, date, scores" PENDING | group/member fields N/A — individual submission; date, scores and signatures PENDING | Group fields are not pending |
| `FINAL_QA.md` | remaining input "front-sheet group/date/scores/signatures" | "front-sheet date/scores/signatures" | Same |
| `FINAL_REPORT_PACKAGE.md` | `Group report` deliverable | `Report (individual submission)` | Individual submission |
| `FINAL_REPORT_PACKAGE.md` | "The report is a *group* submission…" | "The report is an individual submission… group and member fields are N/A" | Contradicted the front sheet |
| `FINAL_REPORT_PACKAGE.md` | "Presentation deck and rehearsal" as a remaining input | "Presentation rehearsal and live demo (12-slide DRAFT deck and PDF supplied)" | Deck exists |
| `ASM2_SUBMISSION_CHECKLIST.md` | `Presentation not produced` | Presentation rehearsal and live demo not completed (DRAFT deck and PDF exist) | Deck exists |
| `ASM2_SUBMISSION_CHECKLIST.md` | `[PENDING: group allocation, member codes, …]` | `[PENDING: submission date, scores, signatures; group/member fields are N/A …]` | Individual submission |
| `ASM2_EVIDENCE_CHECKLIST.md` | front sheet "(group, names, codes, class, assessor)" | student name and code, class, assessor; group/member fields N/A | Same |
| `ASM2_AUDIT_PLAN.md` | front sheet 2 (group), group number and member codes PENDING | student name and code; group/member fields N/A | Same |
| `ASM2_REPORT_DRAFT.md` | "group report" presented as the applied format | brief says "group report"; this submission is individual, group fields N/A | Keeps the verified brief quote without asserting a group submission |
| `ASM2_REPORT_DRAFT.md` | NFR1 "No timings captured" | No UI-response timing captured; cold start and Ask round trip measured but do not test NFR1 | Timings now exist, but not for NFR1 |
| `ASM2_REPORT_COMPACT.md` | front sheet "group, members, class, assessor…" | submission date, scores, signatures; group fields N/A | Individual submission |
| `ASM2_REPORT_COMPACT.md` | "NFR1 and NFR2 measurements" PENDING | NFR1 UI-response timing, NFR2 action count and remaining Appendix F rows | Two metrics are now measured |
| `ASM2_REPORT_COMPACT.md` | "Presentation deck" PENDING | Presentation rehearsal (DRAFT deck and PDF exist) | Deck exists |

Kept unchanged because they are accurate:

- `DRAFT_EXPORT_QA.md`: "No DOCX/PDF was produced **for final submission**" — true; only drafts exist.
- `ASM2_REPORT_COMPACT.md` line 224: NFR2 action count "not measured" — still true.
- `PRESENTATION_DRAFT_QA.md`: rehearsal "not performed" — still true.
- `ASM2_AUDIT_PLAN.md` NFR1/NFR2 "Not tested" — still true for those requirements.

---

## 9. Residual inconsistencies

| Item | State | Action |
|---|---|---|
| `ASM2_PRESENTATION_OUTLINE.md` and the built deck slide 1 say "Group number and presentation date `[PENDING]`" | Group number is N/A — individual submission; presentation date is still pending | Not changed in this task; the PPTX is a built artefact and would need a rebuild. Recorded for the coordinator. |
| `ASM2_REPORT_COMPACT.md` still lists "User guide and demo-account procedure" as one pending row | User guide exists; demo-account procedure does not | Outside the requested stale-claim categories; left unchanged. |
| `ASM2_REPORT_INPUTS.md` has a UTF-8 BOM | Valid UTF-8 | No action; noted for future scripting. |
| Survey, consent, references, rehearsal | No real data | Remain `PENDING`. Nothing was invented. |

---

## 10. Gate summary

| Gate | Result |
|---|---|
| UTF-8 strict decode (18 docs) | **PASS** |
| Replacement/mojibake | **PASS** — 0 |
| Secret patterns in docs and draft binaries | **PASS** — no credential |
| Final 16-image gate | **PASS** — exact set, 0 demo leakage |
| Draft binary media integrity | **PASS** — 16/16 report, 9 final deck, 0 root demo |
| Required path checks | **PASS** — 19/19 |
| Stale-claim scan | **PASS** — requested categories fixed; remaining hits are accurate |
| Source-only ZIP inspection | `PENDING` — archive is not in AUDIT |
| Packaged APK hash, key scan, install | `PENDING` — APK is not in AUDIT |
| Branch handover to `Assignment_Report` | `PENDING` |

**Overall:** documentation and draft-binary checks pass. Package acceptance stays `PENDING` until the real archive and APK are copied in and reviewed with `PACKAGE_REVIEW_TEMPLATE.md`.

**Pending non-package gates:** survey responses and consent, Harvard reference verification, presentation rehearsal, remaining NFR rows (history open, action count, offline timing) and the demo-account procedure.
