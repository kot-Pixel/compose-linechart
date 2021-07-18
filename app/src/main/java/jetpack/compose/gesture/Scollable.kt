package jetpack.compose.gesture

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableBox() {
    val state = rememberScrollState() //使用rememberScrollState来记录ScrollState的状态。
    LaunchedEffect(Unit) { state.animateScrollTo(200) }
    //使用horizonScroll或者VerticalScroll即可完成Column或者Row的滚动。
    Column(
        modifier = Modifier
            .size(100.dp)
            .background(Color.Gray)
            .verticalScroll(state)
    ) {
        repeat(20) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}

//使用scrollable修饰符
@Composable
fun Scrollable() {
    var offset = remember {
        mutableStateOf(0F)
    }
    Box(
        modifier = Modifier
            .size(150.dp)
            .scrollable(
                orientation = Orientation.Horizontal,
                state = rememberScrollableState { delta ->
                    offset.value += delta
                    delta
                }
            ), contentAlignment = Alignment.Center
    ) {
        Text(text = "${offset.value}")
    }
}

//Scroll嵌套.scroll手势应该先由子布局来进行消费。如果消费不掉的话，那么一开始由子布局消费的手势动作会传播到父布局进行消费
//消费的过程。 先给子类进行消费 -> 消费掉不掉的多余的手势传播给 父布局进行消费。
@Composable
fun Scroll() {
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Column {
            repeat(6) {
                Box(
                    modifier = Modifier
                        .height(128.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Scroll here",
                        modifier = Modifier
                            .border(12.dp, Color.DarkGray)
                            .padding(24.dp)
                            .height(150.dp)
                    )
                }
            }
        }
    }
}