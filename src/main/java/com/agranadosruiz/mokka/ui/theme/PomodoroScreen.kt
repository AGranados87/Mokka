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
    // -----------------------
    // Estados principales
    // -----------------------
    var selectedTime by remember { mutableIntStateOf(25) }
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(0) }

    // Modo ciclo x4: Trabajo(25) Descanso(5) Trabajo(25) Descanso(5)
    var cycle4Enabled by remember { mutableStateOf(true) }

    // Plan de intervalos
    data class Interval(val type: SessionType, val durationSec: Int)

    fun buildPlan(): List<Interval> {
        return if (cycle4Enabled) {
            listOf(
                Interval(SessionType.WORK, 25 * 60),
                Interval(SessionType.BREAK, 5 * 60),
                Interval(SessionType.WORK, 25 * 60),
                Interval(SessionType.BREAK, 5 * 60),
            )
        } else {
            val breakSec = when (selectedTime) {
                25 -> 5 * 60
                50 -> 10 * 60
                else -> 10 * 60
            }
            listOf(
                Interval(SessionType.WORK, selectedTime * 60),
                Interval(SessionType.BREAK, breakSec)
            )
        }
    }

    var plan by remember { mutableStateOf(buildPlan()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var sessionType by remember { mutableStateOf(plan[currentIndex].type) }
    var timeLeft by remember { mutableIntStateOf(plan[currentIndex].durationSec) }

    var showDurationDialog by remember { mutableStateOf(!cycle4Enabled) }
    var waitingForBreak by remember { mutableStateOf(false) }

    // -----------------------
    // Reloj
    // -----------------------
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
    }

    // Transiciones al llegar a 0
    LaunchedEffect(timeLeft) {
        if (timeLeft == 0 && isRunning) {
            if (cycle4Enabled) {
                // Avance automático dentro del ciclo x4
                if (sessionType == SessionType.WORK) completedSessions++

                if (currentIndex < plan.lastIndex) {
                    val next = currentIndex + 1
                    currentIndex = next
                    sessionType = plan[next].type
                    timeLeft = plan[next].durationSec
                    // Auto-encadenado: sigue corriendo
                    isRunning = true
                } else {
                    // Fin de los 4 intervalos
                    isRunning = false
                }
            } else {
                // Modo manual (flujo anterior con popup)
                when (sessionType) {
                    SessionType.WORK -> {
                        completedSessions++
                        isRunning = false
                        waitingForBreak = true
                        // Prepara descanso sin arrancar
                        sessionType = SessionType.BREAK
                        timeLeft = plan[1].durationSec
                    }
                    SessionType.BREAK -> {
                        sessionType = SessionType.WORK
                        timeLeft = plan[0].durationSec
                        isRunning = false
                    }
                }
            }
        }
    }

    // Recalcular plan al cambiar opciones
    fun resetWithNewPlan() {
        plan = buildPlan()
        currentIndex = 0
        sessionType = plan[0].type
        timeLeft = plan[0].durationSec
        isRunning = false
        waitingForBreak = false
        showDurationDialog = !cycle4Enabled
    }

    LaunchedEffect(cycle4Enabled) {
        if (cycle4Enabled) selectedTime = 25
        resetWithNewPlan()
    }

    LaunchedEffect(selectedTime) {
        if (!cycle4Enabled) resetWithNewPlan()
    }

    // -----------------------
    // Popup “iniciar descanso” (solo modo manual)
    // -----------------------
    if (waitingForBreak && !cycle4Enabled) {
        Dialog(onDismissRequest = { /* No cerrar tocando fuera */ }) {
            Card(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Periodo de trabajo realizado", fontSize = 20.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "¿Quieres iniciar el periodo de descanso ahora?",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                isRunning = true
                                waitingForBreak = false
                            }
                        ) { Text("Iniciar descanso") }

                        Button(
                            onClick = { waitingForBreak = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) { Text("Más tarde") }
                    }
                }
            }
        }
    }

    // -----------------------
    // Modal de selección (solo modo manual)
    // -----------------------
    if (showDurationDialog && !cycle4Enabled) {
        Dialog(onDismissRequest = { /* No cerrar tocando fuera */ }) {
            Card(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Selecciona la duración de la sesión", fontSize = 20.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    DuracionTrabajo(
                        selectedTime = selectedTime,
                        onTimeSelected = { newTime ->
                            selectedTime = newTime
                        },
                        options = listOf(1, 25, 50)
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { showDurationDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }

    // -----------------------
    // UI principal
    // -----------------------
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
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

                // Cabecera pequeño toggle ciclo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Estado / bloque
                    val bloqueTxt =
                        if (cycle4Enabled) "Bloque ${currentIndex + 1} de ${plan.size}" else
                            if (sessionType == SessionType.WORK) "TRABAJO" else "DESCANSO"

                    Text(
                        text = bloqueTxt,
                        fontSize = 16.sp,
                        color = if (cycle4Enabled && sessionType == SessionType.WORK)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Botón modo ciclo x4
                    val cicloLabel = if (cycle4Enabled) "Ciclo x4: ON" else "Ciclo x4: OFF"
                    Button(
                        onClick = { cycle4Enabled = !cycle4Enabled },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (cycle4Enabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text(cicloLabel) }
                }

                Spacer(Modifier.height(12.dp))

                // Sección imagen/animaciones
                Box(
                    modifier = Modifier.height(250.dp).padding(bottom = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when {
                        sessionType == SessionType.WORK && isRunning -> {
                            Image(
                                painter = painterResource(id = R.drawable.coffee),
                                contentDescription = "Cafetera",
                                modifier = Modifier.fillMaxWidth()
                            )
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
                                Llamas(
                                    areaWidth = 220.dp,
                                    areaHeight = 100.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        sessionType == SessionType.BREAK -> {
                            Image(
                                painter = painterResource(id = R.drawable.cup2),
                                contentDescription = "Descanso con taza",
                                modifier = Modifier.fillMaxWidth()
                            )
                            AnimacionVapor(
                                anchoArea = 160.dp,
                                altoArea = 120.dp,
                                colorVapor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.35f),
                                posicionesX = listOf(0.46f, 0.5f, 0.54f),
                                modifier = Modifier.align(Alignment.TopCenter).offset(y = (-10).dp)
                            )
                        }

                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.mokkabackgroundless),
                                contentDescription = "Logo de la app",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Contador grande con pastilla blanca
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

                Spacer(Modifier.height(24.dp))

                // Botones
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            // Si está al final del ciclo x4, reiniciamos al pulsar iniciar
                            if (cycle4Enabled && !isRunning && currentIndex == plan.lastIndex && timeLeft == 0) {
                                resetWithNewPlan()
                            }
                            isRunning = !isRunning
                        },
                        enabled = !(waitingForBreak && !cycle4Enabled),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        val label = when {
                            isRunning -> "Pausar"
                            else -> if (sessionType == SessionType.BREAK) "Continuar" else "Iniciar"
                        }
                        Text(label)
                    }

                    Button(
                        onClick = {
                            resetWithNewPlan()
                            if (!cycle4Enabled) showDurationDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) { Text("Reiniciar") }
                }

                Spacer(Modifier.height(24.dp))

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
