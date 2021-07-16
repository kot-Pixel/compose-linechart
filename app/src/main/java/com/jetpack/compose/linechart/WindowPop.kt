package com.jetpack.compose.linechart

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize

@ExperimentalComposeUiApi
@Composable
@Preview(widthDp = 500, heightDp = 500)
fun WindowPop() {
    val drawType = remember {
        mutableStateOf(DrawType())
    }
    val size = remember {
        mutableStateOf(Size.Zero)
    }
    val topLeftPosition = remember {
        mutableStateOf(0.0F to 0.0F)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .onGloballyPositioned { layoutCoordinates ->
                size.value = layoutCoordinates.size.toSize()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        drawType.value = confirmDrawTypeByPosition(
                            it.x, it.y, availableWidth = size.value.height
                        )
                        topLeftPosition.value = when (drawType.value.vertical) {
                            DrawOnTop -> it.y - WindowHeight
                            DrawOnBottom -> it.y
                        } to when (drawType.value.horizontal) {
                            DrawOnLeft -> it.x - WindowWidth
                            DrawOnRight -> it.x
                        }
                    }
                    else -> false
                }
                true
            }
        ) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(topLeftPosition.value.second, topLeftPosition.value.first),
                cornerRadius = CornerRadius(x = 20F, y = 20F),
                size = Size(500F, 120F)
            )
        }

    }
}