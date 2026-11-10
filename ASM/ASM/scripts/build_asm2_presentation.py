#!/usr/bin/env python3
"""Build the ASM2 draft presentation (PPTX + PDF).

Rerunnable. Uses only real-final images for core evidence; optional captures
appear only on slide 12, labelled supplementary.
"""
from __future__ import annotations

import hashlib
import json
import platform
import sys
from pathlib import Path

from PIL import Image
from pptx import Presentation
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.util import Inches, Pt

SCRIPT = Path(__file__).resolve()
ASM_DIR = SCRIPT.parents[1]
AUDIT = SCRIPT.parents[3]
OUT_DIR = ASM_DIR / "deliverables" / "draft"
PPTX_OUT = OUT_DIR / "BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pptx"
PDF_OUT = OUT_DIR / "BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.pdf"

ASSIGNMENT = Path(r"C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment")
REAL_CANDIDATES = [
    ASSIGNMENT / "ASM" / "ASM" / "evidence" / "pixel-api-35" / "real-final",
    ASM_DIR / "evidence" / "pixel-api-35" / "real-final",
]
UML_CANDIDATES = [
    ASSIGNMENT / "docs" / "uml" / "png",
    AUDIT / "docs" / "uml" / "png",
]

DARK = RGBColor(0x0F, 0x17, 0x2A)
TEAL = RGBColor(0x0F, 0x76, 0x6E)
MUTED = RGBColor(0x64, 0x74, 0x8B)
LIGHT = RGBColor(0xF8, 0xFA, 0xFC)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
AMBER = RGBColor(0xB4, 0x53, 0x09)
GREEN = RGBColor(0x15, 0x80, 0x3D)

DRAFT = "DRAFT — NOT FOR FINAL SUBMISSION"


def first_existing(paths):
    for p in paths:
        if p.exists():
            return p
    raise FileNotFoundError("none of: " + ", ".join(str(p) for p in paths))


REAL = first_existing(REAL_CANDIDATES)
UML = first_existing(UML_CANDIDATES)


def img(name: str) -> Path | None:
    p = REAL / name
    return p if p.exists() else None


def add_footer(slide, number: int) -> None:
    box = slide.shapes.add_textbox(Inches(0.45), Inches(7.13), Inches(7.2), Inches(0.24))
    p = box.text_frame.paragraphs[0]
    r = p.add_run()
    r.text = DRAFT
    r.font.size = Pt(8.5)
    r.font.bold = True
    r.font.color.rgb = AMBER
    r.font.name = "Calibri"
    num = slide.shapes.add_textbox(Inches(11.9), Inches(7.13), Inches(1.0), Inches(0.24))
    p = num.text_frame.paragraphs[0]
    p.alignment = PP_ALIGN.RIGHT
    r = p.add_run()
    r.text = f"{number} / 12"
    r.font.size = Pt(8.5)
    r.font.color.rgb = MUTED


def add_title(slide, title: str, kicker: str = "") -> None:
    if kicker:
        k = slide.shapes.add_textbox(Inches(0.55), Inches(0.32), Inches(12.2), Inches(0.3))
        p = k.text_frame.paragraphs[0]
        r = p.add_run()
        r.text = kicker.upper()
        r.font.size = Pt(11)
        r.font.bold = True
        r.font.color.rgb = TEAL
        r.font.name = "Calibri"
    box = slide.shapes.add_textbox(Inches(0.55), Inches(0.62), Inches(12.2), Inches(0.85))
    p = box.text_frame.paragraphs[0]
    r = p.add_run()
    r.text = title
    r.font.size = Pt(30)
    r.font.bold = True
    r.font.color.rgb = DARK
    r.font.name = "Calibri"
    line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.58), Inches(1.5), Inches(1.5), Inches(0.05))
    line.fill.solid()
    line.fill.fore_color.rgb = TEAL
    line.line.fill.background()


