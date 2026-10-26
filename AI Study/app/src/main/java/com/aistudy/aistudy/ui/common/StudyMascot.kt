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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
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
 * Mascot cu meo ve hoan toan bang Compose Canvas (khong can asset/anh).
 *
 * - IDLE: nhun nhay nhe, chim dap canh, mat nhap nhay.
 * - THINKING: nghieng dau, mat nhin len, bong nghi suy nghi.
 * - CELEBRATING: nhay len, dang canh, sao lap lanh.
 *
 * Sach va but chi bay quanh tao cam giac "vat the dang chuyen dong" ma khong
 * can them file hinh nao.
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
    val wingState = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot-wing"
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

    val bodyColor = MaterialTheme.colorScheme.primary
    val bellyColor = MaterialTheme.colorScheme.primaryContainer
    val accentColor = MaterialTheme.colorScheme.tertiary
    val eyeColor = MaterialTheme.colorScheme.surface
    val pupilColor = MaterialTheme.colorScheme.onSurface
    val objectColor = MaterialTheme.colorScheme.secondary
    val sparkleColor = MaterialTheme.colorScheme.tertiary

    val thinking = mood == MascotMood.THINKING
    val celebrating = mood == MascotMood.CELEBRATING
    val sleeping = mood == MascotMood.SLEEPING

    Canvas(modifier = modifier.size(size)) {
        val unit = this.size.minDimension / 100f

        // Doc animation State trong draw phase (khong phai composition) de
        // mascot chi invalidate phan ve, khong recompose ca man hinh moi frame.
        val orbit = orbitState.value
        val bob = bobState.value
        val blink = blinkState.value
        val wing = wingState.value
        val sparkle = sparkleState.value

        fun point(x: Float, y: Float) = Offset(x * unit, y * unit)
        fun area(width: Float, height: Float) = Size(width * unit, height * unit)

        val bobAmount = when {
            celebrating -> 8f * bob
            sleeping -> 0.6f * bob
            thinking -> 1.4f * bob
            else -> 2.4f * bob
        }
        val tilt = if (thinking) -7f else 0f
        val eyeHeight = if (sleeping) 0.08f else blink
        val lookX = if (thinking) 1.8f else 0f
        val lookY = if (thinking) -2.2f else 0.4f
        val wingLift = when {
            celebrating -> 34f + wing * 14f
            thinking -> 5f + wing * 6f
            else -> 4f + wing * 7f
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
            drawPencil(unit, accentColor)
        }

        withTransform({
            translate(0f, -bobAmount * unit)
            rotate(tilt, point(50f, 50f))
        }) {
            // Chan
            drawLine(
                color = accentColor,
                start = point(42f, 87f),
                end = point(42f, 94f),
                strokeWidth = 3f * unit,
                cap = StrokeCap.Round
            )
            drawLine(
                color = accentColor,
                start = point(58f, 87f),
                end = point(58f, 94f),
                strokeWidth = 3f * unit,
                cap = StrokeCap.Round
            )

            // Tai
            val leftEar = Path().apply {
                moveTo(33f * unit, 24f * unit)
                lineTo(29f * unit, 9f * unit)
                lineTo(43f * unit, 19f * unit)
                close()
            }
            drawPath(leftEar, bodyColor)

            val rightEar = Path().apply {
                moveTo(67f * unit, 24f * unit)
                lineTo(71f * unit, 9f * unit)
                lineTo(57f * unit, 19f * unit)
                close()
            }
            drawPath(rightEar, bodyColor)

            // Than + bung
            drawOval(
                color = bodyColor,
                topLeft = point(25f, 19f),
                size = area(50f, 70f)
            )
            drawOval(
                color = bellyColor,
                topLeft = point(36f, 48f),
                size = area(28f, 34f)
            )

            // Canh (dao theo wingLift)
            withTransform({ rotate(-wingLift, point(28f, 52f)) }) {
                drawOval(
                    color = bodyColor,
                    topLeft = point(15f, 40f),
                    size = area(13f, 34f)
                )
            }
            withTransform({ rotate(wingLift, point(72f, 52f)) }) {
                drawOval(
                    color = bodyColor,
                    topLeft = point(72f, 40f),
                    size = area(13f, 34f)
                )
            }

            // Mat: chieu cao co lai khi nhay mat
            val eyeTop = 40f - (20f * eyeHeight) / 2f
            drawOval(
                color = eyeColor,
                topLeft = Offset(30f * unit, eyeTop * unit),
                size = Size(20f * unit, 20f * eyeHeight * unit)
            )
            drawOval(
                color = eyeColor,
                topLeft = Offset(50f * unit, eyeTop * unit),
                size = Size(20f * unit, 20f * eyeHeight * unit)
            )

            // Dong tu (nhin len khi dang nghi)
            val pupilWidth = 9f
            val pupilHeight = 9f * eyeHeight
            drawOval(
                color = pupilColor,
                topLeft = Offset(
                    (35.5f + lookX - pupilWidth / 2f) * unit,
                    (40f + lookY - pupilHeight / 2f) * unit
                ),
                size = Size(pupilWidth * unit, pupilHeight * unit)
            )
            drawOval(
                color = pupilColor,
                topLeft = Offset(
                    (55.5f + lookX - pupilWidth / 2f) * unit,
                    (40f + lookY - pupilHeight / 2f) * unit
                ),
                size = Size(pupilWidth * unit, pupilHeight * unit)
            )

            // Mo
            val beak = Path().apply {
                moveTo(45f * unit, 49f * unit)
                lineTo(55f * unit, 49f * unit)
                lineTo(50f * unit, 58f * unit)
                close()
            }
            drawPath(beak, accentColor)

            if (thinking) {
                // Bong suy nghi: ba cham to dan
                drawCircle(
                    color = accentColor.copy(alpha = 0.55f + 0.35f * sparkle),
                    radius = 2.2f * unit,
                    center = point(79f, 20f)
                )
                drawCircle(
                    color = accentColor.copy(alpha = 0.45f + 0.35f * sparkle),
                    radius = 3.2f * unit,
                    center = point(86f, 12f)
                )
                drawCircle(
                    color = accentColor.copy(alpha = 0.35f + 0.35f * sparkle),
                    radius = 4.2f * unit,
                    center = point(94f, 3f)
                )
            }
        }

        if (celebrating) {
            drawSparkle(
                center = point(18f, 18f),
                radius = 4.5f * unit,
                color = sparkleColor,
                alpha = sparkle
            )
            drawSparkle(
                center = point(84f, 22f),
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
