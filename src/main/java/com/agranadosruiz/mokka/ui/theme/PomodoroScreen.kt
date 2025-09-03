package com.agranadosruiz.mokka

import DurationWheelPicker
import FlamesAnimation
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
    var selectedTime by remember { mutableIntStateOf(1) } // duración por defecto
    var timeLeft by remember { mutableIntStateOf(selectedTime * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(0) }
    var showDurationDialog by remember { mutableStateOf(true) }
    var isBreak by remember { mutableStateOf(false) }

    // --- Temporizador principal ---
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            timeLeft--
            if (timeLeft <= 0) {
                if (!isBreak) {
                    // Termina trabajo → iniciar break
                    completedSessions++
                    isBreak = true // primero cambia el estado
                } else {
                    // Termina break → volver a trabajo
                    isBreak = false
                    timeLeft = selectedTime * 60
                    isRunning = false
                }
            }
        }
    }

    // --- Ajusta duración del break cuando comienza ---
    LaunchedEffect(isBreak) {
        if (isBreak) {
            timeLeft = if (selectedTime == 25) 5 * 60 else 10 * 60
        }
    }

    // --- Modal inicial para elegir duración ---
    if (showDurationDialog) {
        Dialog(onDismissRequest = { /* no cerrar al tocar fuera */ }) {
            Card(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    DurationWheelPicker(
                        selectedTime = selectedTime,
                        onTimeSelected = { newTime ->
                            selectedTime = newTime
                            timeLeft = selectedTime * 60
                            isBreak = false
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { showDurationDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }

    // --- Pantalla principal ---
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Imagen según estado ---
                Box(
                    modifier = Modifier.height(250.dp).padding(bottom = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (!isRunning) {
                        Image(
                            painter = painterResource(id = R.drawable.mokkabackgroundless),
                            contentDescription = "Logo de la app",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (isBreak) {
                        Image(
                            painter = painterResource(id = R.drawable.cup2),
                            contentDescription = "Descanso con taza",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.coffee),
                            contentDescription = "Cafetera",
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Animaciones opcionales
                        Box(
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 0.dp, end = 20.dp)
                        ) {
                            BubblesAnimation(
                                areaWidth = 120.dp,
                                areaHeight = 120.dp,
                                modifier = Modifier.offset(x = (-30).dp, y = (-60).dp)
                            )
                        }
                        Box(
                            modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-35).dp)
                        ) {
                            FlamesAnimation(
                                areaWidth = 220.dp,
                                areaHeight = 100.dp,
                                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

                // --- Contador ---
                Text(
                    text = formatTime(timeLeft),
                    fontSize = 50.sp,
                    color = if (isBreak) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- Botones ---
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(if (isRunning) "Pausar" else if (isBreak) "Descanso" else "Iniciar")
                    }

                    Button(
                        onClick = {
                            isRunning = false
                            isBreak = false
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

                // --- Sesiones completadas ---
                Text(
                    text = "Sesiones completadas: $completedSessions",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}