def add_bullets(slide, items, left=0.65, top=1.75, width=6.6, height=4.9, size=17):
    box = slide.shapes.add_textbox(Inches(left), Inches(top), Inches(width), Inches(height))
    tf = box.text_frame
    tf.word_wrap = True
    tf.margin_left = 0
    for i, item in enumerate(items):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.space_after = Pt(10)
        p.line_spacing = 1.08
        if isinstance(item, tuple):
            text, level = item
        else:
            text, level = item, 0
        if level:
            p.level = 1
            p.space_before = Pt(2)
        r = p.add_run()
        r.text = ("•  " if level == 0 else "–  ") + text
        r.font.size = Pt(size - (3 if level else 0))
        r.font.color.rgb = MUTED if level else DARK
        r.font.name = "Calibri"
    return box


def add_note(slide, text: str) -> None:
    slide.notes_slide.notes_text_frame.text = text


def add_picture_fit(slide, path: Path, left_in, top_in, max_w_in, max_h_in, border=True):
    with Image.open(path) as im:
        w, h = im.size
    ratio = w / h
    width = max_w_in
    height = width / ratio
    if height > max_h_in:
        height = max_h_in
        width = height * ratio
    left = left_in + (max_w_in - width) / 2
    pic = slide.shapes.add_picture(str(path), Inches(left), Inches(top_in), Inches(width), Inches(height))
    if border:
        pic.line.color.rgb = RGBColor(0xCB, 0xD5, 0xE1)
        pic.line.width = Pt(0.75)
    return pic, height


def add_caption(slide, text, left, top, width, size=9, color=MUTED, bold=False, align=PP_ALIGN.CENTER):
    box = slide.shapes.add_textbox(Inches(left), Inches(top), Inches(width), Inches(0.34))
    p = box.text_frame.paragraphs[0]
    p.alignment = align
    r = p.add_run()
    r.text = text
    r.font.size = Pt(size)
    r.font.color.rgb = color
    r.font.bold = bold
    r.font.name = "Calibri"


def add_shot(slide, name, left, top, max_w=1.85, max_h=3.25, caption=None):
    p = img(name)
    if p is None:
        add_caption(slide, f"[PENDING: {name} missing]", left, top + 1.4, max_w, color=AMBER, bold=True)
        return
    add_picture_fit(slide, p, left, top, max_w, max_h)
    if caption:
        add_caption(slide, caption, left, top + max_h + 0.02, max_w, size=8.5)


def add_stat(slide, value, label, left, top, width=2.2, color=TEAL):
    v = slide.shapes.add_textbox(Inches(left), Inches(top), Inches(width), Inches(0.62))
    p = v.text_frame.paragraphs[0]
    r = p.add_run()
    r.text = value
    r.font.size = Pt(36)
    r.font.bold = True
    r.font.color.rgb = color
    r.font.name = "Calibri"
    lab = slide.shapes.add_textbox(Inches(left), Inches(top + 0.62), Inches(width), Inches(0.5))
    p = lab.text_frame.paragraphs[0]
    r = p.add_run()
    r.text = label
    r.font.size = Pt(11)
    r.font.color.rgb = MUTED
    r.font.name = "Calibri"


def add_table(slide, rows, left, top, width, col_widths, font_size=12):
    shape = slide.shapes.add_table(len(rows), len(rows[0]), Inches(left), Inches(top), Inches(width), Inches(0.35 * len(rows)))
    table = shape.table
    for i, w in enumerate(col_widths):
        table.columns[i].width = Inches(w)
    for ri, row in enumerate(rows):
        for ci, val in enumerate(row):
            cell = table.cell(ri, ci)
            cell.text = ""
            p = cell.text_frame.paragraphs[0]
            r = p.add_run()
            r.text = val
            r.font.size = Pt(font_size)
            r.font.name = "Calibri"
            r.font.bold = ri == 0
            r.font.color.rgb = WHITE if ri == 0 else DARK
            cell.fill.solid()
            cell.fill.fore_color.rgb = TEAL if ri == 0 else (LIGHT if ri % 2 else WHITE)
            cell.margin_left = Inches(0.06)
            cell.margin_right = Inches(0.06)
            cell.margin_top = Inches(0.03)
            cell.margin_bottom = Inches(0.03)
    return table


