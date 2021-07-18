package jetpack.compose.gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun DragModifier() {
    val offset = remember { mutableStateOf<Float>(0F) }
    Text(modifier = Modifier
        .draggable(
            orientation = Orientation.Horizontal,
            state = rememberDraggableState {
                offset.value += it
            }
        )
        .offset { IntOffset(offset.value.roundToInt(), 0) }, text = "doDrag!"
    )
}

@Composable
fun DragScope2() {
    val offsetX = remember { mutableStateOf(0F) }
    val offsetY = remember { mutableStateOf(0F) }
    Box(modifier = Modifier
        .size(100.dp)
        .offset {
            IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
        }
        .background(color = Color.Blue)
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consumeAllChanges()
                offsetX.value += dragAmount.x
                offsetY.value += dragAmount.y
            }
        }
    )
}

@Composable
fun TransformableState() {
    //缩放的程度
    val scale = remember {
        mutableStateOf(1F)
    }
    //旋转
    val rotate = remember {
        mutableStateOf(0F)
    }
    //平移
    val offset = remember {
        mutableStateOf(Offset.Zero)
    }
    val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
        scale.value *= zoomChange
        rotate.value += rotationChange
        offset.value += panChange
    }
    Box(
        Modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value

                rotationZ = rotate.value
                translationX = offset.value.x
                translationY = offset.value.y
            }
            .transformable(state = state)

            .size(100.dp)
            .background(color = Color.Blue)
    )
}