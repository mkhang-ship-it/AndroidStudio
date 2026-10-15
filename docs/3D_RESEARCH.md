# Nghiên cứu 3D cho app AI Study

Mục tiêu: chọn kỹ thuật 3D phù hợp để ghép vào màn Practice (quiz / flashcard), loại bỏ hướng quá nặng / rủi ro.

## 1. Ba đường so sánh

- **A. Filament engine**: render 3D thật (PBR, model .glb), viết custom `ModelViewer` + `FilamentView`.
- **B. SceneView lib**: wrapper quanh Filament/Sceneform, dùng `SceneView` / `ModelNode` trong Compose.
- **C. graphicsLayer giả-3D**: chỉ dùng `Modifier.graphicsLayer { rotationY; cameraDistance }` + `animateFloatAsState(spring())`, không engine 3D.

## 2. Bảng so sánh 5 tiêu chí

| Tiêu chí | A. Filament engine | B. SceneView lib | C. graphicsLayer giả-3D |
|---|---|---|---|
| 1. Độ nặng dependency | Rất nặng: native `.so` ~ MB, thêm module `filament-android`, tăng APK, build chậm | Nặng vừa: 1 dependency Gradle (~MB), kéo theo Filament/ARCore transitives, tăng size + conflict version | Nhẹ nhất: 0 dependency mới, chỉ Compose core (`animation-core`, `ui-graphics`) đã có sẵn |
| 2. Hiệu năng máy yếu | Kém: tốn GPU/RAM, load model gây drop frame, máy RAM 2–3 GB dễ OOM/crash | Trung bình: đỡ hơn Filament trần nhưng vẫn giữ Scene/RenderLoop chạy nền, máy yếu nóng + hao pin | Tốt nhất: chỉ là transform layer GPU 2.5D, 60fps ngay cả máy yếu, không load model |
| 3. Thời gian junior học | Lâu nhất: phải học lifecycle engine, PBR material, tone-mapping, JNI crash log (1–2 tuần mới vững) | Trung bình: API gọn hơn nhưng vẫn phải hiểu node/scene/lighting/asset loading (vài ngày) | Nhanh nhất: chỉ cần `rotationY + cameraDistance + spring`, junior Compose copy demo là chạy (vài giờ) |
| 4. Rủi ro với code hiện tại | Cao nhất: đụng NDK/ABI filter, minSdk, ProGuard/R8, lifecycle leak nếu quên `destroy`, khó review | Trung bình-cao: version lib trôi nhanh, dễ conflict Compose/BOM, ARCore permission thừa nếu chỉ cần model tĩnh | Thấp nhất: file UI thuần, không đụng Gradle/manifest/permission, xóa là sạch, không ảnh hưởng Practice hiện tại |
| 5. Hợp với app học tập không | Thừa: app cần flashcard/quiz lật thẻ nhanh, không cần render PBR; 3D thật gây xao nhãng + chậm vào bài | Lệch: hợp showcase model 3D (sinh học/geometri) nhưng Practice hiện tại chỉ cần quiz, ROI thấp | Hợp nhất: hiệu ứng lật thẻ quiz đúng nhu cầu Practice, giữ focus học tập, dễ mở rộng (deck, swipe, stagger) |

## 3. Kết luận

- **Ghép vào màn Practice: C (graphicsLayer giả-3D).** File demo `ThreeDCardDemo.kt` (`FlipCard` lật hỏi/đáp bằng `rotationY + cameraDistance = 12f + spring`) đủ nhẹ để gắn thẳng vào Practice mà không sửa code cũ.
- **Loại: A (Filament engine) — loại hẳn** vì quá nặng, rủi ro native/build, không hợp quiz. **B (SceneView) — tạm loại**, chỉ xem lại sau nếu có epic riêng "xem mô hình 3D" (ví dụ sinh học/giải phẫu) chứ không ghép vào Practice bây giờ.
