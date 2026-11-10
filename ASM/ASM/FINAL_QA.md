# ASM2 — Final QA Record

**Scope:** `ASM/ASM/ASM2_REPORT_COMPACT.md`, `ASM/ASM/ASM2_REPORT_DRAFT.md`, `ASM/ASM/FINAL_REPORT_PACKAGE.md`, `ASM/ASM/ASM2_SUBMISSION_CHECKLIST.md`, evidence set `ASM/ASM/evidence/`.
**Date:** 2026-11-10.
**Rule applied:** verification and evidence-gate labelling only. No prose was invented, no screenshot was fabricated, and no application source was touched by this worktree. Draft DOCX, PDF and PPTX now exist under `deliverables/draft/` and are labelled DRAFT.

---

## 1. Verdict

| Check | Result |
|---|---|
| Valid UTF-8, strict decode | **PASS** (all four report files) |
| Byte-order mark | None (expected; UTF-8 without BOM) |
| Mojibake / replacement characters | **PASS** — none |
| API keys or secret material | **PASS** — no assignment, no key-shaped token |
| Unfinished-work markers (task markers, filler text) | **PASS** — none |
| Pending tags well-formed | **PASS** — 30 compact, 69 draft, 16 package, 11 checklist; none untagged |
| Prose word count | **2,936** by the documented method (inside 2,500–3,000) |
| Package word-count consistency | **PASS** — five references, all 2,936 |
| Rubric sections P4, M3, P5, M4, P6, M5, D2 | **PASS** — all present in the compact |
| Front-sheet metadata block | **PASS** — student, ID, class, assessor and unit supplied; group/member fields N/A — individual submission; date, scores and signatures marked PENDING |
| Working-source status | **PASS** — compact remains the single report source; DRAFT DOCX/PDF exported from it (`deliverables/draft/`) |
| Reference list | **PASS** — `ASM2_HARVARD_REFERENCES.md`: 17 external entries from Part 1 plus 13 project documents; verification `[PENDING]` |
| Presentation | **PASS (draft)** — 12-slide PPTX and PDF exported from `ASM2_PRESENTATION_OUTLINE.md`; rehearsal `[PENDING]` |
| Evidence paths | **PASS** — all referenced paths exist, including the 16 real-final images |
| Blocker / remaining-input lists | **PASS** — present in all three documents |
| Evidence gate | **PASS** — only `real-final/01–16` cited as final; root 01–22 labelled DEMO / NON-FINAL |
| Application source modified in AUDIT | **None** — zero changes under `AI Study/` |
| Commit / push | **None** |

No blocking defect found. Remaining inputs are catalogued in `ASM2_REPORT_INPUTS.md`: survey export, front-sheet date/scores/signatures, Harvard references, presentation rehearsal, remaining NFR rows and demo-account procedure. Cold start and the Gemini Ask round trip are now measured.

---

## 2. Evidence gate result

| Item | State |
|---|---|
| Demo set (root 02–19, `FakeAiClient`) | **DEMO / NON-FINAL**, retained, not deleted |
| Pre-final real-key set (root 20–22) | **DEMO / NON-FINAL** — predates the fixed build |
| Real-final set | `real-final/01–16` — **FINAL** |
| Build | Practice-fix APK, SHA-256 `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71` |
| Provider | Real Gemini; ask and quiz calls both HTTP 200, `gemini-3.5-flash-lite` |
| Practice-scoring fix | Delivered; normalises letter/number keys to option text; 7 regression tests |
| Corrected-feedback evidence | `real-final/12-practice-correct-feedback.png` — option B correct, “Chính xác!” |
| Unit tests | **159 tests, 13 classes, 0 failures** (7 new `PracticeAnswerTest`) |
| Connected tests | **39/39**, 0 failures; post-fix XML `real-final/tests-connected-result.xml`; `.txt` log `real-final/tests-connected.txt` |
| Final-citable screenshots | 16, listed in §8 |

Everything under root 01–22 is demo archive. Any caption or sentence presenting those images as final evidence is a defect.

---

## 3. Commands and results

### 3.1 Encoding and content scan

```
python qa_checks.py ASM2_REPORT_COMPACT.md ASM2_REPORT_DRAFT.md FINAL_REPORT_PACKAGE.md ASM2_SUBMISSION_CHECKLIST.md
```

