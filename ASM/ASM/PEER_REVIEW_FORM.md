# ASM2 — Peer Review Questionnaire (Google Form)

**Purpose:** P4 (peer review of problem definition, proposed solution and development strategy) + M3 (interpret peer feedback).
**Instrument:** Google Forms questionnaire, 16 items.
**Status:** **DRAFT — unpublished. No responses exist.**
**Form owner account:** `nguyenminhkhang1128@gmail.com` (Chrome profile "Liên Minh").
**Recorded:** 2026-11-09.

---

## 1. Form identity

| Item | Value |
|---|---|
| Title | `AI Study Mentor – Peer Review of Requirements and Proposed Solution` |
| Drive file name | `AI Study Mentor – Peer Review Survey` |
| Edit URL | https://docs.google.com/forms/d/16Hd08rqT475PtOu46Kmxi554dX3TwuaiSb9xiNZ_WmQ/edit |
| Preview URL | https://docs.google.com/forms/d/16Hd08rqT475PtOu46Kmxi554dX3TwuaiSb9xiNZ_WmQ/preview |
| Respondent URL | not issued — the form was never published |
| Draft evidence | Editor banner `Đã lưu tất cả thay đổi trong Drive`; header button still reads `Xuất bản` (Publish), not the published state |
| Draft evidence (preview) | Preview header shows `Chưa được xuất bản` and `Biểu mẫu này không chấp nhận lời phản hồi` |

---

## 2. Structure (16 items, all required)

Order is the order in the form.

| # | Item | Type | Required | Detail |
|---|---|---|---|---|
| 1 | Participant full name | Short answer | Yes | Was a multiple-choice item with a single dummy option; corrected to short answer |
| 2 | Role | Multiple choice | Yes | Options: Student · Developer · Teacher/Educator · Other |
| 3 | Job description / study background | Paragraph | Yes | — |
| 4 | Review date/context | Short answer | Yes | — |
| 5 | Q1 The learning problems that AI Study Mentor aims to solve are clearly defined. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 6 | Q2 The target users (middle school, high school, and university students) are clearly identified. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 7 | Q3 The proposed user requirements reflect students' actual learning needs. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 8 | Q4 The proposed AI Study Mentor application is suitable for addressing the identified learning problems. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 9 | Q5 The AI question-and-answer feature is useful for supporting students' learning. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 10 | Q6 The question history and progress tracking features are useful for students. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 11 | Q7 The selected development tools are appropriate for building the application. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 12 | Q8 The selected software development methodology is suitable for this project. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 13 | Q9 The proposed development plan is realistic within the 14-week timeline and limited budget. | Linear scale 1–5 | Yes | 1 = Strongly disagree · 5 = Strongly agree |
| 14 | Q10 What important user requirements or learning problems are missing from the proposed application? | Paragraph | Yes | — |
| 15 | Q11 What weaknesses or risks do you identify in the proposed solution or development strategy? | Paragraph | Yes | — |
| 16 | Q12 What improvements would you suggest for the AI Study Mentor application or its development plan? | Paragraph | Yes | — |

**Question IDs.** The `Q1`…`Q12` prefixes are kept inside the on-screen question text so that a response can be traced back to a Q-ID without a lookup table.

**Form title and description** were left as they were before this task:

> This peer-review survey supports Unit 22 Assignment Part 2 (P4 and M3). It evaluates the problem definition, user requirements, proposed AI Study Mentor solution, and development strategy. Please answer based on the description provided and your role. Responses will be analysed in aggregate for the academic report.

---

## 3. Verification performed

| Check | Method | Result |
|---|---|---|
| 16 items present, in the order above | Read-only preview of the form | Pass |
| Scale labels on all nine Likert items | Counted rendered labels in preview: 9 × `1 = Strongly disagree`, 9 × `5 = Strongly agree` | Pass |
| Five-point scale on all nine Likert items | Radio buttons counted in preview: 45 (9 × 5) | Pass |
| Role options | Preview renders Student / Developer / Teacher/Educator / Other | Pass |
| Required flags | Every item shows the required marker (`Câu hỏi bắt buộc`) in preview | Pass |
| Paragraph vs short answer | Preview renders multi-line boxes for Job description and Q10–Q12, single-line for Participant full name and Review date | Pass |
| Unpublished | Preview header `Chưa được xuất bản`; editor header still offers `Xuất bản`; no respondent link issued | Pass |
| No test/synthetic responses | Responses tab not populated; preview "not accepting responses" | Pass |

---

## 4. Responses

**None.** No invitations have been sent, no links have been distributed and no responses exist. Nothing in this file is a participant answer, and no respondent identity, score or comment has been recorded here.

When real responses arrive, record them in the report's Appendix B (participant table + one row per participant per question), not in this file.

---

## 5. Tooling note (why automation looked different)

Browser automation through the DevTools protocol was not usable on the signed-in profile: Chrome 136+ refuses `--remote-debugging-port` when the user data directory is the default one, and copying the profile to another path breaks cookie decryption because Chrome's app-bound cookie encryption is bound to the profile path (verified: a copy of the signed-in profile loads 0 Google auth cookies). `--load-extension` is also ignored on the current stable build, so a bridge extension was not an option.

The form was therefore edited through **Windows UI Automation** driving the same, already signed-in Chrome window (accessibility tree: question titles, type selector, options, scale labels, required toggle). No credentials were entered, no second form was created, and nothing was published.

---

## 6. Next step

1. Recruit the reviewers (minimum three: at least one student outside the team and one person with development experience — see `ASM2_AUDIT_PLAN.md` §6).
2. Publish the form and send the respondent link, or share the edit link for a supervised session.
3. Record responses verbatim in Appendix B, then compute median and range per Likert item (n < 5 → no percentages).
4. Feed the results into report §1.3–1.4, §5.1–5.3 and the FB decision table.
