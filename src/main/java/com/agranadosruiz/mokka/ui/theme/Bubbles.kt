
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun BubblesAnimation() {
    val bubbleCount = 6
    val bubbles = List(bubbleCount) {
        Bubble(
            xOffset = Random.nextFloat(),
            size = Random.nextFloat() * 12 + 8
        )
    }

    bubbles.forEach { bubble ->
        val infiniteTransition = rememberInfiniteTransition()
        val yAnim by infiniteTransition.animateFloat(
            initialValue = 220f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = (2000..4000).random(), easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        Canvas(
            modifier = Modifier
                .size(bubble.size.dp)
                .graphicsLayer {
                    translationX = bubble.xOffset * 180f  // posición horizontal
                    translationY = yAnim                  // posición vertical animada
                }
        ) {
            drawCircle(color = Color.White.copy(alpha = 0.6f))
        }

    }
}

data class Bubble(
    val xOffset: Float,
    val size: Float
)
