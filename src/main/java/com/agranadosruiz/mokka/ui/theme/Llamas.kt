import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Llamas(
    modifier: Modifier = Modifier,
    areaWidth: Dp = 120.dp,
    areaHeight: Dp = 60.dp

) {
    val infiniteTransition = rememberInfiniteTransition(label = "flames")

    val flameHeight1 by infiniteTransition.animateFloat(
        initialValue = 12f, targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "flameHeight1"
    )
    val flameHeight2 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "flameHeight2"
    )
    val flameHeight3 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "flameHeight3"
    )

    Canvas(modifier = modifier.size(areaWidth, areaHeight)) {
        val centerX = size.width / 2
        val baseY = size.height
        val offsetX = -8f

        for (i in -4..4) {
            drawCircle(
                color = Color(0xFFD32F2F).copy(alpha = 0.6f),
                radius = flameHeight1,
                center = Offset(centerX + i * 20f + offsetX, baseY - flameHeight1 * 1.2f)
            )
        }

        for (i in -4..4) {
            drawCircle(
                color = Color(0xFFFF6F00).copy(alpha = 0.7f),
                radius = flameHeight2,
                center = Offset(centerX + i * 20f + offsetX, baseY - flameHeight2 * 2f)
            )
        }

        for (i in -4..4) {
            drawCircle(
                color = Color(0xFFFFEB3B).copy(alpha = 0.8f),
                radius = flameHeight3,
                center = Offset(centerX + i * 20f + offsetX, baseY - flameHeight3 * 3f)
            )
        }

        //Quemador
        val burnerWidth = size.width * 0.5f
        val burnerXStart = (size.width - burnerWidth) / 2 + offsetX
        val burnerXEnd = burnerXStart + burnerWidth
        drawLine(
            color = Color.Gray,
            start = Offset(burnerXStart, baseY),
            end = Offset(burnerXEnd, baseY),
            strokeWidth = 30f                     // grosor
        )

    }
}

