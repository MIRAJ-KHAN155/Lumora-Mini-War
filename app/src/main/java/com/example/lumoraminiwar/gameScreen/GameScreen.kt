package com.example.lumoraminiwar.gameScreen


import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumoraminiwar.R
import com.example.lumoraminiwar.subclass.Enemy
import com.example.lumoraminiwar.subclass.GameEngine
import com.example.lumoraminiwar.subclass.Platform
import com.example.lumoraminiwar.subclass.Player
import com.example.lumoraminiwar.ui.theme.LumoraMiniWarTheme
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun GameScreen() {
    val gameEngine = remember { GameEngine() }
    val frame = remember { mutableLongStateOf(0L) }
    var mainPlayer by remember { mutableStateOf<Player?>(null) }

    var bombCount by remember { mutableStateOf(4) }
    var lastBombTime by remember { mutableStateOf(0L) }

    var isGameStarted by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isGameWin by remember { mutableStateOf(false) }

    val soldierImg = ImageBitmap.imageResource(id = R.drawable.soldier_jetpack)
    val enemyImg = ImageBitmap.imageResource(id = R.drawable.enemy_soldier_final)
    val platformLeft = ImageBitmap.imageResource(id = R.drawable.jungle_platform_left)
    val platformGround = ImageBitmap.imageResource(id = R.drawable.jungle_platform_long)

    fun loadLevel(level:Int){
        Log.d("GameDebug", "Loading Level:$level - Infinite")

        isGameOver = false
        isGameWin = false
        frame.longValue = 0

        gameEngine.gameObjects.clear()
        gameEngine.hasEnemiesSpawned = false
        gameEngine.currentLevel = level

        val player = Player(x = 550f, y = 750f, image = soldierImg)
        mainPlayer = player
        gameEngine.addGameObject(player)

        val baseEnemeyConut = 8
        val enemyCount = (baseEnemeyConut +  level / 5 ).coerceAtMost(15)

        val enemyHealthMultiplier = 1f + (level * 0.15f)

        var startX = 1200f
        for (i in 0 until enemyCount){
            val enemy = Enemy(
                x = startX,
                y = 600f,
                targetPlayer =  player,
                image = enemyImg
            )
            enemy.health = (enemy.health * enemyHealthMultiplier)

            gameEngine.addGameObject(enemy)
            startX += 700f
        }
        val mapLength = 8000f + (enemyCount * 600f)
        var gx = -500f
        while (gx < mapLength){
            gameEngine.addGameObject(Platform(gx, 1050f, 1200f, 500f, platformGround))
            gx += 1190f
        }

        var platformX = 0f
        repeat(10){
            platformX += 800f + Random.nextFloat() * 400f
            if (platformX > mapLength) return@repeat
            gameEngine.addGameObject(Platform(platformX, 700f + Random.nextFloat()*300f, 400f, 200f, platformLeft))
        }
        bombCount = (4 + level / 2).coerceAtMost(12)
        isGameStarted = true
    }

   LaunchedEffect(Unit) {
       while (true){
           awaitFrame()
           if (isGameStarted && !isGameOver && !isGameWin){
               gameEngine.update()
               frame.longValue++

               if (mainPlayer?.isDead == true){
                   isGameOver = true
               }
               if (gameEngine.isAllEnemiesDefeated()){
                   Log.d("GameDebug", "All Enemies Defeated! Level Win Triggered.")
                   isGameWin = true
               }
           }
       }
   }

    Box(Modifier.fillMaxSize()) {
        when {
            isGameWin -> {
                GameWinScreen(
                    currentLevel = gameEngine.currentLevel,
                    onPlayAgainClick = {
                        loadLevel(gameEngine.currentLevel + 1)
                    }
                )
            }
            isGameOver -> {
                GameOverScreen(
                    onRestartClick = {
                        loadLevel(1)
                    }
                )
            }
            isGameStarted -> {
                Box(Modifier.fillMaxSize().background(Color(0xFFB3E5FC))) {
                    Canvas(Modifier.fillMaxSize()) {
                        val cameraX = (mainPlayer?.x ?: 0f) - size.width / 2.5f
                        val cameraY = (mainPlayer?.y ?: 0f) - size.height / 1.6f
                        translate(-cameraX, -cameraY) {

                            gameEngine.render(this, cameraX, size.width)

                        }
                    }

                    Row(
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 70.dp, top = 15.dp)
                            .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Box(Modifier.size(120.dp, 10.dp).background(Color.Gray)) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth((mainPlayer?.health ?: 0f) / (mainPlayer?.maxHealth ?: 100f))
                                        .background(Color(0xFFD946EF))
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Box(Modifier.size(120.dp, 8.dp).background(Color.Gray)) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth((mainPlayer?.armor ?: 0f) / (mainPlayer?.maxArmor ?: 100f))
                                        .background(Color(0xFF3B82F6))
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("x${mainPlayer?.lives ?: 3}", color = Color.White, fontSize = 14.sp)
                    }

                    Joystick(Modifier.align(Alignment.BottomStart).padding(30.dp)) { offset ->
                        mainPlayer?.let {
                            if (offset.x < -0.25f) it.moveLeft()
                            else if (offset.x > 0.25f) it.moveRight()
                            else it.stopMoving()
                            if (offset.y < -0.4f) it.startJetpack() else it.stopJetpack()
                        }
                    }

                    Button(
                        onClick = {
                            val now = System.currentTimeMillis()
                            if (now - lastBombTime > 1000 && bombCount > 0) {
                                lastBombTime = now
                                mainPlayer?.let {
                                    val bomb = it.throwBomb()
                                    gameEngine.addGameObject(bomb)
                                    bombCount--
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 160.dp, bottom = 50.dp)
                            .size(65.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.6f))
                    ) {
                        Text(text = "B\n${bombCount}", fontSize = 12.sp, color = Color.Black)
                    }

                    Joystick(Modifier.align(Alignment.BottomEnd).padding(40.dp)) { fireOffset ->
                        val power = hypot(fireOffset.x, fireOffset.y)
                        if (power > 0.6f) {
                            mainPlayer?.let { player ->
                                player.updateFacing(fireOffset.x, fireOffset.y)
                                val bullet = player.shoot(fireOffset.x, fireOffset.y)
                                bullet?.let { gameEngine.addGameObject(it) }
                            }
                        }
                    }
                }
            }
            else -> {
                StartScreen(
                    currentLevel = gameEngine.currentLevel,
                    onStartClick = { loadLevel(1) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LumoraMiniWarTheme {
        GameScreen()
    }
}