def build() -> Presentation:
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank = prs.slide_layouts[6]

    # ---------------------------------------------------------------- 1 title
    s = prs.slides.add_slide(blank)
    bg = s.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, prs.slide_width, prs.slide_height)
    bg.fill.solid()
    bg.fill.fore_color.rgb = DARK
    bg.line.fill.background()
    bar = s.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(2.0), Inches(0.09), Inches(2.6))
    bar.fill.solid()
    bar.fill.fore_color.rgb = TEAL
    bar.line.fill.background()
    t = s.shapes.add_textbox(Inches(1.15), Inches(1.85), Inches(11.2), Inches(1.0))
    p = t.text_frame.paragraphs[0]
    r = p.add_run()
    r.text = "AI Study Mentor"
    r.font.size = Pt(48)
    r.font.bold = True
    r.font.color.rgb = WHITE
    r.font.name = "Calibri"
    st = s.shapes.add_textbox(Inches(1.17), Inches(2.85), Inches(10.8), Inches(0.5))
    p = st.text_frame.paragraphs[0]
    r = p.add_run()
    r.text = "Unit 22: Application Development — Assignment Part 2"
    r.font.size = Pt(20)
    r.font.color.rgb = RGBColor(0xCB, 0xD5, 0xE1)
    meta = s.shapes.add_textbox(Inches(1.17), Inches(3.55), Inches(11.0), Inches(1.2))
    tf = meta.text_frame
    for i, line in enumerate([
        "Nguyen Minh Khang (BC00720) · SE08201",
        "Assessor: Mr. Le Nhat Quang",
        "Group number and presentation date [PENDING]",
    ]):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        r = p.add_run()
        r.text = line
        r.font.size = Pt(14)
        r.font.color.rgb = WHITE if i == 0 else RGBColor(0x94, 0xA3, 0xB8)
        r.font.name = "Calibri"
    badge = s.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.17), Inches(5.55), Inches(4.1), Inches(0.45))
    badge.fill.solid()
    badge.fill.fore_color.rgb = AMBER
    badge.line.fill.background()
    p = badge.text_frame.paragraphs[0]
    p.alignment = PP_ALIGN.CENTER
    r = p.add_run()
    r.text = DRAFT
    r.font.size = Pt(12)
    r.font.bold = True
    r.font.color.rgb = WHITE
    add_footer(s, 1)
    add_note(s, "This is an evaluation of a built application against the Part 1 design, not a proposal. Every number on the following slides comes from the report and its evidence set.")

    # ---------------------------------------------------------------- 2 problem
    s = prs.slides.add_slide(blank)
    add_title(s, "The problem: help that is generic, unverified and lost", "Context")
    add_bullets(s, [
        "Students working alone receive explanations that are hard to check and not tuned to their level.",
        "Answers disappear after the session, so progress cannot be seen.",
        "Target users: middle school, high school and university students; BrightPath owns the product.",
        "Constraints: junior team, 14-week MVP, limited budget and an API cost ceiling.",
        ("Design response: local-first storage, a cache in front of the provider, structured answers and a testable domain tier.", 1),
    ], width=6.9)
    add_shot(s, "01-welcome.png", 7.55, 1.75, max_w=1.9, max_h=3.5, caption="real-final/01-welcome.png")
    add_shot(s, "06-ask-gemini-answer.png", 9.75, 1.75, max_w=1.9, max_h=3.5, caption="real-final/06-ask-gemini-answer.png")
    add_caption(s, "FINAL evidence — real Gemini, fixed APK", 10.25, 5.55, 2.6, size=9, color=GREEN, bold=True)
    add_footer(s, 2)
    add_note(s, "Connect each constraint to a design decision shown later: cost ceiling to the cache, solo study to local-first storage, junior team to the test rule.")

    # ---------------------------------------------------------------- 3 requirements
    s = prs.slides.add_slide(blank)
    add_title(s, "Requirements and priorities", "Part 1 baseline")
    rows = [
        ["Band", "Requirements", "Why"],
        ["MVP", "FR1 auth, FR2 onboarding, FR3 text/image ask, FR4 structured answer, FR6 history, FR8 progress", "Core study loop"],
        ["Post-MVP", "FR5 improvement blocks, FR7 practice, FR9 gamification, FR10 notifications", "Scheduled after the loop"],
        ["NFR", "NFR1 responsiveness, NFR2 three actions, NFR3 API 33+, NFR4 encryption, NFR5 offline reading, NFR6 auth hardening", "Quality constraints"],
    ]
    add_table(s, rows, 0.6, 1.85, 12.1, [1.5, 7.3, 3.3], font_size=12)
    add_caption(s, "Deferred items are scheduled work, not failures.", 0.62, 5.05, 10.0, size=12, color=MUTED, align=PP_ALIGN.LEFT)
    add_footer(s, 3)
    add_note(s, "FR5, FR7, FR9 and FR10 follow the core loop by plan. Say explicitly that deferral is not the same as failure, and that the report scores them as partial or unverified.")

    # ---------------------------------------------------------------- 4 solution
    s = prs.slides.add_slide(blank)
    add_title(s, "Proposed solution and the frozen Part 1 design", "Design baseline")
    add_bullets(s, [
        "Kotlin + Jetpack Compose, single activity, one module.",
        "Room for local-first storage; manual dependency graph instead of a framework.",
        "Gemini called through HttpURLConnection and org.json.",
        "Ask path: normalise → exact hash → similarity policy → provider → persist → history.",
        "Cache before AI is the cost-control mechanism the brief requires.",
    ], width=7.3)
    add_stat(s, "4", "layers: ui, domain,\ndata, platform", 8.4, 1.95)
    add_stat(s, "98", "Kotlin files,\n12,103 lines", 10.45, 1.95)
    add_caption(s, "Design decisions D-1…D-12 are the M4 baseline.", 8.4, 4.05, 4.3, size=11, color=MUTED, align=PP_ALIGN.LEFT)
    add_footer(s, 4)
    add_note(s, "The design is frozen before the build; the evaluation asks whether the built artefact matches it. Point out that the cache is a product decision driven by cost, not only a performance trick.")

    # ---------------------------------------------------------------- 5 method
    s = prs.slides.add_slide(blank)
    add_title(s, "Development method and toolchain", "M4")
    add_bullets(s, [
        "One-week increments; Definition of Done per phase.",
        "Rule: every logic change ships with a test; tests never call the real provider.",
        "AGP 9.3.3 · Kotlin 2.2.10 · Compose BOM 2026.02.01 · Room 2.7.2 · KSP 2.3.12.",
        "CI runs ./gradlew test with no API key, so it builds against the offline fake client.",
        "Thirteen PlantUML sources are kept as text and reviewed as diffs.",
        ("Honest record: 10 of 14 sprint entries are empty; 13 commits on the audited branch, single author.", 1),
    ], width=11.6, size=17)
    add_footer(s, 5)
    add_note(s, "State the sprint-record gap yourself before anyone asks. The toolchain evidence is in the build files and the workflow, not in prose.")

    # ---------------------------------------------------------------- 6 architecture
    s = prs.slides.add_slide(blank)
    add_title(s, "As-built architecture and data flow", "P5")
    add_bullets(s, [
        "Four layers: ui, domain, data, platform; one module.",
        "Room schema v1, seven tables: users, profiles, question_answers, quiz_items, xp_events, user_badges, notifications.",
        "Ask flow: normalise → hash → similarity → provider → persist → history.",
        "A cache hit costs no API call; a number or language difference forces a fresh call.",
    ], width=5.4, size=16)
    add_picture_fit(s, UML / "06_component.png", 6.15, 1.8, 6.4, 2.45)
    add_caption(s, "Component view — docs/uml/png/06_component.png", 6.15, 4.3, 6.4, size=9)
    add_picture_fit(s, UML / "09_erd.png", 6.15, 4.75, 6.4, 1.85)
    add_caption(s, "ERD, seven tables — docs/uml/png/09_erd.png", 6.15, 6.66, 6.4, size=9)
    add_footer(s, 6)
    add_note(s, "The diagrams are rendered from text sources, so they can be reviewed as diffs. The ERD shows the seven-table schema referenced in the report.")

    # ---------------------------------------------------------------- 7 demo 1
    s = prs.slides.add_slide(blank)
    add_title(s, "Demo 1: a real question, a real Gemini answer", "P5 · runtime")
    x = 0.6
    for name, cap in [
        ("01-welcome.png", "01 · welcome"),
        ("05-ask-input.png", "05 · question"),
        ("06-ask-gemini-answer.png", "06 · real answer"),
    ]:
        add_shot(s, name, x, 1.75, max_w=2.55, max_h=4.15, caption=cap)
        x += 2.7
    add_bullets(s, [
        "POST …:generateContent → HTTP 200, modelVersion gemini-3.5-flash-lite.",
        "Answer shows steps and is saved to history.",
        "One real response evidences the path; it does not establish answer quality.",
    ], left=8.9, top=1.85, width=4.0, height=4.3, size=13)
    add_footer(s, 7)
    add_note(s, "This is the core loop. Say clearly that one response is a path demonstration, not an accuracy claim.")

    # ---------------------------------------------------------------- 8 demo 2
    s = prs.slides.add_slide(blank)
    add_title(s, "Demo 2: history, detail and progress", "P5 · runtime")
    x = 0.6
    for name, cap in [
        ("07-history-real-question.png", "07 · history"),
        ("08-question-detail.png", "08 · detail"),
        ("09-progress-real.png", "09 · progress"),
    ]:
        add_shot(s, name, x, 1.75, max_w=2.55, max_h=4.15, caption=cap)
        x += 2.7
    add_bullets(s, [
        "Progress observed: 1 question, 15 s, 0%, +7 XP.",
        "Search, bookmark and subject filters were not exercised.",
    ], left=8.9, top=1.85, width=4.0, height=3.2, size=13)
    add_footer(s, 8)
    add_note(s, "The progress numbers come from the real-final capture. Do not claim search or filter behaviour; it was not exercised.")

    # ---------------------------------------------------------------- 9 demo 3
    s = prs.slides.add_slide(blank)
    add_title(s, "Demo 3: practice and corrected feedback", "P5 · M5")
    x = 0.6
    for name, cap in [
        ("10-practice-generate.png", "10 · setup"),
        ("11-practice-question.png", "11 · quiz"),
        ("12-practice-correct-feedback.png", "12 · corrected"),
    ]:
        add_shot(s, name, x, 1.75, max_w=2.55, max_h=4.15, caption=cap)
        x += 2.7
    add_bullets(s, [
        "Letter/number keys now resolve to option text before comparison.",
        "Seven PracticeAnswerTest regressions; screenshot 12 shows “Chính xác!”.",
        "The screenshot used an option-text key; the letter-key path is unit-tested.",
    ], left=8.9, top=1.85, width=4.0, height=4.0, size=13)
    add_footer(s, 9)
    add_note(s, "Be precise about what the screenshot proves. The fix is real and tested, but the letter-key path is covered by tests rather than this capture.")

    # ---------------------------------------------------------------- 10 evidence
    s = prs.slides.add_slide(blank)
    add_title(s, "Evidence and testing", "P5 · P6")
    add_stat(s, "159", "JVM tests, 13 classes, 0 failures", 0.7, 1.75, width=3.6)
    add_stat(s, "39/39", "instrumented tests after the fix", 4.5, 1.75, width=3.6)
    add_stat(s, "16", "real-final screenshots", 8.3, 1.75, width=3.6)
    add_bullets(s, [
        "NFR1 cold start: 3041 / 3108 / 3284 / 3310 / 5139 ms — median 3284, n=5.",
        "Gemini Ask round trip: 861 / 913 / 2207 ms — median 913, 3/3 HTTP 200.",
        "Observations only: no project SLO is defined.",
        "Tests prove logic and data behaviour, not AI accuracy.",
    ], top=3.75, width=11.9, height=2.6, size=14)
    add_footer(s, 10)
    add_note(s, "These are measured observations, not compliance claims. The cold start includes the first-launch outlier; the round trip covers upload, server time and response read.")

    # ---------------------------------------------------------------- 11 evaluation
    s = prs.slides.add_slide(blank)
    add_title(s, "Evaluation against requirements", "P6")
    rows = [
        ["Status", "Requirements", "Basis"],
        ["Met", "FR1, FR4 (one response), FR6 list/detail, FR7 generation/feedback, FR8", "Real-final runtime"],
        ["Partial", "FR2 subjects, FR3 image, FR9 leaderboard, FR10 reminders", "Observed gaps"],
        ["Unverified", "FR5 improvement blocks", "Not exercised"],
        ["NFR", "NFR1 measured observation; NFR3 met; NFR4–NFR6 partial", "NFR2 and history timing pending"],
    ]
    add_table(s, rows, 0.6, 1.8, 12.1, [1.55, 6.55, 4.0], font_size=12)
    add_caption(s, "No row is rounded up; partial stays partial.", 0.62, 5.15, 10.0, size=12, color=MUTED, align=PP_ALIGN.LEFT)
    add_footer(s, 11)
    add_note(s, "Use the report matrix. NFR1 is a measured observation with no threshold; NFR2 action count and history timing remain pending. Offline reading is partial: saved data is readable, new questions fail without a queue.")

    # ---------------------------------------------------------------- 12 review
    s = prs.slides.add_slide(blank)
    add_title(s, "Critical review, feedback and next steps", "M5 · D2")
    add_bullets(s, [
        "Strengths: testable domain tier, disclosed trade-offs, measured cost control.",
        "Weaknesses: onboarding subjects, image lifecycle, inactive abuse filter, subject mapping.",
        "Risks: hallucinated content, cost ceiling, privacy scope, sprint-record gaps.",
        "Peer feedback and decisions: [PENDING: survey responses].",
        "Backlog: 2FA, image lifecycle, offline queue, abuse filter, server-backed leaderboard.",
    ], width=7.4, size=14)
    add_caption(s, "Supplementary evidence — not part of the 16-image final gate", 8.55, 1.72, 4.2, size=9, color=AMBER, bold=True, align=PP_ALIGN.LEFT)
    for i, (name, cap) in enumerate([
        ("offline-01-error.png", "offline error"),
        ("login-02-success.png", "login after logout"),
        ("image-02-answer.png", "image answer"),
    ]):
        left = 8.5 + i * 1.52
        p = REAL / "optional" / name
        if p.exists():
            add_picture_fit(s, p, left, 2.05, 1.4, 2.5, border=True)
            add_caption(s, cap, left, 4.62, 1.4, size=7.5)
        else:
            add_caption(s, "[missing]", left, 2.8, 1.4, size=8, color=AMBER)
    add_footer(s, 12)
    add_note(s, "Close on what is evidenced and what is honestly outstanding. The three thumbnails are supplementary captures for offline recovery, login after logout and the image question; they are not part of the sixteen-image final gate. Survey responses are still pending.")

    return prs


