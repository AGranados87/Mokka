import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimacionVapor(
    modifier: Modifier = Modifier,
    anchoArea: Dp = 160.dp,
    altoArea: Dp = 120.dp,
    colorVapor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
    posicionesX: List<Float> = listOf(0.46f, 0.5f, 0.54f, 0.54f, 0.54f, 0.54f),
    amplitudOndulacionPx: Float = 18f,
    distanciaEntreBurbujasPx: Float = 16f,
    numBurbujasPorColumna: Int = 8) {
    val transicion = rememberInfiniteTransition(label = "vapor")
    val columnas = remember {
        posicionesX.map { pos ->
            ColumnaVapor(
                posicionX = pos,
                desfaseOnda = Random.nextFloat() * (2f * Math.PI.toFloat()),
                duracionMs = Random.nextInt(3200, 4200)
            )
        }
    }

    Box(modifier = modifier.size(anchoArea, altoArea)) {
        columnas.forEachIndexed { idx, columna ->
            val yAnim = transicion.animateFloat(
                initialValue = 1.1f,  // empieza un poco más abajo del área
                targetValue = -0.2f,  // se va por arriba
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = columna.duracionMs, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "vaporY_$idx"
            )
            val onda = transicion.animateFloat(
                initialValue = 0f,
                targetValue = 2f * Math.PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = columna.duracionMs - 400, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "vaporOnda_$idx"
            )

            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                // X oscila alrededor de la posición base de la columna
                val baseX = w * columna.posicionX
                val x = baseX + sin(onda.value + columna.desfaseOnda) * amplitudOndulacionPx
                val yBase = h * yAnim.value

                for (i in 0 until numBurbujasPorColumna) {
                    val offsetY = i * distanciaEntreBurbujasPx
                    val radio = 8f + i * 3f
                    val alphaFactor = (1f - i * (1f / (numBurbujasPorColumna + 1))).coerceIn(0f, 1f)

                    drawCircle(
                        color = colorVapor.copy(alpha = colorVapor.alpha * alphaFactor),
                        radius = radio,
                        center = Offset(x, yBase - offsetY)
                    )
                }
            }
        }
    }
}

private data class ColumnaVapor(
    val posicionX: Float,
    val desfaseOnda: Float,
    val duracionMs: Int
)
