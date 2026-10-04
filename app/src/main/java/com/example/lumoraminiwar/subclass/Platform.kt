package com.example.lumoraminiwar.subclass

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.lumoraminiwar.baseclass.GameObject

class Platform(
    x: Float, y: Float,
    val width: Float, val height: Float,
    val image: ImageBitmap? = null // PNG থাকলে এটা ব্যবহার হবে
) : GameObject(x, y) {
    override fun update() {}
    override fun render(drawScope: DrawScope) {
        with(drawScope) {
            if (image != null) {
                drawImage(image, dstOffset = androidx.compose.ui.unit.IntOffset(x.toInt(), y.toInt()),
                    dstSize = androidx.compose.ui.unit.IntSize(width.toInt(), height.toInt()))
            } else {
                // fallback - আগের মতো
                drawRect(androidx.compose.ui.graphics.Color(0xFF8D7B6F), Offset(x,y), androidx.compose.ui.geometry.Size(width, height))
            }
        }
    }
}