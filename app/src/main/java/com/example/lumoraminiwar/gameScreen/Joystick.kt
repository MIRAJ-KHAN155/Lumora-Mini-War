package com.example.lumoraminiwar.gameScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    onMove: (Offset) -> Unit
) {
    val baseRadius = 60.dp
    val thumbRadius = 25.dp
    val baseRadiusPx = with(LocalDensity.current) { baseRadius.toPx() }
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(baseRadius * 2)
            .background(Color.White.copy(alpha = 0.2f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onMove(Offset.Zero)
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onMove(Offset.Zero)
                    }
                ) { change, _ ->
                    val deltaX = change.position.x - baseRadiusPx
                    val deltaY = change.position.y - baseRadiusPx
                    val distance = hypot(deltaX, deltaY)
                    val angle = atan2(deltaY, deltaX)
                    val maxDistance = baseRadiusPx - 25f
                    val finalDistance = if (distance > maxDistance) maxDistance else distance
                    thumbOffset = Offset(
                        x = (cos(angle) * finalDistance).toFloat(),
                        y = (sin(angle) * finalDistance).toFloat()
                    )
                    onMove(Offset(thumbOffset.x / maxDistance, thumbOffset.y / maxDistance))
                }
            }
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset((thumbOffset.x + baseRadiusPx - 25f).roundToInt(), (thumbOffset.y + baseRadiusPx - 25f).roundToInt()) }
                .size(thumbRadius * 2)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
        )
    }

}