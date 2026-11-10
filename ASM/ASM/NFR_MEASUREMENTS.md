# ASM2 NFR Measurement Record

**Status:** Two metrics measured on the fixed APK; all other metrics remain explicitly PENDING.
No values are invented. This record separates measured values from unmeasured requirements.

## Environment

- Measurement date/time: **2026-11-10 00:51 +07:00** (measurement window approx. 00:40–00:51 local)
- Device: `Pixel_6_API_35` / Android 15 / API 35 / model `sdk_gphone64_x86_64` (emulator-5554)
- APK: fixed practice-scoring build, SHA-256 `c7c8f9e2b3bc1307d0840502cdae6b27bdc2012c68d15da3831e1f4f50bd3c71`
- Launcher component (from installed manifest): `com.aistudy.aistudy/.MainActivity`
- AI mode: real Gemini via local `ai.properties`; key value never printed or stored in evidence
- Network condition (emulator `ping`, 3 packets each, 0% loss):
  - `8.8.8.8`: min/avg/max = 40.215 / **49.560** / 60.535 ms
  - `generativelanguage.googleapis.com`: min/avg/max = 397.893 / **419.816** / 436.899 ms
  - Raw: `evidence/pixel-api-35/real-final/nfr-network.txt`

## Measured metrics

### 1. Cold start — MEASURED

**Method**

```bat
adb shell am force-stop com.aistudy.aistudy
:: wait 1.5 s
adb shell am start -W -n com.aistudy.aistudy/.MainActivity
```

Conditions: emulator already booted; screen awake/unlocked; 5 repeated runs; `am start -W` TotalTime (activity launch/display). Raw output: `evidence/pixel-api-35/real-final/nfr-cold-start.txt`.

**Raw values (TotalTime, ms)**

| Run | TotalTime |
|---:|---:|
| 1 | 5139 |
| 2 | 3284 |
| 3 | 3108 |
| 4 | 3310 |
| 5 | 3041 |

**Summary:** n=5, min **3041 ms**, median **3284 ms**, max **5139 ms**, mean **3576.4 ms**.

**Status:** MEASURED. No pass/fail NFR threshold is defined in the project, so this is reported as an observation, not compliance.

**Limitations**

- Emulator (WHPX) on a shared host; host load affects results.
- Run 1 is the first launch after install and includes cold code/cache effects; runs 2–5 are steady-state.
- `am start -W` measures activity display, not a separate "Welcome text visible / AuthGate resolved" timestamp.
- Only 5 runs, one device/session; no percentile or statistical significance claimed.

### 2. Gemini ask round-trip — MEASURED (3/3 real requests)

**Method**

For each of three distinct questions, logcat was cleared, the question was submitted through the real UI, and round-trip was computed from epoch logcat timestamps:

```
t(POST .../gemini-flash-lite-latest:generateContent)  →  t(← HTTP 200)
```

This covers request upload, server processing, and response body read by the app. Raw logs: `nfr-gemini-ask-1.txt`, `nfr-gemini-ask-2.txt`, `nfr-gemini-ask-3.txt`.

**Raw values**

| Request | Question | POST epoch | HTTP 200 epoch | Round-trip |
|---:|---|---:|---:|---:|
| 1 | What is 5 plus 5 | 1794246604.151 | 1794246606.358 | **2207 ms** |
| 2 | What is the capital of Japan | 1794246629.434 | 1794246630.347 | **913 ms** |
| 3 | What is 12 minus 4 | 1794246647.506 | 1794246648.367 | **861 ms** |

All three returned `HTTP 200` and were live requests (no cache badge; each produced a POST).

**Summary:** n=3, min **861 ms**, median **913 ms**, max **2207 ms**, mean **1327.0 ms**.

**Status:** MEASURED (3/3 successful real Gemini requests). No latency SLO is defined in the project.

**Limitations**