```
ASM2_REPORT_COMPACT.md        utf8-strict: OK  mojibake: none  secrets: none  PENDING: 30
ASM2_REPORT_DRAFT.md          utf8-strict: OK  mojibake: none  secrets: none  PENDING: 69
FINAL_REPORT_PACKAGE.md       utf8-strict: OK  mojibake: none  secrets: none  PENDING: 16
ASM2_SUBMISSION_CHECKLIST.md  utf8-strict: OK  mojibake: none  secrets: none  PENDING: 11
PROBLEM COUNT: 0
```

The scan covered Google API key prefixes, OpenAI-style keys, AWS access-key identifiers, GitHub tokens, Slack tokens, private-key blocks, assigned `GEMINI_API_KEY` values, the replacement character and common mojibake sequences.

The APK SHA-256 digests (`b33d9241…`, `0ac0c05a…`, `c7c8f9e2…`) are build artefact identifiers, deliberately retained as evidence, not secrets.

### 3.2 Prose word count

Method: exclude table rows, `Table n:` captions, `Figure n:` callouts, the reference list and the appendix index; count remaining prose including headings and the status block.

```
node count_words.js ASM2_REPORT_COMPACT.md
prose words (headings included): 2936
table rows skipped: 107 | figure lines skipped: 15
```

Cross-check without heading lines:

```
prose excl. headings (front matter included): 2791
prose excl. headings, body only (from Section 1): 2609
```

All three figures fall inside the 2,500–3,000 band used for Activity 2. `FINAL_REPORT_PACKAGE.md` states 2,936 in all five places; a grep for 2,891, 2,876 and 2,882 returns zero hits.

### 3.3 Evidence paths and rubric coverage

```
real-final png: 16
manifest: True        (real-final/MANIFEST_REAL.txt)
tests xml: True       (real-final/tests-connected-result.xml)
tests txt: True       (real-final/tests-connected.txt)
unit focused: True    (real-final/tests-unit-practice.txt)
logcats: 2            (ask + quiz)
```

The earlier 30-path and 7-path checks still pass; the real-final paths were verified individually.

```
SECTIONS in ASM2_REPORT_COMPACT.md
  P4 present   M3 present   P5 present   M4 present
  P6 present   M5 present   D2 present
```

### 3.4 Stale-claim scan

Searched all four documents for the superseded final-set claims (`only 20`, `20–21 final`, quiz fix missing, real-key coverage missing), the superseded word counts and `152 tests` used as a current figure. Only one hit remains: the package's generic instruction that `"Implemented, unverified" or "demo-only"` is used where no final run exists. That is correct guidance, not a stale claim.

### 3.5 Workspace state

```
git status --porcelain
?? .commandcode/
?? ASM/

git status --porcelain -- "AI Study"   -> 0 entries
```

No application source change in AUDIT. The practice fix exists only as uncommitted changes in the sibling `Assigment` worktree (`PracticeViewModel.kt`, `PracticeScreen.kt`, new `PracticeAnswerTest.kt`), which is what the real-final evidence was captured against.

---

## 4. Encoding repair history (disclosed)

Earlier in the compact pass, a PowerShell `Get-Content -Raw` / `WriteAllText` round-trip re-encoded both Markdown files through the system ANSI code page, producing mojibake. It was repaired with a Python UTF-8-only reverse transform and re-verified after every subsequent edit. Current scan: zero problems. Future edits must use a UTF-8-aware editor or Python.

---

## 5. Package consistency

| Claim in the package | Verified value | Status |
|---|---|---|
| Export source is the compact | `ASM2_REPORT_COMPACT.md` named as source of truth and in the deliverable table | Match |
| Body length | 2,936 words, exclusion method stated | Match |
| Figures | Six body figures; Figures 4–6 are `real-final/` captures | Match |
| Screenshot appendix | 16 final in `real-final/`; root 01–22 demo archive | Match |
| Tables | Nine numbered tables in the compact | Match |
| Instrumented result | 39/39 post-fix, XML and log retained | Match |
| JVM result | 159 tests, 13 classes, 0 failures | Match |
| Real provider | Ask and quiz calls verified, HTTP 200 | Match |
| Practice fix | 7 regression tests; screenshot 12 | Match |
| Draft DOCX/PDF and PPTX/PDF exist | `deliverables/draft/`; final export gated on the pending inputs | Match |

