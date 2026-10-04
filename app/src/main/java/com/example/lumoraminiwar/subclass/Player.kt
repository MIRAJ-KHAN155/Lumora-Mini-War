package com.example.lumoraminiwar.subclass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.lumoraminiwar.baseclass.GameObject
import kotlin.math.atan2

class Player(
    x: Float,
    y: Float,
    var image: ImageBitmap? = null
) : GameObject(x, y) {

    var velocityX = 0f
    var velocityY = 0f
    var isOnGround = false
    var isJetpacking = false
    var facingRight = true
    var aimAngle = 0f
    var isAiming = false

    var maxHealth = 100f
    var health by mutableFloatStateOf(100f)
    var armor by mutableFloatStateOf(100f)
    var maxArmor = 100f
    var lives = 3
    var isDead = false

    var currentGun = "AK"
    var lastShotTime = 0L
    private val fireRate = 200L

    private val gravity = 0.6f
    private val jetpackPower = -1.2f

    private val playerWidth = 120
    private val playerHeight = 140

    fun updateFacing(dirX: Float, dirY: Float) {
        if (kotlin.math.hypot(dirX, dirY) < 0.2f) return
        isAiming = true
        aimAngle = Math.toDegrees(atan2(dirY, dirX).toDouble()).toFloat()
        if (dirX > 0.2f) facingRight = true
        if (dirX < -0.2f) facingRight = false
    }

    override fun update() {
        if (isDead) return

        if (!isJetpacking) velocityY += gravity else velocityY += jetpackPower
        velocityY = velocityY.coerceIn(-12f, 15f)
        x += velocityX
        y += velocityY
        velocityX *= 0.9f

        // 💀 স্ক্রিনের নিচে খাদে পড়লে রিসেট লজিক
        if (y > 1350f) {
            respawnOrDie()
        }
    }

    // খাদে পড়লে রিস্পন করার নিবেদিত মেথড
    private fun respawnOrDie() {
        lives--
        if (lives <= 0) {
            isDead = true
            health = 0f
            armor = 0f
        } else {
            health = maxHealth
            armor = 30f
            x = 550f
            y = 750f
            velocityX = 0f
            velocityY = 0f
            isJetpacking = false
        }
    }

    fun moveLeft() { velocityX = -8f; if (!isAiming) facingRight = false }
    fun moveRight() { velocityX = 8f; if (!isAiming) facingRight = true }

    fun shoot(dirX: Float = 0f, dirY: Float = 0f): Bullet? {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShotTime < fireRate) return null

        var speedX: Float
        var speedY: Float
        var damage: Float

        if (dirX == 0f && dirY == 0f) {
            speedX = if (facingRight) 18f else -18f
            speedY = 0f
        } else {
            speedX = dirX * 18f
            speedY = dirY * 18f
        }

        if (currentGun == "AK") {
            speedX *= 1.3f
            speedY *= 1.3f
            damage = 25f
        } else {
            damage = 12f
        }

        lastShotTime = currentTime

        val startX = x + if (facingRight) 25f else -25f
        val startY = y + 15f
        return Bullet(startX, startY, speedX, speedY, true, damage)
    }

    fun startJetpack() { isJetpacking = true }
    fun stopJetpack() { isJetpacking = false }

    override fun render(drawScope: DrawScope) {
        if (isDead) return

        with(drawScope) {
            val img = image
            if (img != null) {
                val centerX = x
                val centerY = y

                withTransform({
                    if (!facingRight) {
                        scale(scaleX = -1f, scaleY = 1f, pivot = Offset(centerX, centerY))
                    }
                }) {
                    drawImage(
                        image = img,
                        dstOffset = IntOffset((centerX - playerWidth / 2).toInt(), (centerY - playerHeight / 2).toInt()),
                        dstSize = IntSize(playerWidth, playerHeight)
                    )

                    if (isJetpacking) {
                        drawCircle(Color(0xFFFF6A00), 12f, Offset(centerX - 25f, centerY + 35f))
                        drawCircle(Color.Yellow, 7f, Offset(centerX - 25f, centerY + 40f))
                    }
                }
            } else {
                drawCircle(Color.Green, 20f, Offset(x, y))
            }
        }
    }

    fun throwBomb(): Bomb {
        val bombSpeedX = if (facingRight) 10f else -10f
        return Bomb(x, y - 20f, bombSpeedX, -10f)
    }

    fun takeDamage(damage: Float) {
        if (isDead) return
        if (armor > 0) {
            armor -= damage * 0.7f
            health -= damage * 0.3f
            if (armor < 0) { health += armor; armor = 0f }
        } else { health -= damage }

        if (health <= 0) {
            respawnOrDie()
        }
    }
    fun stopMoving() {
        velocityX = 0f
    }
}