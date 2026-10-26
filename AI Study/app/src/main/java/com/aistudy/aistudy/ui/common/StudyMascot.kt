package com.aistudy.aistudy.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class MascotMood {
    IDLE,
    THINKING,
    CELEBRATING,
    SLEEPING
}

/**
 * Mascot "meo hoc tap" ve hoan toan bang Compose Canvas (khong can asset/anh).
 *
 * Thiet ke co y khac linh vat cua Duolingo:
 * - la MEo (tai tam giac, duoi cong, ria mep), khong phai cu meo;
 * - bang mau tim + kem + vang, khong dung xanh la chu dao;
 * - doi mu tot nghiep de ro chu de hoc tap.
 *
 * - IDLE: nhun nhay, nhay mat, duoi ve.
 * - THINKING: nghieng dau, mat nhin len, bong suy nghi.
 * - CELEBRATING: nhay len, duoi dung manh, sao lap lanh.
 */
@Composable
fun StudyMascot(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    val transition = rememberInfiniteTransition(label = "study-mascot")

    val orbitState = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mascot-orbit"
    )
    val bobState = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot-bob"
    )
    val blinkState = transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4_800
                1f at 0
                1f at 3_850
                0.06f at 3_940
                0.06f at 4_060
                1f at 4_160
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "mascot-blink"
    )
    val swayState = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot-sway"
    )
    val sparkleState = transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot-sparkle"
    )

    // Bang mau rieng cua mascot — khong lay xanh la thuong hieu cua Duolingo.
    val bodyColor = Color(0xFF7B61FF)
    val bodyShade = Color(0xFF5B43D6)
    val bellyColor = Color(0xFFFFF2DA)
    val innerEarColor = Color(0xFFFFAFC4)
    val capColor = Color(0xFF23324A)
    val capGold = Color(0xFFFFC94D)
    val eyeColor = Color(0xFFFFFDF8)
    val pupilColor = Color(0xFF20242C)
    val noseColor = Color(0xFFFF8FA3)
    val whiskerColor = Color(0xFFFFF2DA)
    val objectColor = MaterialTheme.colorScheme.secondary
    val pencilColor = MaterialTheme.colorScheme.tertiary
    val sparkleColor = MaterialTheme.colorScheme.tertiary

    val thinking = mood == MascotMood.THINKING
    val celebrating = mood == MascotMood.CELEBRATING
    val sleeping = mood == MascotMood.SLEEPING

    Canvas(modifier = modifier.size(size)) {
        val unit = this.size.minDimension / 100f

        // Doc animation State trong draw phase de chi invalidate phan ve,
        // khong recompose ca man hinh moi frame.
        val orbit = orbitState.value
        val bob = bobState.value
        val blink = blinkState.value
        val sway = swayState.value
        val sparkle = sparkleState.value

        fun point(x: Float, y: Float) = Offset(x * unit, y * unit)
        fun area(width: Float, height: Float) = Size(width * unit, height * unit)

        val bobAmount = when {
            celebrating -> 8f * bob
            sleeping -> 0.6f * bob
            thinking -> 1.4f * bob
            else -> 2.4f * bob
        }
        val tilt = if (thinking) -6f else 0f
        val eyeHeight = if (sleeping) 0.08f else blink
        val lookX = if (thinking) 1.6f else 0f
        val lookY = if (thinking) -2f else 0.4f
        val tailAngle = when {
            celebrating -> -22f + sway * 30f
            thinking -> -4f + sway * 6f
            else -> -6f + sway * 10f
        }

        // Vat the bay quanh: sach (xanh duong) va but chi (vang).
        drawOrbitObject(
            angleDegrees = orbit,
            radiusX = 45f,
            radiusY = 36f,
            unit = unit
        ) {
            drawBook(unit, objectColor)
        }
        drawOrbitObject(
            angleDegrees = orbit + 145f,
            radiusX = 47f,
            radiusY = 33f,
            unit = unit
        ) {
            drawPencil(unit, pencilColor)
        }

        withTransform({
            translate(0f, -bobAmount * unit)
            rotate(tilt, point(50f, 55f))
        }) {
            // Duoi meo (ve truoc de than che goc duoi)
            withTransform({ rotate(tailAngle, point(72f, 76f)) }) {
                val tail = Path().apply {
                    moveTo(70f * unit, 76f * unit)
                    cubicTo(
                        88f * unit, 80f * unit,
                        94f * unit, 60f * unit,
                        84f * unit, 50f * unit
                    )
                }
                drawPath(
                    path = tail,
                    color = bodyColor,
                    style = Stroke(width = 6f * unit, cap = StrokeCap.Round)
                )
            }

            // Than + bung
            drawOval(
                color = bodyColor,
                topLeft = point(25f, 35f),
                size = area(50f, 58f)
            )
            drawOval(
                color = bellyColor,
                topLeft = point(37f, 52f),
                size = area(26f, 36f)
            )

            // Chan truoc
            drawOval(
                color = bellyColor,
                topLeft = point(35f, 84f),
                size = area(13f, 10f)
            )
            drawOval(
                color = bellyColor,
                topLeft = point(52f, 84f),
                size = area(13f, 10f)
            )

            // Tai ngoai + tai trong
            val leftEar = Path().apply {
                moveTo(33f * unit, 25f * unit)
                lineTo(28f * unit, 7f * unit)
                lineTo(46f * unit, 19f * unit)
                close()
            }
            drawPath(leftEar, bodyColor)
            val leftInnerEar = Path().apply {
                moveTo(34f * unit, 21f * unit)
                lineTo(31f * unit, 12f * unit)
                lineTo(41f * unit, 18f * unit)
                close()
            }
            drawPath(leftInnerEar, innerEarColor)

            val rightEar = Path().apply {
                moveTo(67f * unit, 25f * unit)
                lineTo(72f * unit, 7f * unit)
                lineTo(54f * unit, 19f * unit)
                close()
            }
            drawPath(rightEar, bodyColor)
            val rightInnerEar = Path().apply {
                moveTo(66f * unit, 21f * unit)
                lineTo(69f * unit, 12f * unit)
                lineTo(59f * unit, 18f * unit)
                close()
            }
            drawPath(rightInnerEar, innerEarColor)

            // Dau
            drawOval(
                color = bodyColor,
                topLeft = point(29f, 17f),
                size = area(42f, 42f)
            )

            // Mu tot nghiep: de ro day la mascot hoc tap, khong phai linh vat khac.
            drawRoundRect(
                color = capColor,
                topLeft = point(43f, 14f),
                size = area(14f, 8f),
                cornerRadius = CornerRadius(1.5f * unit, 1.5f * unit)
            )
            val capBoard = Path().apply {
                moveTo(32f * unit, 13f * unit)
                lineTo(50f * unit, 5f * unit)
                lineTo(68f * unit, 13f * unit)
                lineTo(50f * unit, 21f * unit)
                close()
            }
            drawPath(capBoard, capColor)
            withTransform({ rotate(sway * 8f - 4f, point(68f, 13f)) }) {
                drawLine(
                    color = capGold,
                    start = point(68f, 13f),
                    end = point(72f, 26f),
                    strokeWidth = 1.8f * unit,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = capGold,
                    radius = 2f * unit,
                    center = point(72.5f, 27f)
                )
            }

            // Mat
            val eyeTop = 33f - (13f * eyeHeight) / 2f
            drawOval(
                color = eyeColor,
                topLeft = Offset(36f * unit, eyeTop * unit),
                size = Size(11f * unit, 13f * eyeHeight * unit)
            )
            drawOval(
                color = eyeColor,
                topLeft = Offset(53f * unit, eyeTop * unit),
                size = Size(11f * unit, 13f * eyeHeight * unit)
            )

            val pupilWidth = 5f
            val pupilHeight = 6f * eyeHeight
            drawOval(
                color = pupilColor,
                topLeft = Offset(
                    (41.5f + lookX - pupilWidth / 2f) * unit,
                    (39.5f + lookY - pupilHeight / 2f) * unit
                ),
                size = Size(pupilWidth * unit, pupilHeight * unit)
            )
            drawOval(
                color = pupilColor,
                topLeft = Offset(
                    (58.5f + lookX - pupilWidth / 2f) * unit,
                    (39.5f + lookY - pupilHeight / 2f) * unit
                ),
                size = Size(pupilWidth * unit, pupilHeight * unit)
            )

            // Mui + mieng
            val nose = Path().apply {
                moveTo(48f * unit, 47f * unit)
                lineTo(52f * unit, 47f * unit)
                lineTo(50f * unit, 50f * unit)
                close()
            }
            drawPath(nose, noseColor)

            val mouth = Path().apply {
                moveTo(50f * unit, 50f * unit)
                quadraticTo(47f * unit, 54f * unit, 44f * unit, 52f * unit)
                moveTo(50f * unit, 50f * unit)
                quadraticTo(53f * unit, 54f * unit, 56f * unit, 52f * unit)
            }
            drawPath(
                path = mouth,
                color = pupilColor.copy(alpha = 0.65f),
                style = Stroke(width = 1.3f * unit, cap = StrokeCap.Round)
            )

            // Ria mep
            val whiskerWidth = 1.1f * unit
            drawLine(whiskerColor, point(43f, 49f), point(31f, 46f), whiskerWidth, StrokeCap.Round)
            drawLine(whiskerColor, point(43f, 52f), point(30f, 52f), whiskerWidth, StrokeCap.Round)
            drawLine(whiskerColor, point(43f, 55f), point(31f, 58f), whiskerWidth, StrokeCap.Round)
            drawLine(whiskerColor, point(57f, 49f), point(69f, 46f), whiskerWidth, StrokeCap.Round)
            drawLine(whiskerColor, point(57f, 52f), point(70f, 52f), whiskerWidth, StrokeCap.Round)
            drawLine(whiskerColor, point(57f, 55f), point(69f, 58f), whiskerWidth, StrokeCap.Round)

            if (thinking) {
                drawCircle(
                    color = capGold.copy(alpha = 0.55f + 0.35f * sparkle),
                    radius = 2.2f * unit,
                    center = point(80f, 21f)
                )
                drawCircle(
                    color = capGold.copy(alpha = 0.45f + 0.35f * sparkle),
                    radius = 3.2f * unit,
                    center = point(87f, 13f)
                )
                drawCircle(
                    color = capGold.copy(alpha = 0.35f + 0.35f * sparkle),
                    radius = 4.2f * unit,
                    center = point(95f, 4f)
                )
            }
        }

        if (celebrating) {
            drawSparkle(
                center = point(17f, 18f),
                radius = 4.5f * unit,
                color = sparkleColor,
                alpha = sparkle
            )
            drawSparkle(
                center = point(84f, 20f),
                radius = 3.6f * unit,
                color = sparkleColor,
                alpha = 1f - sparkle * 0.6f
            )
            drawSparkle(
                center = point(50f, 2f),
                radius = 3f * unit,
                color = sparkleColor,
                alpha = sparkle
            )
        }
    }
}

