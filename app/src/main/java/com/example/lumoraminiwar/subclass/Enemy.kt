package com.example.lumoraminiwar.subclass

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.lumoraminiwar.baseclass.GameObject

class Enemy(
    x: Float,
    y: Float,
    val speed: Float = 2f,
    private val targetPlayer: Player,
    var image: ImageBitmap? = null
) : GameObject(x, y) {
    var health = 50f
    var isDead = false
    var velocityX = 0f
    var velocityY = 0f
    var isOnGround = false
    private val gravity = 0.6f

    var facingRight = true
    private var lastShootTime = 0L

    // এনিমির সাইজ
    private val enemyWidth = 90
    private val enemyHeight = 100

    fun takeDamage(dmg: Float) {
        health -= dmg
        if (health <= 0) isDead = true
    }

    fun tryShoot(): Bullet? {
        val now = System.currentTimeMillis()
        if (now - lastShootTime < 1500) return null

        val dx = targetPlayer.x - x
        val dy = targetPlayer.y - y
        val dist = kotlin.math.hypot(dx, dy)

        // ৪৫০ পিক্সেলের মধ্যে আসলে ফায়ার করবে
        if (dist > 450f) return null

        // গুলি করার সময় প্লেয়ারের দিকে দিক ঠিক করা
        facingRight = dx >= 0

        lastShootTime = now

        // এনিমি ডানে থাকলে ডান পাশের বন্দুক থেকে, বামে থাকলে বাম পাশ থেকে গুলি বের হবে
        val bulletStartX = x + if (facingRight) 50f else -50f
        val bulletStartY = y + 10f
        return Bullet(bulletStartX, bulletStartY, (dx / dist) * 12f, (dy / dist) * 12f, false, 15f)
    }

    override fun update() {
        if (!isOnGround) velocityY += gravity
        y += velocityY
        x += velocityX

        // প্লেয়ার কোন দিকে আছে তা দেখে ডিরেকশন ঠিক করা
        val dx = targetPlayer.x - x
        facingRight = dx >= 0

        // প্লেয়ারের কাছাকাছি (৬০০ পিক্সেল) আসলে ধাওয়া করবে
        if (kotlin.math.abs(dx) < 700f) {
            val direction = if (facingRight) 1 else -1
            velocityX = direction * speed * 1.5f
        } else {
            velocityX = 0f
        }

        isOnGround = false
    }

    override fun render(drawScope: DrawScope) {
        with(drawScope) {
            val img = image
            if (img != null) {
                // এনিমির সেন্টার পয়েন্ট হিসাব করা
                val centerX = x
                val centerY = y

                withTransform({
                    if (!facingRight) {
                        // পুরো বডি সেন্টার পিনপয়েন্ট ধরে বামে ফ্লিপ হবে
                        scale(scaleX = -1f, scaleY = 1f, pivot = Offset(centerX, centerY))
                    }
                }) {
                    drawImage(
                        image = img,
                        dstOffset = IntOffset((centerX - enemyWidth / 2).toInt(), (centerY - enemyHeight / 2).toInt()),
                        dstSize = IntSize(enemyWidth, enemyHeight)
                    )
                }
            } else {
                // ইমেজ না থাকলে লাল গোলক
                drawCircle(Color.Red, 25f, Offset(x, y))
            }
        }
    }
}