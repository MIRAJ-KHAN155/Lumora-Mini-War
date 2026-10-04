package com.example.lumoraminiwar.subclass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.lumoraminiwar.baseclass.GameObject
import kotlin.math.hypot

class GameEngine {
    val gameObjects = mutableStateListOf<GameObject>()
    var currentLevel by mutableIntStateOf(1)
    var hasEnemiesSpawned = false

    fun addGameObject(obj: GameObject) {
        gameObjects.add(obj)
        if (obj is Enemy) {
            hasEnemiesSpawned = true
        }
    }

    fun isAllEnemiesDefeated(): Boolean {
        if (!hasEnemiesSpawned) return false
        val remainingEnemies = gameObjects.filterIsInstance<Enemy>().filter { !it.isDead }
        return remainingEnemies.isEmpty()
    }

    fun update() {

        val snapshot = gameObjects.toList()

        for (obj in snapshot) obj.update()

        val players = snapshot.filterIsInstance<Player>()
        val platforms = snapshot.filterIsInstance<Platform>()
        val bullets = snapshot.filterIsInstance<Bullet>()
        val enemies = snapshot.filterIsInstance<Enemy>()
        val bombs = snapshot.filterIsInstance<Bomb>()

        for (player in players) {
            player.isOnGround = false
            for (platform in platforms) {
                val playerLeft = player.x - 20f
                val playerRight = player.x + 20f
                val playerBottom = player.y + 28f
                val playerTop = player.y - 40f

                if (playerRight > platform.x &&
                    playerLeft < platform.x + platform.width &&
                    playerBottom > platform.y &&
                    playerTop < platform.y + platform.height &&
                    player.velocityY > 0 &&
                    playerBottom < platform.y + 30f
                ) {
                    player.y = platform.y - 28f
                    player.velocityY = 0f
                    player.isOnGround = true
                }
            }
        }

        for (enemy in enemies) {
            enemy.isOnGround = false
            for (platform in platforms) {
                if (enemy.x + 25f > platform.x && enemy.x - 25f < platform.x + platform.width &&
                    enemy.y + 35f > platform.y && enemy.y < platform.y + platform.height &&
                    enemy.velocityY > 0) {
                    enemy.y = platform.y - 35f
                    enemy.velocityY = 0f
                    enemy.isOnGround = true
                }
            }
        }

        for (bullet in bullets){
            if (!bullet.isPlayerBullet) continue
            for (enemy in enemies) {
                if (enemy.isDead) continue
                val distance = hypot(bullet.x - enemy.x, bullet.y - enemy.y)
                if (distance < 100f){
                    bullet.isDead = true
                    enemy.takeDamage(bullet.damage)
                }
            }
        }

        for (bomb in bombs){
            for (enemy in enemies){
                if (enemy.isDead) continue
                if (bomb.checkHit(enemy)){
                    enemy.isDead = true
                }
            }
        }

        for (player in players){
            if (player.isDead) continue
            for (enemy in enemies){
                if (enemy.isDead) continue
                val distance = hypot(player.x - enemy.x, player.y - enemy.y)
                if (distance < 70f) {
                    player.takeDamage(0.8f)
                }
            }
        }

        for (enemy in enemies) {
            if (enemy.isDead) continue
            val b = enemy.tryShoot()
            if (b != null) gameObjects.add(b)
        }

        for (bullet in bullets) {
            if (!bullet.isPlayerBullet) {
                for (player in players) {
                    if (!player.isDead && hypot(bullet.x - player.x, bullet.y - player.y) < 35f) {
                        bullet.isDead = true
                        player.takeDamage(15f)
                    }
                }
            }
        }

        gameObjects.removeAll { (it is Bullet && it.isDead) || (it is Enemy && it.isDead) || (it is Bomb && it.isDead)}
    }

    fun render(drawScope: DrawScope, cameraX: Float, screenWidth: Float) {
        for (obj in gameObjects) {
            val w = if (obj is Platform) obj.width else 100f
            if (obj.x + w < cameraX - 500f || obj.x > cameraX + screenWidth + 500f) continue
            obj.render(drawScope)
        }
    }
}