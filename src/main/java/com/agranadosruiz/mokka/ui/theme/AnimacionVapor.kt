import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun AnimacionVapor(
    modifier: Modifier = Modifier,
    anchoArea: Dp = 200.dp,
    altoArea: Dp = 150.dp,
    colorVapor: Color = Color.White.copy(alpha = 0.3f)
) {
    val transicionInfinita = rememberInfiniteTransition()

    // Crear múltiples columnas de vapor
    val columnasVapor = remember {
        List(3) { indice ->
            ColumnaVapor(
                posicionX = 0.3f + (indice * 0.2f), // Distribuir las columnas
                desfaseOnda = indice * 0.5f // Diferentes fases para cada columna
            )
        }
    }

    Box(modifier = modifier.size(anchoArea, altoArea)) {
        columnasVapor.forEachIndexed { indice, columna ->
            // Animación de movimiento vertical
            val posicionY = transicionInfinita.animateFloat(
                initialValue = 1f,
                targetValue = -0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000 + (indice * 500), // Diferentes velocidades
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "vaporY_$indice"
            )

            // Animación de ondulación
            val ondulacion = transicionInfinita.animateFloat(
                initialValue = 0f,
                targetValue = 2f * Math.PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000 + (indice * 300),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "vaporOnda_$indice"
            )

            // Animación de transparencia
            val transparencia = transicionInfinita.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 4000 + (indice * 500)
                        0f at 0 with LinearEasing
                        0.6f at 500 with LinearEasing
                        0.6f at 3000 with LinearEasing
                        0f at 4000 with LinearEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "vaporAlpha_$indice"
            )

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val anchoPantalla = size.width
                val altoPantalla = size.height

                // Calcular posición X con ondulación
                val x = anchoPantalla * columna.posicionX +
                        sin(ondulacion.value + columna.desfaseOnda) * 20f

                // Calcular posición Y
                val y = altoPantalla * posicionY.value

                // Dibujar la columna de vapor como una serie de círculos difuminados
                for (i in 0..5) {
                    val offsetY = i * 15f
                    val radio = 8f + i * 3f // Radio aumenta mientras sube
                    val alpha = transparencia.value * (1f - i * 0.15f) // Se desvanece mientras sube

                    drawCircle(
                        color = colorVapor.copy(alpha = colorVapor.alpha * alpha),
                        radius = radio,
                        center = Offset(x, y - offsetY)
                    )
                }
            }
        }
    }
}

// Clase de datos para cada columna de vapor
data class ColumnaVapor(
    val posicionX: Float,
    val desfaseOnda: Float
)