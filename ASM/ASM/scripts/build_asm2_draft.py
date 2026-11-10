#!/usr/bin/env python3
"""Build the ASM2 draft report DOCX (and PDF via Word COM) from the compact source.

Rerunnable: overwrites outputs in ASM/ASM/deliverables/draft/.
No app source is read or modified.
"""
from __future__ import annotations

import datetime as _dt
import hashlib
import json
import os
import platform
import re
import subprocess
import sys
from pathlib import Path

from docx import Document
from docx.enum.section import WD_ORIENT, WD_SECTION
from docx.enum.style import WD_STYLE_TYPE
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor

SCRIPT = Path(__file__).resolve()
ASM_DIR = SCRIPT.parents[1]          # .../ASM/ASM
AUDIT = SCRIPT.parents[3]            # .../AUDIT
COMPACT = ASM_DIR / "ASM2_REPORT_COMPACT.md"
OUT_DIR = ASM_DIR / "deliverables" / "draft"
DOCX_OUT = OUT_DIR / "BC00720_KhangNM_ASM2_DRAFT_REPORT.docx"
PDF_OUT = OUT_DIR / "BC00720_KhangNM_ASM2_DRAFT_REPORT.pdf"
LOG_OUT = OUT_DIR / "BC00720_KhangNM_ASM2_DRAFT_REPORT.export-log.json"
LOG_TXT = OUT_DIR / "BC00720_KhangNM_ASM2_DRAFT_REPORT.export-log.txt"

ASSIGNMENT = Path(r"C:\Users\ADMIN\orca\workspaces\AndroidStudio\Assigment")
TEMPLATE_CANDIDATES = [
    Path(r"C:\Users\ADMIN\orca\AndroidStudio\ASM\ASM\FRONTSHEET ASM FINAL REPORT (GROUP).docx"),
    AUDIT / "ASM-20260928T082606Z-1-001" / "ASM" / "FRONTSHEET ASM FINAL REPORT (GROUP).docx",
]
UML_DIRS = [
    ASSIGNMENT / "docs" / "uml" / "png",
    AUDIT / "docs" / "uml" / "png",
]
REAL_FINAL_DIRS = [
    ASSIGNMENT / "ASM" / "ASM" / "evidence" / "pixel-api-35" / "real-final",
    AUDIT / "ASM" / "ASM" / "evidence" / "pixel-api-35" / "real-final",
]

META = {
    "student": "Nguyen Minh Khang",
    "student_id": "BC00720",
    "class": "SE08201",
    "assessor": "Mr. Le Nhat Quang",
    "unit": "Unit 22: Application Development",
    "qualification": "Pearson BTEC Level 5 Higher National Diploma in Computing",
}

PENDING = "[PENDING]"
DRAFT_LABEL = "DRAFT — NOT FOR FINAL SUBMISSION"


def first_existing(paths: list[Path]) -> Path | None:
    for p in paths:
        if p.exists():
            return p
    return None


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            h.update(chunk)
    return h.hexdigest()


# --------------------------------------------------------------------------- docx helpers

def set_cell(cell, text: str, bold: bool = False, size: int = 9) -> None:
    cell.text = ""
    p = cell.paragraphs[0]
    run = p.add_run(text)
    run.bold = bold
    run.font.name = "Calibri"
    run.font.size = Pt(size)


def set_header(section, text: str) -> None:
    header = section.header
    header.is_linked_to_previous = False
    p = header.paragraphs[0] if header.paragraphs else header.add_paragraph()
    p.text = ""
    run = p.add_run(text)
    run.font.name = "Calibri"
    run.font.size = Pt(9)
    run.bold = True
    run.font.color.rgb = RGBColor(0xC0, 0x00, 0x00)
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER


def add_page_number_footer(section) -> None:
    footer = section.footer
    footer.is_linked_to_previous = False
    p = footer.paragraphs[0] if footer.paragraphs else footer.add_paragraph()
    p.text = ""
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run("Page ")
    run.font.name = "Calibri"
    run.font.size = Pt(9)
    _add_field(p, "PAGE")
    run2 = p.add_run(" of ")
    run2.font.name = "Calibri"
    run2.font.size = Pt(9)
    _add_field(p, "SECTIONPAGES")


