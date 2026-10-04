package com.example.lumoraminiwar.baseclass

import androidx.compose.ui.graphics.drawscope.DrawScope

abstract class GameObject(
    var x: Float,
    var y: Float
) {
    abstract fun update()

    abstract  fun render(drawScope: DrawScope)
}