private fun DrawScope.drawOrbitObject(
    angleDegrees: Float,
    radiusX: Float,
    radiusY: Float,
    unit: Float,
    draw: DrawScope.() -> Unit
) {
    val radians = angleDegrees * PI / 180.0
    val x = (50f + cos(radians).toFloat() * radiusX) * unit
    val y = (50f + sin(radians).toFloat() * radiusY) * unit
    withTransform({
        translate(x, y)
        rotate(angleDegrees * 0.55f)
    }) {
        draw()
    }
}

private fun DrawScope.drawBook(unit: Float, color: Color) {
    val width = 11f * unit
    val height = 8f * unit
    drawRoundRect(
        color = color,
        topLeft = Offset(-width / 2f, -height / 2f),
        size = Size(width, height),
        cornerRadius = CornerRadius(1.4f * unit, 1.4f * unit)
    )
    drawLine(
        color = Color.White.copy(alpha = 0.85f),
        start = Offset(0f, -height / 2f),
        end = Offset(0f, height / 2f),
        strokeWidth = 1f * unit
    )
}

private fun DrawScope.drawPencil(unit: Float, color: Color) {
    val width = 3.2f * unit
    val height = 11f * unit
    drawRoundRect(
        color = color,
        topLeft = Offset(-width / 2f, -height / 2f),
        size = Size(width, height * 0.72f),
        cornerRadius = CornerRadius(0.8f * unit, 0.8f * unit)
    )
    val tip = Path().apply {
        moveTo(-width / 2f, height * 0.22f)
        lineTo(width / 2f, height * 0.22f)
        lineTo(0f, height / 2f)
        close()
    }
    drawPath(tip, color)
}

private fun DrawScope.drawSparkle(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float
) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        quadraticTo(center.x, center.y, center.x + radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + radius)
        quadraticTo(center.x, center.y, center.x - radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - radius)
        close()
    }
    drawPath(path, color.copy(alpha = alpha.coerceIn(0f, 1f)))
}