def _add_field(paragraph, instr: str) -> None:
    run = paragraph.add_run()
    fld_begin = OxmlElement("w:fldChar")
    fld_begin.set(qn("w:fldCharType"), "begin")
    instr_el = OxmlElement("w:instrText")
    instr_el.set(qn("xml:space"), "preserve")
    instr_el.text = f" {instr} "
    fld_sep = OxmlElement("w:fldChar")
    fld_sep.set(qn("w:fldCharType"), "separate")
    text = OxmlElement("w:t")
    text.text = "1"
    fld_end = OxmlElement("w:fldChar")
    fld_end.set(qn("w:fldCharType"), "end")
    run._r.append(fld_begin)
    run._r.append(instr_el)
    run._r.append(fld_sep)
    run._r.append(text)
    run._r.append(fld_end)


def ensure_style(doc: Document, name: str, style_type=WD_STYLE_TYPE.PARAGRAPH):
    try:
        return doc.styles[name]
    except KeyError:
        return doc.styles.add_style(name, style_type)


def ensure_table_borders(table) -> None:
    tbl = table._tbl
    tblPr = tbl.tblPr
    borders = OxmlElement("w:tblBorders")
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        el = OxmlElement(f"w:{edge}")
        el.set(qn("w:val"), "single")
        el.set(qn("w:sz"), "4")
        el.set(qn("w:space"), "0")
        el.set(qn("w:color"), "808080")
        borders.append(el)
    tblPr.append(borders)


def set_base_styles(doc: Document) -> None:
    styles = doc.styles
    normal = styles["Normal"]
    normal.font.name = "Calibri"
    normal.font.size = Pt(12)
    normal.paragraph_format.line_spacing = 1.5
    normal.paragraph_format.space_after = Pt(6)
    # East Asian font fallback so Word keeps Calibri.
    if normal.element.rPr is not None and normal.element.rPr.rFonts is not None:
        normal.element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")

    for name, size in (("Heading 1", 16), ("Heading 2", 14), ("Heading 3", 12)):
        st = ensure_style(doc, name)
        st.font.name = "Calibri"
        st.font.size = Pt(size)
        st.font.bold = True
        st.font.color.rgb = RGBColor(0x1F, 0x1F, 0x1F)
        st.paragraph_format.space_before = Pt(12)
        st.paragraph_format.space_after = Pt(6)
        st.paragraph_format.line_spacing = 1.15

    cap = ensure_style(doc, "Caption")
    cap.font.name = "Calibri"
    cap.font.size = Pt(10)
    cap.font.italic = True

    ensure_style(doc, "List Bullet")
    ensure_style(doc, "List Number")


def setup_section(section, first_page_number: bool = False) -> None:
    section.orientation = WD_ORIENT.PORTRAIT
    section.page_width = Cm(21.0)
    section.page_height = Cm(29.7)
    section.left_margin = Cm(1.25)
    section.right_margin = Cm(1.0)
    section.top_margin = Cm(1.0)
    section.bottom_margin = Cm(1.0)
    if first_page_number:
        sectPr = section._sectPr
        pgNumType = sectPr.find(qn("w:pgNumType"))
        if pgNumType is None:
            pgNumType = OxmlElement("w:pgNumType")
            sectPr.append(pgNumType)
        pgNumType.set(qn("w:start"), "1")