---

## 6. Handoff checklist

Export is gated for the final artefacts; DRAFT DOCX/PDF/PPTX/PDF already exist under `deliverables/draft/`.

| # | Input | State | Blocks |
|---|---|---|---|
| 1 | Front sheet: submission date, final scores, signatures | Student/ID/class/assessor supplied; group/member fields N/A — individual submission; date, scores and signatures `[PENDING]` | Cover page |
| 2 | Peer-review participants and real responses | `[PENDING]` | Sections 2.3–2.5, 6.1, Appendices A–B |
| 3 | Reference list with verified Harvard entries | `[PENDING]` | Section 10 |
| 4 | Presentation rehearsal | Draft 12-slide deck exists; rehearsal `[PENDING]` | Activity 2 delivery |
| 5 | NFR measurements: history open and action count | Cold start and Ask round trip measured; history and action count `[PENDING]` | NFR2, Appendix F remainder |
| 6 | Demo-account procedure (user guide is supplied) | `[PENDING]` | P5 support documentation |
| 7 | Tutor decision on the word limit (`DEC-01`) | `[PENDING]` | If the 2,000–2,500 limit governs, about 400 words must be cut |
| 8 | Pull-request history check on the remote | `[PENDING]` | One M4 sentence |

Optional, non-blocking evidence gaps (already stated as limitations):

| # | Optional capture | Note |
|---|---|---|
| 9 | Login after logout | Supplementary capture `optional/login-01…03`; not part of the final gate |
| 10 | Image/camera question flow | Supplementary capture `optional/image-01…02`; redisplay still missing |
| 11 | OS-level notification permission | In-app list was exercised, push was not |
| 12 | Formula-rendering and loading-state captures | Observed answers are plain text; these captures are still missing (cold start and Ask timing are now measured) |

When items 1–4 and 6 are complete, follow `FINAL_REPORT_PACKAGE.md` §6–§7 for export and verification.

---

## 7. Residual risks

| Risk | Impact | Mitigation |
|---|---|---|
| A demo screenshot leaks into the body or a caption omits its status | Presents simulated/pre-final output as real evidence | Export check §7.5; Appendix D status column; honesty check 4 |
| One real answer and one real quiz mistaken for demonstrated quality | Over-claiming AI accuracy | Compact states quality is not established; no accuracy claim appears |
| Word's counter includes table text | Reported figure may look inconsistent with the exclusion rule | State the counting method; the compact and package both do |
| Editing with PowerShell text round-trips | Encoding regression | Use Python or a UTF-8 editor; re-run §3.1 |
| New evidence lands without a manifest update | Path drift and stale captions | Update `MANIFEST_REAL.txt`; re-run the path check |

---

## 8. Exact final status and citable evidence

**Report:** `ASM2_REPORT_COMPACT.md` — 2,936 prose words, English only, all seven criteria covered, front-sheet metadata block included, evidence gate applied.
**Inputs register:** `ASM2_REPORT_INPUTS.md` — the exact remaining fields, with no fabricated values.
**Export plan:** `FINAL_REPORT_PACKAGE.md` — six body figures, nine tables, appendices A–F, gated export.
**Evidence pack:** `ASM2_REPORT_DRAFT.md` plus `ASM/ASM/evidence/`.

Final-citable screenshots (all under `ASM/ASM/evidence/pixel-api-35/real-final/`):

```
01-welcome.png
02-register-filled.png
03-onboarding-step1.png
04-onboarding-step2.png
05-ask-input.png
06-ask-gemini-answer.png
07-history-real-question.png
08-question-detail.png
09-progress-real.png
10-practice-generate.png
11-practice-question.png
12-practice-correct-feedback.png
13-profile.png
14-notifications.png
15-logout-confirm.png
16-logged-out-welcome.png
```

Supporting non-image evidence:

```
real-final/MANIFEST_REAL.txt
real-final/06-gemini-network-logcat.txt
real-final/11-gemini-quiz-logcat.txt
real-final/tests-connected.txt
real-final/tests-connected-result.xml
real-final/tests-unit-practice.txt
```

**Status:** QA complete; working source ready; evidence gate applied; export blocked on §6 items 1–6. No commit and no push were performed.
