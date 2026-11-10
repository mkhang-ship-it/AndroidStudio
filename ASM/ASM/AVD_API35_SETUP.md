# AVD API 35 Setup — Pixel_6_API_35

Ngày chạy: 2026-11-09
Môi trường: Windows (win32), Android emulator 37.1.11.0, sdkmanager 12.0, WHPX enabled.

## Biến môi trường dùng cho mọi lệnh

```bat
set "ANDROID_SDK_ROOT=C:\Users\ADMIN\AppData\Local\Android\Sdk"
set "ANDROID_HOME=C:\Users\ADMIN\AppData\Local\Android\Sdk"
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
```

- SDK: `C:\Users\ADMIN\AppData\Local\Android\Sdk`
- JBR: `C:\Program Files\Android\Android Studio\jbr`
- adb: `C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- avdmanager: `C:\Users\ADMIN\AppData\Local\Android\Sdk\cmdline-tools\latest\bin\avdmanager.bat`
- sdkmanager: `C:\Users\ADMIN\AppData\Local\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat`

## 1. Kiểm tra system image

Lệnh:

```powershell
Get-ChildItem -Recurse -Depth 3 "C:\Users\ADMIN\AppData\Local\Android\Sdk\system-images"
```

Kết quả thật (rút gọn): đã có sẵn

```
system-images\android-35\google_apis\x86_64\
    build.prop
    kernel-ranchu
    ramdisk.img
    system.img
    vendor.img
    package.xml
    ...
```

Kết luận: package `system-images;android-35;google_apis;x86_64` đã tồn tại, **không cần cài thêm**. Do đó không cần chạy `sdkmanager` cho system image và không phát sinh bước license cho image này.

## 2. Kiểm tra AVD và device profile

Lệnh:

```bat
avdmanager.bat list avd
avdmanager.bat list device
```

Kết quả thật — AVD trước khi tạo:

```
Available Android Virtual Devices:
    Name: Medium_Phone
  Device: medium_phone (Generic)
    Path: C:\Users\ADMIN\.android\avd\Medium_Phone.avd
  Target: Google APIs PlayStore (Google Inc.)
          Based on: Android API 0 Tag/ABI: google_apis_playstore/x86_64
  Sdcard: 512M
```

Kết quả thật — device profile Pixel 6 có tồn tại:

```
id: 28 or "pixel_6"
    Name: Pixel 6
```

`Medium_Phone` được giữ nguyên, không xoá/sửa.

## 3. Tạo AVD Pixel_6_API_35

Lệnh:

```bat
echo no | avdmanager.bat create avd -n Pixel_6_API_35 -k "system-images;android-35;google_apis;x86_64" -d pixel_6 --force
```

Kết quả thật (stdout):

```
Loading local repository...
Fetch remote repository... 100%
Auto-selecting single ABI x86_64
EXIT=0
```

stderr có warning không chặn (cmdline-tools cũ hơn Android Studio):

```
Warning: This version only understands SDK XML versions up to 3 but an SDK XML file of version 4 was encountered.
Warning: package.xml parsing problem. unexpected element ... "abis" / "translatedAbis"
```

Exit code: `0` → tạo thành công.

### Xác minh AVD sau khi tạo

Lệnh:

```bat
avdmanager.bat list avd
```

Kết quả thật:

```
Available Android Virtual Devices:
    Name: Medium_Phone
  Device: medium_phone (Generic)
    Path: C:\Users\ADMIN\.android\avd\Medium_Phone.avd
  Target: Google APIs PlayStore (Google Inc.)
          Based on: Android API 0 Tag/ABI: google_apis_playstore/x86_64
  Sdcard: 512M
---------
    Name: Pixel_6_API_35
  Device: pixel_6 (Google)
    Path: C:\Users\ADMIN\.android\avd\Pixel_6_API_35.avd
  Target: Google APIs (Google Inc.)
          Based on: Android API 35 Tag/ABI: google_apis/x86_64
  Sdcard: 512 MB
```

`config.ini` (các dòng chính):

```ini
abi.type=x86_64
hw.cpu.arch=x86_64
hw.device.name=pixel_6
hw.device.manufacturer=Google
hw.lcd.width=1080
hw.lcd.height=2400
hw.lcd.density=420
hw.ramSize=1536M
image.sysdir.1=system-images\android-35\google_apis\x86_64\
tag.id=google_apis
PlayStore.enabled=no
```

## 4. Kiểm tra tăng tốc phần cứng

Lệnh:

```bat
emulator.exe -accel-check
```

Kết quả thật:

```
accel:
0
WHPX(10.0.26200) is installed and usable.
accel
```

`0` = tăng tốc khả dụng.

## 5. Khởi động emulator

Lệnh (chạy nền):

```bat
emulator.exe -avd Pixel_6_API_35 -no-snapshot-load -gpu auto -no-boot-anim
```

Tiến trình nền: PID `12504` (task id `s04xw5ro`).

## 6. Chờ boot và xác minh

Lệnh chờ (bounded, tối đa 180s):

```powershell
adb wait-for-device
# poll: adb shell getprop sys.boot_completed cho tới khi = 1
```

Kết quả thật:

```
BOOT_COMPLETED after 52 checks
--- devices ---
List of devices attached
emulator-5554   device
```

### Thông số thiết bị (kết quả thật)

| Property | Giá trị |
|---|---|
| `ro.build.version.sdk` | `35` |
| `ro.build.version.release` | `15` |
| `ro.product.model` | `sdk_gphone64_x86_64` |
| `ro.product.device` | `emu64xa` |
| `ro.product.cpu.abi` | `x86_64` |
| `ro.build.fingerprint` | `google/sdk_gphone64_x86_64/emu64xa:15/AE3A.240806.043/12960925:userdebug/dev-keys` |

Lệnh:

```powershell
adb shell getprop ro.build.version.sdk
adb shell getprop ro.build.version.release
adb shell getprop ro.product.model
```

## 7. Screenshot màn hình home

Lưu ý thực tế: dùng `adb exec-out screencap -p > file.png` trong PowerShell 5.1 làm hỏng file (redirect ghi UTF-16). Đã chuyển sang cách pull:

```powershell
adb shell input keyevent KEYCODE_HOME
adb shell screencap -p /sdcard/home.png
adb pull /sdcard/home.png "ASM\ASM\evidence\pixel-api-35\01-device-home.png"
adb shell rm /sdcard/home.png
```

Kết quả thật:

```
/sdcard/home.png: 1 file pulled, 0 skipped. 25.7 MB/s (1374272 bytes in 0.051s)
size=1080x2400
```

File evidence: `ASM/ASM/evidence/pixel-api-35/01-device-home.png`
- Kích thước ảnh: 1080x2400
- Nội dung: launcher home screen của Android 15 (wallpaper, thanh search, icon Gmail/Photos/YouTube/Phone/Messages/Chrome)
- Dung lượng sau pull: 1,374,272 bytes (ảnh gốc PNG)

## 8. Kết luận

| Hạng mục | Trạng thái |
|---|---|
| System image `android-35;google_apis;x86_64` | Đã có sẵn (không cần cài) |
| AVD `Pixel_6_API_35` (device `pixel_6`) | Đã tạo thành công |
| AVD `Medium_Phone` | Giữ nguyên |
| Emulator boot | Thành công (`sys.boot_completed=1`) |
| SDK = 35 | Xác minh |
| Release = 15 | Xác minh |
| Model | `sdk_gphone64_x86_64` |
| Screenshot home | Đã chụp thành công |

Không có blocker. Không sửa app source/report prose.