def fill_front_sheet(doc: Document) -> None:
    t0 = doc.tables[0]
    # Submission dates: visible pending.
    set_cell(t0.rows[2].cells[1], PENDING)
    set_cell(t0.rows[2].cells[3], PENDING)
    # Individual submission: group and member slots are explicitly N/A, not pending.
    INDIVIDUAL = "N/A — Individual submission"
    set_cell(t0.rows[5].cells[0], "Group number: " + INDIVIDUAL)
    set_cell(t0.rows[5].cells[1], f"{META['student']} ({META['student_id']})")
    set_cell(t0.rows[5].cells[2], PENDING)
    set_cell(t0.rows[5].cells[3], PENDING)
    for r in range(6, 10):
        set_cell(t0.rows[r].cells[0], "Group number: " + INDIVIDUAL)
        set_cell(t0.rows[r].cells[1], INDIVIDUAL)
        set_cell(t0.rows[r].cells[2], PENDING)
        set_cell(t0.rows[r].cells[3], PENDING)
    # Class and assessor.
    set_cell(t0.rows[10].cells[1], META["class"])
    set_cell(t0.rows[10].cells[3], META["assessor"])

    # Observation-record table: student name row and signatures.
    t2 = doc.tables[2]
    set_cell(t2.rows[0].cells[1], f"{META['student']} ({META['student_id']})")
    set_cell(t2.rows[7].cells[1], PENDING)  # student signature
    set_cell(t2.rows[7].cells[4], PENDING)  # date
    set_cell(t2.rows[8].cells[1], PENDING)  # assessor signature
    set_cell(t2.rows[8].cells[4], PENDING)  # date
    set_cell(t2.rows[9].cells[1], META["assessor"])
    # Grade row uses merged cells: keep the labels, append the pending value.
    set_cell(t2.rows[11].cells[0], "Grade: " + PENDING)
    set_cell(t2.rows[11].cells[3], "Assessor Signature: " + PENDING)
    set_cell(t2.rows[11].cells[6], "Date: " + PENDING)


# --------------------------------------------------------------------------- markdown parsing

INLINE_RE = re.compile(r"(\*\*.+?\*\*|`[^`]+`)")


def add_inline(paragraph, text: str) -> None:
    for part in INLINE_RE.split(text):
        if not part:
            continue
        if part.startswith("**") and part.endswith("**") and len(part) > 4:
            run = paragraph.add_run(part[2:-2])
            run.bold = True
            run.font.name = "Calibri"
            run.font.size = Pt(12)
        elif part.startswith("`") and part.endswith("`") and len(part) > 2:
            run = paragraph.add_run(part[1:-1])
            run.font.name = "Consolas"
            run.font.size = Pt(10)
        else:
            run = paragraph.add_run(part)
            run.font.name = "Calibri"
            run.font.size = Pt(12)


def parse_table(lines: list[str], start: int) -> tuple[list[list[str]], int]:
    rows = []
    i = start
    while i < len(lines) and lines[i].strip().startswith("|"):
        raw = lines[i].strip()
        cells = [c.strip() for c in raw.strip("|").split("|")]
        if not all(re.fullmatch(r":?-{2,}:?", c) for c in cells if c):
            rows.append(cells)
        i += 1
    return rows, i


def add_table(doc: Document, rows: list[list[str]]) -> None:
    if not rows:
        return
    width = max(len(r) for r in rows)
    table = doc.add_table(rows=len(rows), cols=width)
    try:
        table.style = "Table Grid"
    except KeyError:
        ensure_style(doc, "Table Grid", WD_STYLE_TYPE.TABLE)
        try:
            table.style = "Table Grid"
        except Exception:
            pass
    ensure_table_borders(table)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    for ri, row in enumerate(rows):
        for ci in range(width):
            text = row[ci] if ci < len(row) else ""
            cell = table.cell(ri, ci)
            cell.text = ""
            p = cell.paragraphs[0]
            p.paragraph_format.line_spacing = 1.0
            p.paragraph_format.space_after = Pt(0)
            for part in INLINE_RE.split(text):
                if not part:
                    continue
                if part.startswith("**") and part.endswith("**") and len(part) > 4:
                    run = p.add_run(part[2:-2])
                    run.bold = True
                elif part.startswith("`") and part.endswith("`") and len(part) > 2:
                    run = p.add_run(part[1:-1])
                    run.font.name = "Consolas"
                else:
                    run = p.add_run(part)
                run.font.size = Pt(9)
                if ri == 0:
                    run.bold = True
    doc.add_paragraph()


FIGURE_RE = re.compile(r"^Figure\s+(\d+):\s*(.+?)\s*[—-]\s*`([^`]+)`\s*$")


