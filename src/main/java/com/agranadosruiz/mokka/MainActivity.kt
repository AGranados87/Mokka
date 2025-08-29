package com.agranadosruiz.mokka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agranadosruiz.mokka.ui.theme.MokkaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MokkaTheme {
                PomodoroApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroApp() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mokka Pomodoro") }) }
    ) { padding ->
        PomodoroScreen(modifier = Modifier.padding(padding))
    }
}

@Composable
fun PomodoroScreen(modifier: Modifier = Modifier) {
    var timeLeft by remember { mutableStateOf(25 * 60) } // 25 minutos en segundos
    var isRunning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Temporizador
        Text(
            text = formatTime(timeLeft),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de control
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                isRunning = !isRunning
                if (isRunning) {
                    scope.launch {
                        while (isRunning && timeLeft > 0) {
                            delay(1000)
                            timeLeft -= 1
                        }
                        isRunning = false
                    }
                }
            }) {
                Text(if (isRunning) "Pausar" else "Iniciar")
            }

            Button(onClick = {
                isRunning = false
                timeLeft = 25 * 60
            }) {
                Text("Reiniciar")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Estadísticas simples
        Text("Sesiones completadas: 0") // Más adelante puedes vincularlo a un estado
    }
}

fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}

@Preview(showBackground = true)
@Composable
fun PomodoroPreview() {
    MokkaTheme {
        PomodoroScreen()
    }
}
