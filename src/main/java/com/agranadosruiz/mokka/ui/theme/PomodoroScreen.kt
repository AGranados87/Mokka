package com.agranadosruiz.mokka

import AnimacionVapor
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
    var selectedTime by remember { mutableIntStateOf(25) }         // Trabajo (min)
    var timeLeft by remember { mutableIntStateOf(selectedTime * 60) } // Segundos restantes
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(0) }
    var showDurationDialog by remember { mutableStateOf(true) }
    var sessionType by remember { mutableStateOf(SessionType.WORK) }
    var waitingForBreak by remember { mutableStateOf(false) }       // Mostrar popup para iniciar descanso

    // Duración del descanso
    fun getBreakDuration(workMinutes: Int): Int {
        return when (workMinutes) {
            1 -> 20            // 20s para pruebas
            25 -> 5 * 60       // 5 min
            50 -> 10 * 60      // 10 min
            else -> 10 * 60
        }
    }

    // Temporizador
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
    }

    // Transiciones al llegar a 0
    LaunchedEffect(timeLeft) {
        if (timeLeft == 0 && isRunning) {
            when (sessionType) {
                SessionType.WORK -> {
                    // Termina el trabajo -> NO arrancar descanso todavía
                    completedSessions++
                    isRunning = false
                    waitingForBreak = true
                    timeLeft = getBreakDuration(selectedTime) // Preparamos el descanso pero pausado
                    // no cambiamos sessionType hasta que el usuario pulse el botón
                }
                SessionType.BREAK -> {
                    // Termina descanso -> Volver a trabajo y pausar
                    sessionType = SessionType.WORK
                    timeLeft = selectedTime * 60
                    isRunning = false
                }
            }
        }
    }

    // Popup para iniciar descanso tras terminar trabajo
    if (waitingForBreak) {
        Dialog(onDismissRequest = { /* Evitar cerrar tocando fuera */ }) {
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
                        text = "Periodo de trabajo realizado",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¿Quieres iniciar el periodo de descanso ahora?",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                // Arranca el descanso cuando el usuario confirme
                                sessionType = SessionType.BREAK
                                isRunning = true
                                waitingForBreak = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Iniciar descanso")
                        }

                        Button(
                            onClick = {
                                // Opción: posponer (cerrar popup y quedarse en pausa)
                                waitingForBreak = false

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text("Más tarde")
                        }
                    }
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
                        options = listOf(1, 25, 50)
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
                            // Logo cuando está parado en trabajo
                            Image(
                                painter = painterResource(id = R.drawable.mokkabackgroundless),
                                contentDescription = "Logo de la app",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        sessionType == SessionType.BREAK -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                // Taza
                                Image(
                                    painter = painterResource(id = R.drawable.cup2),
                                    contentDescription = "Descanso con taza",
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Vapor en columnas sobre la boquilla
                                AnimacionVapor(
                                    anchoArea = 160.dp,
                                    altoArea = 120.dp,
                                    colorVapor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.35f),
                                    posicionesX = listOf(0.46f, 0.5f, 0.54f),
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = (-10).dp) // ajusta para clavar la salida en la boquilla
                                )
                            }
                        }
                        sessionType == SessionType.WORK && isRunning -> {
                            // Trabajando -> cafetera + animaciones
                            Image(
                                painter = painterResource(id = R.drawable.coffee),
                                contentDescription = "Cafetera",
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Burbujas
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

                            // Llamas
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

                // Texto de estado
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

                // Contador de tiempo (con fondo para legibilidad)
                Box(
                    modifier = Modifier
                        .background(
                            color = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 50.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF4E342E),
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
                        enabled = !waitingForBreak, // Evitar que arranquen mientras mostramos el popup
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
                            waitingForBreak = false
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

                // Contador de sesiones
                Text(
                    text = "Sesiones completadas: $completedSessions",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )
            }
        }
    }
}

enum class SessionType { WORK, BREAK }

fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}
