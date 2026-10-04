package com.example.lumoraminiwar.subclass

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.lumoraminiwar.baseclass.GameObject
import kotlin.math.atan2

class Bullet(
    x: Float,
    y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val isPlayerBullet: Boolean = true,
    val damage: Float = 10f
) : GameObject(x, y) {

    var isDead = false

    override fun update() {
        x += velocityX
        y += velocityY

        if (x < -1000 || x > 13000 || y < -1000 || y > 2000){
            isDead = true
        }
    }

    override fun render(drawScope: DrawScope) {
        val color = if (damage >= 25f) Color(0xFFFFEB3B) else Color.Yellow
        val centerX = x
        val centerY = y

        val angle = Math.toDegrees(atan2(velocityY.toDouble(), velocityX.toDouble())).toFloat()
        withScopeTransform(drawScope, angle,centerX,centerY,color)

    }
    private fun withScopeTransform(
        drawScope: DrawScope,
        angle: Float,
        centerX: Float,
        centerY: Float,
        color: Color
    ){
        drawScope.withTransform({
            rotate(degrees = angle, pivot = Offset(centerX, centerY))
        }){
            val bulletLength = 40f
            val bulletThickness = 6f

            drawRoundRect(
                color = color,
                topLeft = Offset(centerX - bulletLength / 2, centerY - bulletThickness / 2),
                size = Size(bulletLength, bulletThickness),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}