def convert_pdf(pptx_path: Path, pdf_path: Path) -> tuple[bool, str, int | None]:
    try:
        import win32com.client  # type: ignore
    except Exception as exc:
        return False, f"PowerPoint COM unavailable: {exc}", None
    app = None
    pres = None
    try:
        app = win32com.client.DispatchEx("PowerPoint.Application")
        pres = app.Presentations.Open(str(pptx_path), WithWindow=False)
        slides = int(pres.Slides.Count)
        pres.SaveAs(str(pdf_path), 32)  # ppSaveAsPDF
        return True, "Microsoft PowerPoint COM SaveAs(ppSaveAsPDF=32)", slides
    except Exception as exc:
        return False, f"PowerPoint COM export failed: {exc}", None
    finally:
        if pres is not None:
            try:
                pres.Close()
            except Exception:
                pass
        if app is not None:
            try:
                app.Quit()
            except Exception:
                pass


def main() -> int:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    prs = build()
    prs.save(str(PPTX_OUT))
    ok, method, slides = convert_pdf(PPTX_OUT, PDF_OUT)

    used = []
    for slide in prs.slides:
        for shape in slide.shapes:
            if shape.shape_type == 13:  # PICTURE
                try:
                    used.append(Path(shape.image.filename).name if hasattr(shape.image, "filename") and shape.image.filename else None)
                except Exception:
                    pass
    log = {
        "slides": len(prs.slides),
        "pdf_ok": ok,
        "pdf_method": method,
        "pdf_slides": slides,
        "pptx": str(PPTX_OUT),
        "pdf": str(PDF_OUT),
        "python": platform.python_version(),
        "python_pptx": __import__("pptx").__version__,
    }
    (OUT_DIR / "BC00720_KhangNM_ASM2_DRAFT_PRESENTATION.export-log.json").write_text(
        json.dumps(log, indent=2, ensure_ascii=False), encoding="utf-8"
    )
    print(json.dumps(log, indent=2, ensure_ascii=False))
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
