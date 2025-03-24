package com.example.shadowescape

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.scale
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpandingShadowEscapeGame()
        }
    }
}

@Composable
fun ExpandingShadowEscapeGame() {
    var playerX by remember { mutableStateOf(200f) }
    var playerY by remember { mutableStateOf(400f) }
    var shadowRadius by remember { mutableStateOf(50f) }
    var worldSize by remember { mutableStateOf(800f) }
    var playerSpeed by remember { mutableStateOf(5f) }
    var enemySpeed by remember { mutableStateOf(2f) }
    var gameOver by remember { mutableStateOf(false) }

    val enemies = remember {
        mutableStateListOf(
            Offset(500f, 700f),
            Offset(300f, 200f)
        )
    }

    LaunchedEffect(Unit) {
        while (!gameOver) {
            delay(100)
            shadowRadius += 5f
            worldSize += 5f
            playerSpeed += 0.1f
            enemySpeed += 0.05f

            if (shadowRadius > worldSize / 2) gameOver = true

            enemies.forEachIndexed { index, enemy ->
                val direction = Offset(
                    (playerX - enemy.x) * 0.02f * enemySpeed,
                    (playerY - enemy.y) * 0.02f * enemySpeed
                )
                enemies[index] = Offset(enemy.x + direction.x, enemy.y + direction.y)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Oyuncunun pozisyonunu güncelle
                    playerX = offset.x
                    playerY = offset.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            val scaleFactor = size.width / worldSize

            scale(scaleFactor, Offset(centerX, centerY)) {
                // Işık alanı
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.8f),
                    center = Offset(centerX, centerY),
                    radius = 100f
                )

                // Gölge alanı
                drawCircle(
                    color = Color.Black.copy(alpha = 0.7f),
                    center = Offset(centerX, centerY),
                    radius = shadowRadius
                )

                // Oyuncunun görselini çiz (resim yerine daire kullan)
                drawCircle(
                    color = Color.Cyan,
                    center = Offset(playerX, playerY),
                    radius = 20f
                )

                // Düşmanlar
                enemies.forEach { enemy ->
                    drawCircle(
                        color = Color.Red,
                        center = enemy,
                        radius = 25f
                    )
                }
            }
        }

        if (gameOver) {
            Text(
                text = "Game Over",
                color = Color.Red,
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp)

            )


        }

        }
    }