- Includes upload + server + response read; not pure network RTT.
- Server load and emulator/host network vary; first request was slower (2207 ms).
- Only 3 samples; no percentile claim.
- UI polling observed the answer within ~1–2 s after HTTP 200; that UI observation is not included in the reported round-trip.
- Real Gemini quota/network required; if the service is unavailable the measurement cannot be repeated.

## PENDING metrics

| Metric | Why PENDING | Notes |
|---|---|---|
| History screen open time | No reliable timing hook for Compose tab navigation; `uiautomator` polling granularity is too coarse for a defensible millisecond value | Not claimed |
| Actions to submit | No formally defined usability script/observer count executed | Not claimed |
| Lockout persistence | Automated test coverage exists; manual time/usability measurement not executed | Automated pass is not a latency measurement |
| OS-level push permission | Not exercised | Functional gap |
| Demo account | No controlled demo account supplied | Must be supplied later |

## Optional flows observed (functional, no timing claim)

These were exercised after the measurements, on the same fixed APK/device.
Evidence lives in `real-final/optional/` and is supplementary to the 16-image gate.

| Flow | Action | Observed result | Evidence |
|---|---|---|---|
| Image question | Pushed a simple `2 + 2 = ?` PNG, opened the system photo picker, selected it | App showed `Đã đính kèm ảnh`; submit produced a real Gemini answer `2 + 2 = 4` | `image-01-attached.png`, `image-02-answer.png`, `image-question-logcat.txt` |
| Offline error | Enabled airplane mode, disabled Wi-Fi/data; ping returned `unknown host`; submitted a question | UI error: `Không gọi được AI. Kiểm tra kết nối mạng rồi thử lại.` | `offline-01-error.png`, `offline-logcat.txt`, `offline-network-state.txt` |
| Offline recovery | Restored airplane/Wi-Fi; IP ping worked but DNS initially failed; Wi-Fi off/on restored DNS; retried | Retry produced HTTP 200 and answer `9 + 9 = 18` | `offline-02-recovered.png`, `offline-recovery-logcat.txt` |
| Login after logout | Logged out, opened the login screen, signed in with the local measurement account | Main shell opened on Hỏi AI; Profile showed the account and stored XP/badges | `login-01-screen.png`, `login-02-success.png`, `login-03-profile.png` |

They are functional observations, not latency measurements. No timing value is
claimed for these flows.

## Verified functional facts (not latency metrics)

| Fact | Evidence | Status |
|---|---|---|
| Correct MCQ answer shows positive feedback | `real-final/12-practice-correct-feedback.png`; real Gemini quiz, option B selected, `Chính xác!` | VERIFIED |
| Practice scoring regression tests | `real-final/tests-unit-practice.txt`: `PracticeAnswerTest` 7/7, 0 failures | VERIFIED |
| Connected instrumented tests | `real-final/tests-connected.txt` + XML: 39/39, 0 failures/skipped, BUILD SUCCESSFUL | VERIFIED |
| Real Gemini Ask answer | `real-final/06-gemini-answer.png`, `06-gemini-network-logcat.txt` | VERIFIED |
| Real Gemini quiz generation | `real-final/11-practice-question.png`, `11-gemini-quiz-logcat.txt` | VERIFIED |

## Raw evidence files

- `evidence/pixel-api-35/real-final/nfr-cold-start.txt`
- `evidence/pixel-api-35/real-final/nfr-gemini-ask-1.txt`
- `evidence/pixel-api-35/real-final/nfr-gemini-ask-2.txt`
- `evidence/pixel-api-35/real-final/nfr-gemini-ask-3.txt`
- `evidence/pixel-api-35/real-final/nfr-network.txt`
- `evidence/pixel-api-35/real-final/optional/` (image/offline/login supplementary evidence)

## Interpretation

The fixed build has repeatable runtime evidence for cold start and Gemini ask round-trip on API 35. Other NFR dimensions remain PENDING and must not be represented as measured or compliant.
