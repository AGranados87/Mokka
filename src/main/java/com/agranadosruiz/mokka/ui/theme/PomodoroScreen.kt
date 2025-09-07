package com.agranadosruiz.mokka

import DuracionTrabajo
import Llamas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen(modifier: Modifier = Modifier) {
    // Estados principales
    var selectedTime by remember { mutableIntStateOf(25) } // Tiempo de trabajo en minutos
    var timeLeft by remember { mutableIntStateOf(selectedTime * 60) } // Tiempo restante en segundos
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(0) }
    var showDurationDialog by remember { mutableStateOf(true) }
    var sessionType by remember { mutableStateOf(SessionType.WORK) } // Enum para mejor control
    var waitingForBreak by remember { mutableStateOf(false) } // Estado de espera para iniciar descanso

    // Función para obtener la duración del descanso
    fun getBreakDuration(workMinutes: Int): Int {
        return when (workMinutes) {
            1 -> 20  // 20 segundos para pruebas
            25 -> 5 * 60  // 5 minutos
            50 -> 10 * 60  // 10 minutos
            else -> 10 * 60  // Por defecto 10 minutos
        }
    }

    // Temporizador principal
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
    }

    // Manejo de transiciones cuando el tiempo llega a 0
    LaunchedEffect(timeLeft) {
        if (timeLeft == 0 && isRunning) {
            when (sessionType) {
                SessionType.WORK -> {
                    // Termina el trabajo, inicia el descanso
                    completedSessions++
                    sessionType = SessionType.BREAK
                    timeLeft = getBreakDuration(selectedTime)
                }
                SessionType.BREAK -> {
                    // Termina el descanso, vuelve al trabajo
                    sessionType = SessionType.WORK
                    timeLeft = selectedTime * 60
                    isRunning = false
                }
            }
        }
    }

    // Modal de selección de duración
    if (showDurationDialog) {
        Dialog(onDismissRequest = { /* No cerrar al tocar fuera */ }) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Selecciona la duración de la sesión",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DuracionTrabajo(
                        selectedTime = selectedTime,
                        onTimeSelected = { newTime ->
                            selectedTime = newTime
                            timeLeft = selectedTime * 60
                            sessionType = SessionType.WORK
                        },
                        options = listOf(1, 25, 50) // Opciones disponibles
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showDurationDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }

    // Pantalla principal
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sección de imagen
                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when {
                        !isRunning && sessionType == SessionType.WORK -> {
                            // No está corriendo, muestra el logo
                            Image(
                                painter = painterResource(id = R.drawable.mokkabackgroundless),
                                contentDescription = "Logo de la app",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        sessionType == SessionType.BREAK -> {
                            // En descanso, muestra la taza
                            Image(
                                painter = painterResource(id = R.drawable.cup2),
                                contentDescription = "Descanso con taza",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        sessionType == SessionType.WORK && isRunning -> {
                            // Trabajando, muestra la cafetera con animaciones
                            Image(
                                painter = painterResource(id = R.drawable.coffee),
                                contentDescription = "Cafetera",
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Animación de burbujas
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 0.dp, end = 20.dp)
                            ) {
                                BubblesAnimation(
                                    areaWidth = 120.dp,
                                    areaHeight = 120.dp,
                                    modifier = Modifier.offset(x = (-30).dp, y = (-60).dp)
                                )
                            }

                            // Animación de llamas
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (-35).dp)
                            ) {
                                Llamas(
                                    areaWidth = 220.dp,
                                    areaHeight = 100.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }

                // Texto de estado (TRABAJO o DESCANSO)
                Text(
                    text = when {
                        waitingForBreak -> "¡SESIÓN COMPLETADA!"
                        sessionType == SessionType.WORK -> "TRABAJO"
                        sessionType == SessionType.BREAK -> "DESCANSO"
                        else -> ""
                    },
                    fontSize = 16.sp,
                    color = if (waitingForBreak) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contador de tiempo - CON FONDO PARA GARANTIZAR VISIBILIDAD
                Box(
                    modifier = Modifier
                        .background(
                            color = androidx.compose.ui.graphics.Color(0xFFFFFFFF), // Beige de fondo
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 50.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF4E342E), // Marrón oscuro
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botones de control
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = when {
                                !isRunning && sessionType == SessionType.WORK -> "Iniciar"
                                !isRunning && sessionType == SessionType.BREAK -> "Continuar"
                                isRunning -> "Pausar"
                                else -> "Iniciar"
                            }
                        )
                    }

                    Button(
                        onClick = {
                            isRunning = false
                            sessionType = SessionType.WORK
                            timeLeft = selectedTime * 60
                            showDurationDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text("Reiniciar")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Contador de sesiones completadas
                Text(
                    text = "Sesiones completadas: $completedSessions",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )
            }
        }
    }
}

// Enum para los tipos de sesión
enum class SessionType {
    WORK,
    BREAK
}

// Función para formatear el tiempo
fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}