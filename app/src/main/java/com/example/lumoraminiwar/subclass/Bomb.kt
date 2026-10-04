package com.example.lumoraminiwar.subclass

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.lumoraminiwar.baseclass.GameObject
import kotlin.math.hypot

class Bomb(
    x: Float,
    y: Float,
    velocityX: Float,
    velocityY: Float
) : GameObject(x, y) {

    var velX = velocityX
    var velY = velocityY
    var isDead = false
    var isExploding = false
    private var explodeTimer = 0f
    private var lifeTime = 0f
    private val gravity = 0.5f
    var explosionRadius = 0f

    private val bombColor = Color(0xFFFFD700)

    override fun update() {
        if (!isExploding) {
            velY += gravity
            x += velX
            y += velY
            velX *= 0.98f

            lifeTime += 1f
            if (lifeTime > 80f) {
                startExplode()
            }
            // niche pore gele fatbe
            if (y > 1000f) {
                y = 1000f
                startExplode()
            }
        } else {
            explodeTimer += 1f
            // boro hobe aste aste
            explosionRadius = explodeTimer * 12f
            if (explodeTimer > 40f) { // age 25 chilo, ekhon 40 - onekkhon dekhabe
                isDead = true
            }
        }
    }

    fun startExplode() {
        if (!isExploding) {
            isExploding = true
            explodeTimer = 0f
        }
    }

    fun checkHit(enemy: Enemy): Boolean {
        if (!isExploding) return false
        val dist = hypot(x - enemy.x, y - enemy.y)
        return dist < explosionRadius
    }

    override fun render(drawScope: DrawScope) {
        with(drawScope) {
            if (!isExploding) {
                drawCircle(Color.Black, 16f, Offset(x, y))
                drawCircle(bombColor, 11f, Offset(x, y))
                drawCircle(Color.Red, 6f, Offset(x, y - 18f))
                drawCircle(Color.Yellow, 4f, Offset(x, y - 18f))
            } else {
                // Blast - onek boro ar ujjol korlam
                val progress = explodeTimer / 40f
                val alpha = 1f - progress

                // 1. Sobcheye boro lal glow - dure thekeo dekhabe
                drawCircle(
                    Color.Red.copy(alpha = alpha * 0.4f),
                    explosionRadius * 1.3f,
                    Offset(x, y)
                )
                // 2. Komola bairer
                drawCircle(
                    Color(0xFFFF6A00).copy(alpha = alpha * 0.6f),
                    explosionRadius,
                    Offset(x, y)
                )
                // 3. Holud majher - tomar bullet er moto
                drawCircle(
                    bombColor.copy(alpha = alpha * 0.8f),
                    explosionRadius * 0.75f,
                    Offset(x, y)
                )
                // 4. Vetorer sada agun
                drawCircle(
                    Color.White.copy(alpha = alpha),
                    explosionRadius * 0.45f,
                    Offset(x, y)
                )
                // 5. Ekdom vetore ujjol holud
                if (explodeTimer < 15f) {
                    drawCircle(
                        Color.White,
                        25f - explodeTimer,
                        Offset(x, y)
                    )
                }
            }
        }
    }
}