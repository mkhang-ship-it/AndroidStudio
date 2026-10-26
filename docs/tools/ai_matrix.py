"""Ma tran test hoi AI tren emulator (subject + cache + buoc dau).

Chay:
    python docs/tools/ai_matrix.py

Yeu cau:
    - Emulator dang chay, app da cai va da dang nhap.
    - App dang o tab "Hoi AI".
    - Cau hoi dung ASCII vi `adb shell input text` khong go duoc dau.

Ket qua ky vong: 9/9 pass (xem docs/IMPROVEMENT_LOG.md muc 2.11).
"""

import os
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

ADB = os.path.expandvars(r"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe")


def adb(*args, timeout=90):
    last = None
    for _ in range(3):
        try:
            result = subprocess.run(
                [ADB, *args],
                capture_output=True,
                text=True,
                encoding="utf-8",
                errors="replace",
                timeout=timeout,
            )
            last = result
            combined = (result.stderr or "") + (result.stdout or "")
            if "cannot connect to daemon" in combined or "daemon not running" in combined:
                subprocess.run([ADB, "kill-server"], capture_output=True)
                time.sleep(2)
                subprocess.run([ADB, "start-server"], capture_output=True)
                time.sleep(3)
                continue
            return result
        except Exception as error:  # noqa: BLE001
            last = error
            time.sleep(2)
            subprocess.run([ADB, "kill-server"], capture_output=True)
            time.sleep(2)
            subprocess.run([ADB, "start-server"], capture_output=True)
            time.sleep(3)
    return last


def tap(x, y):
    adb("shell", "input", "tap", str(x), str(y))


def clear_field(times=45):
    for _ in range(times):
        adb("shell", "input", "keyevent", "67")


def type_text(text):
    adb("shell", "input", "text", text.replace(" ", "%s"))


def dump_texts():
    for _ in range(3):
        adb("shell", "uiautomator", "dump", "/sdcard/mx.xml")
        data = adb("exec-out", "cat", "/sdcard/mx.xml").stdout or ""
        start = data.find("<?xml")
        if start < 0:
            time.sleep(1)
            continue
        try:
            root = ET.fromstring(data[start:])
            return [n.attrib.get("text") for n in root.iter() if n.attrib.get("text")]
        except ET.ParseError:
            time.sleep(1)
    return []


def to_top():
    for _ in range(3):
        adb("shell", "input", "swipe", "540", "600", "540", "1900", "300")
        time.sleep(0.4)


def ask(question, wait=24):
    to_top()
    tap(540, 660)
    time.sleep(0.6)
    clear_field()
    type_text(question)
    time.sleep(0.5)
    tap(540, 1093)
    time.sleep(wait)
    texts = dump_texts()
    if not any(t.startswith(("Bước 1", "Step 1")) for t in texts):
        time.sleep(10)
        texts = dump_texts()

    subject = next(
        (t for t in texts if t in ("Toán học", "Khoa học", "Lập trình", "Lịch sử", "Ngôn ngữ")),
        "?",
    )
    cached = "Từ bộ nhớ đệm" in texts
    step = next((t for t in texts if t.startswith(("Bước 1", "Step 1"))), "")
    return subject, cached, step, texts


CASES = [
    ("Giai phuong trinh x^2 - 5x + 6 = 0", "Toán học", False, "toán cơ bản"),
    ("Giai phuong trinh x^2 - 5x + 6 = 1", "Toán học", False, "khác số cuối -> không cache"),
    ("Quang hop o thuc vat la gi", "Khoa học", False, "sinh học"),
    ("Chien tranh the gioi thu hai ket thuc nam nao", "Lịch sử", False, "lịch sử"),
    ("Phan tich bai tho Dong chi cua Chinh Huu", "Ngôn ngữ", False, "ngữ văn"),
    ("Ham trong Python la gi", "Lập trình", False, "code Python"),
    ("Tinh dao ham cua x^4 tai x = 2", "Toán học", False, "toán khác số mũ"),
    ("Giai phuong trinh x^2 - 5x + 6 = 0", "Toán học", True, "lặp y hệt -> cache"),
    ("Giai phuong trinh cua x^2 - 5x + 6 = 0", "Toán học", True, "thêm từ dừng -> cache"),
]


def main():
    adb("shell", "am", "force-stop", "com.aistudy.aistudy")
    adb("shell", "am", "start", "-n", "com.aistudy.aistudy/.MainActivity")
    time.sleep(8)
    tap(100, 2230)  # tab Hỏi AI
    time.sleep(2)

    results = []
    for index, (question, expected_subject, expected_cache, note) in enumerate(CASES, 1):
        print(f"[{index}/{len(CASES)}] {question}", flush=True)
        subject, cached, step, texts = ask(question)
        ok_subject = subject == expected_subject
        ok_cache = cached == expected_cache
        ok = ok_subject and ok_cache and bool(step)
        results.append((question, expected_subject, subject, expected_cache, cached, ok, note))
        print(
            f"    -> mon={subject} (ky vong {expected_subject}) "
            f"cache={cached} (ky vong {expected_cache}) buoc='{step[:40]}' "
            f"{'OK' if ok else 'FAIL'}",
            flush=True,
        )

    print("\n===== TONG HOP =====")
    passed = 0
    for question, exp_subject, subject, exp_cache, cached, ok, note in results:
        passed += 1 if ok else 0
        print(
            f"{'PASS' if ok else 'FAIL'} | {subject} | cache={cached} | {note} | {question}"
        )
    print(f"\n{passed}/{len(results)} case pass")
    return 0 if passed == len(results) else 1


if __name__ == "__main__":
    sys.exit(main())