def resolve_image(rel: str) -> Path | None:
    rel_norm = rel.replace("\\", "/")
    name = Path(rel_norm).name
    if "real-final" in rel_norm:
        base = first_existing(REAL_FINAL_DIRS)
        return base / name if base else None
    # UML or other repository figure
    for base in UML_DIRS:
        candidate = base / name
        if candidate.exists():
            return candidate
    for base in (AUDIT, ASSIGNMENT):
        candidate = base / rel_norm
        if candidate.exists():
            return candidate
    return None


def add_figure(doc: Document, number: str, caption_rest: str, rel: str) -> bool:
    src = resolve_image(rel)
    if src is None:
        p = doc.add_paragraph()
        run = p.add_run(f"[PENDING: figure missing — {rel}]")
        run.bold = True
        run.font.color.rgb = RGBColor(0xC0, 0x00, 0x00)
        return False
    width = Cm(15.5) if src.suffix.lower() == ".png" and "real-final" not in rel else Cm(6.5)
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run()
    run.add_picture(str(src), width=width)
    cap = doc.add_paragraph(style="Caption")
    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    cap.add_run(f"Figure {number}: {caption_rest}").font.name = "Calibri"
    return True


def build_body(doc: Document, markdown: str) -> dict[str, int]:
    lines = markdown.splitlines()
    # Locate the body start at "## 1. Introduction" so the front-sheet metadata
    # block is not duplicated after the template page.
    start = 0
    for i, line in enumerate(lines):
        if re.match(r"^##\s+1\.\s+Introduction", line):
            start = i
            break

    stats = {"headings": 0, "tables": 0, "figures": 0, "missing_figures": 0, "paragraphs": 0}
    i = start
    while i < len(lines):
        line = lines[i].rstrip()
        stripped = line.strip()

        if not stripped or stripped == "---":
            i += 1
            continue

        if stripped.startswith("|"):
            rows, i = parse_table(lines, i)
            add_table(doc, rows)
            stats["tables"] += 1
            continue

        m = re.match(r"^(#{2,4})\s+(.*)$", stripped)
        if m:
            level = len(m.group(1)) - 1  # ## -> Heading 1
            heading = re.sub(r"\*\*(.+?)\*\*", r"\1", m.group(2))
            doc.add_heading(heading, level=min(level, 3))
            stats["headings"] += 1
            i += 1
            continue

        fm = FIGURE_RE.match(stripped)
        if fm:
            if add_figure(doc, fm.group(1), fm.group(2), fm.group(3)):
                stats["figures"] += 1
            else:
                stats["missing_figures"] += 1
            i += 1
            continue

        if stripped.startswith("- "):
            p = doc.add_paragraph()
            p.paragraph_format.left_indent = Cm(0.7)
            p.paragraph_format.first_line_indent = Cm(-0.35)
            p.add_run("•  ").font.name = "Calibri"
            add_inline(p, stripped[2:])
            stats["paragraphs"] += 1
            i += 1
            continue

        nm = re.match(r"^\d+\.\s+(.*)$", stripped)
        if nm:
            n = re.match(r"^(\d+)\.", stripped).group(1)
            p = doc.add_paragraph()
            p.paragraph_format.left_indent = Cm(0.7)
            p.paragraph_format.first_line_indent = Cm(-0.35)
            p.add_run(f"{n}.  ").font.name = "Calibri"
            add_inline(p, nm.group(1))
            stats["paragraphs"] += 1
            i += 1
            continue

        p = doc.add_paragraph()
        add_inline(p, stripped)
        stats["paragraphs"] += 1
        i += 1
    return stats


