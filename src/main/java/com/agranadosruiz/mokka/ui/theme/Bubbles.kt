package com.agranadosruiz.mokka

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BubblesAnimation(
    modifier: Modifier = Modifier,
    areaWidth: Dp = 120.dp,
    areaHeight: Dp = 120.dp
) {
    val bubbleCount = 10
    val infinite = rememberInfiniteTransition(label = "bubbles")

    val bubbles = remember {
        List(bubbleCount) {
            Bubble(
                xOffset = Random.nextInt(-40, 40).toFloat(),   // dispersión horizontal base
                drift = Random.nextInt(-12, 12).toFloat(),      // amplitud de vaivén
                size = Random.nextInt(8, 16).dp,                // tamaño en dp
                duration = Random.nextInt(1800, 3600),          // ms
                delay = Random.nextInt(0, 1500)                 // ms
            )
        }
    }

    // Un "progreso" 0..1 por burbuja (composable)
    val progresses = bubbles.mapIndexed { i, b ->
        infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = b.duration,
                    delayMillis = b.delay,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "bubbleProgress$i"
        )
    }

    Canvas(modifier = modifier.size(areaWidth, areaHeight)) {
        bubbles.forEachIndexed { i, b ->
            val t = progresses[i].value
            val y = size.height * (1f - t)
            val alpha = 1f - t                        // se desvanece al subir
            val x = size.width / 2f +
                    b.xOffset +
                    b.drift * sin(2 * PI * t).toFloat()

            drawCircle(
                color = Color.LightGray.copy(alpha = alpha),
                radius = b.size.toPx() / 2f,
                center = Offset(x, y)
            )
        }
    }
}

data class Bubble(
    val xOffset: Float,
    val drift: Float,
    val size: Dp,
    val duration: Int,
    val delay: Int
)