def add_appendix_d(doc: Document) -> int:
    base = first_existing(REAL_FINAL_DIRS)
    if base is None:
        doc.add_paragraph("[PENDING: real-final evidence folder missing]")
        return 0
    files = sorted(base.glob("*.png"))
    if not files:
        doc.add_paragraph("[PENDING: no real-final screenshots found]")
        return 0
    doc.add_heading("Appendix D — Screenshot set (real-final)", level=1)
    p = doc.add_paragraph(
        "Sixteen 1080×2400 captures from Pixel_6_API_35 (Android 15, API 35), fixed APK "
        "SHA-256 c7c8f9e2…, real Gemini. Root-folder images are DEMO / NON-FINAL and are not included."
    )
    p.paragraph_format.line_spacing = 1.15

    cols = 3
    rows = (len(files) + cols - 1) // cols
    table = doc.add_table(rows=rows, cols=cols)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    for idx, f in enumerate(files):
        cell = table.cell(idx // cols, idx % cols)
        cell.text = ""
        cp = cell.paragraphs[0]
        cp.alignment = WD_ALIGN_PARAGRAPH.CENTER
        cp.paragraph_format.line_spacing = 1.0
        run = cp.add_run()
        run.add_picture(str(f), width=Cm(4.9))
        cap = cell.add_paragraph()
        cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        cap.paragraph_format.line_spacing = 1.0
        r = cap.add_run(f.name)
        r.font.name = "Calibri"
        r.font.size = Pt(7.5)
    doc.add_paragraph()
    return len(files)


# --------------------------------------------------------------------------- conversion

def convert_pdf(docx_path: Path, pdf_path: Path) -> tuple[bool, str, int | None, int | None]:
    try:
        import win32com.client  # type: ignore
    except Exception as exc:  # pragma: no cover
        return False, f"Word COM unavailable: {exc}", None, None
    word = None
    doc = None
    try:
        word = win32com.client.DispatchEx("Word.Application")
        word.Visible = False
        word.DisplayAlerts = 0
        doc = word.Documents.Open(str(docx_path), ReadOnly=False)
        pages = int(doc.ComputeStatistics(2))   # wdStatisticPages
        words = int(doc.ComputeStatistics(0))   # wdStatisticWords
        doc.ExportAsFixedFormat(str(pdf_path), 17)  # wdExportFormatPDF
        return True, "Microsoft Word COM ExportAsFixedFormat (PDF)", pages, words
    except Exception as exc:  # pragma: no cover
        return False, f"Word COM export failed: {exc}", None, None
    finally:
        if doc is not None:
            try:
                doc.Close(False)
            except Exception:
                pass
        if word is not None:
            try:
                word.Quit()
            except Exception:
                pass


# --------------------------------------------------------------------------- main

def main() -> int:
    if not COMPACT.exists():
        print(f"ERROR: source not found: {COMPACT}")
        return 2
    template = first_existing(TEMPLATE_CANDIDATES)
    if template is None:
        print("ERROR: front-sheet template not found")
        return 2

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    markdown = COMPACT.read_text(encoding="utf-8")
    pending_count = markdown.count("[PENDING")

    doc = Document(str(template))
    set_base_styles(doc)
    setup_section(doc.sections[0])
    set_header(doc.sections[0], DRAFT_LABEL + " — BC00720 — ASM2")
    fill_front_sheet(doc)

    # Page break into the body section.
    body_section = doc.add_section(WD_SECTION.NEW_PAGE)
    setup_section(body_section, first_page_number=True)
    set_header(body_section, DRAFT_LABEL + " — BC00720 — ASM2")
    add_page_number_footer(body_section)

    # Title block (body).
    title = doc.add_paragraph()
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = title.add_run("AI Study Mentor: Peer Review, Development and Evaluation")
    run.bold = True
    run.font.name = "Calibri"
    run.font.size = Pt(20)
    sub = doc.add_paragraph()
    sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    sub.add_run("Unit 22: Application Development — Assignment Part 2").font.size = Pt(12)
    who = doc.add_paragraph()
    who.alignment = WD_ALIGN_PARAGRAPH.CENTER
    who.add_run(
        f"Individual submission — {META['student']} ({META['student_id']}), {META['class']}\n"
        f"Assessor: {META['assessor']}"
    ).font.size = Pt(11)
    draft_p = doc.add_paragraph()
    draft_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    dr = draft_p.add_run(DRAFT_LABEL)
    dr.bold = True
    dr.font.color.rgb = RGBColor(0xC0, 0x00, 0x00)
    doc.add_page_break()

    stats = build_body(doc, markdown)
    image_count = add_appendix_d(doc)

    doc.core_properties.title = DRAFT_LABEL + " — BC00720 ASM2 Report"
    doc.core_properties.author = META["student"]
    doc.core_properties.subject = "Unit 22 Application Development — Assignment Part 2 (draft)"

    doc.save(str(DOCX_OUT))

    pdf_ok, pdf_method, pages, words = convert_pdf(DOCX_OUT, PDF_OUT)
    if not pdf_ok:
        pdf_method = f"PDF not produced: {pdf_method}"

    real_final = first_existing(REAL_FINAL_DIRS)
    optional_png = sorted(p.name for p in (real_final / "optional").glob("*.png")) if real_final else []

    log = {
        "generated_at": _dt.datetime.now().isoformat(timespec="seconds"),
        "source": str(COMPACT),
        "source_sha256": sha256(COMPACT),
        "template": str(template),
        "output_docx": str(DOCX_OUT),
        "output_pdf": str(PDF_OUT) if pdf_ok else None,
        "pdf_method": pdf_method,
        "docx_pages": pages,
        "docx_words_including_tables": words,
        "markdown_prose_words": 2936,
        "pending_markers_in_source": pending_count,
        "body_headings": stats["headings"],
        "body_tables": stats["tables"],
        "body_figures": stats["figures"],
        "missing_figures": stats["missing_figures"],
        "appendix_d_images": image_count,
        "nfr_cold_start_ms": [5139, 3284, 3108, 3310, 3041],
        "nfr_cold_start_summary": {"n": 5, "min": 3041, "median": 3284, "max": 5139, "mean": 3576.4},
        "nfr_gemini_round_trip_ms": [2207, 913, 861],
        "nfr_gemini_summary": {"n": 3, "min": 861, "median": 913, "max": 2207, "mean": 1327.0, "http_200": 3},
        "nfr_note": "Measured observations on Pixel_6_API_35; no SLO defined. NFR2 action count and history/offline timing remain PENDING.",
        "nfr_source": str(ASM_DIR / "NFR_MEASUREMENTS.md"),
        "optional_evidence_png": optional_png,
        "optional_evidence_status": "SUPPLEMENTARY — image, offline error/recovery, login after logout; excluded from the 16-image final gate",
        "python": platform.python_version(),
        "python_docx": getattr(__import__("docx"), "__version__", "unknown"),
        "platform": platform.platform(),
    }
    LOG_OUT.write_text(json.dumps(log, indent=2, ensure_ascii=False), encoding="utf-8")
    LOG_TXT.write_text(
        "\n".join(
            [
                "ASM2 DRAFT EXPORT LOG",
                f"Generated: {log['generated_at']}",
                f"Source: {log['source']}",
                f"Source SHA-256: {log['source_sha256']}",
                f"Template: {log['template']}",
                f"DOCX: {log['output_docx']}",
                f"PDF: {log['output_pdf']}",
                f"PDF method: {log['pdf_method']}",
                f"Pages: {log['docx_pages']}",
                f"Words (DOCX incl. tables): {log['docx_words_including_tables']}",
                f"Markdown prose words: {log['markdown_prose_words']}",
                f"Pending markers in source: {log['pending_markers_in_source']}",
                f"Body figures: {log['body_figures']} (missing {log['missing_figures']})",
                f"Appendix D images: {log['appendix_d_images']}",
                "NFR cold start (ms): 5139, 3284, 3108, 3310, 3041 (median 3284; min 3041; max 5139; mean 3576.4; n=5)",
                "NFR Gemini round trip (ms): 2207, 913, 861 (median 913; min 861; max 2207; mean 1327.0; n=3; 3/3 HTTP 200)",
                "NFR note: observations only; no SLO. NFR2 action count and history/offline timing PENDING.",
                f"Optional evidence PNG (supplementary): {len(optional_png)}",
                f"Optional evidence status: {log['optional_evidence_status']}",
                f"Python: {log['python']}",
                f"python-docx: {log['python_docx']}",
                f"Platform: {log['platform']}",
                "",
            ]
        ),
        encoding="utf-8",
    )

    print(json.dumps(log, indent=2, ensure_ascii=False))
    return 0 if pdf_ok else 1


if __name__ == "__main__":
    sys.exit(main())
