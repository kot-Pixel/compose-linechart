package com.jetpack.compose.linechart.charts

import android.graphics.Typeface
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.jetpack.compose.linechart.*
import kotlin.math.roundToInt


@ExperimentalUnsignedTypes
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalComposeUiApi
@Composable
fun LineChart(
    xSegment: Int = 10,
    ySegment: Int = 6,
    fontSize: Float = 30F,
    chartWidth: Float,
    chartHeight: Float,
    data: List<List<Float>> = listOf(
        listOf(80F, 90F, 100F, 120F, 85F, 90F, 98F, 120F, 100F, 130F, 135F),
        listOf(125F, 100F, 110F, 90F, 85F, 120F, 87F, 110F, 98F, 109F, 88F),
        listOf(80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F),
        listOf(98F, 110F, 108F, 99F, 109F, 110F, 132F, 95F, 120F, 86F, 98F)
    ),
) {
    val targetList = mutableListOf<MutableList<Pair<Float, Float>>>()
    val rangeList = mutableListOf<MutableList<Pair<pointRrange, String>>>()
    data.forEach { list ->
        targetList.add(mutableListOf<Pair<Float, Float>>().also {
            list.forEachIndexed { index, value ->
                it.add(
                    chartWidth / xSegment * index to calculateYPosition2(
                        value,
                        (chartHeight / ySegment)
                    )
                )
            }
        })
        rangeList.add(mutableListOf<Pair<pointRrange, String>>().also {
            list.forEachIndexed { index, value ->
                val x = chartWidth / xSegment * index
                val y = calculateYPosition2(
                    value,
                    (chartHeight / ySegment)
                )
                it.add(
                    pointRrange(
                        x, y
                    ) to "${index}-${list[index]}"
                )
            }
        })
    }


    val drawType = remember {
        mutableStateOf(DrawType())
    }
    val size = remember {
        mutableStateOf(Size.Zero)
    }
    val topLeftPosition = remember {
        mutableStateOf(0.0F to 0.0F)
    }

    val point = remember {
        mutableStateOf(0.0F to 0.0F)
    }

    val yAxisPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = fontSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = android.graphics.Paint.Align.CENTER
    }

    val xAxisPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = fontSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = android.graphics.Paint.Align.RIGHT
    }

    val manager = CanvasManager(rememberCoroutineScope())
    manager.start()
    val state = manager.windowStateFlow.collectAsState(initial = WindowState.NotDisplay)

    val animationTargetState = remember { mutableStateOf(0f) }

    val animatedFloatState = animateFloatAsState(
        targetValue = animationTargetState.value,
        animationSpec = tween(durationMillis = 3000)
    )

    Canvas(modifier = Modifier
        .width(920.dp)
        .height(280.dp)
        .offset(80.dp, 50.dp)
        .onGloballyPositioned { layoutCoordinates ->
            size.value = layoutCoordinates.size.toSize()
        }
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    rangeList.forEach { list ->
                        list.forEach { pair ->
                            val x = pair.first.x
                            val y = pair.first.y
                            if ((it.x in x - 20..x + 20) && (it.y in y - 20..y + 20)) {
                                manager.enableDisplay()
                                animationTargetState.value = 0.3F
                                point.value = x to y
                                drawType.value = confirmDrawTypeByPosition(
                                    x, y, availableWidth = size.value.width
                                )
                                topLeftPosition.value = when (drawType.value.vertical) {
                                    DrawOnTop -> y - WindowHeight
                                    DrawOnBottom -> y - 20F
                                } to when (drawType.value.horizontal) {
                                    DrawOnLeft -> x - WindowWidth
                                    DrawOnRight -> x
                                }
                            }
                        }
                    }
                }
            }
            true
        }) {
        lineChartDrawBorder(
            scope = this,
            width = chartWidth,
            height = chartHeight,
            color = Color.Black
        )
        lineChartYAxisSolid(
            scope = this,
            ySegment = ySegment,
            width = chartWidth,
            height = chartHeight,
            color = Color.Black
        )
        drawXAxisScale(
            scope = this,
            xSegment = xSegment,
            width = chartWidth,
            height = chartHeight,
            color = Color.Black
        )

        drawAxisText(
            scope = this,
            fontSize = fontSize,
            xSegment = xSegment,
            ySegment = ySegment,
            width = chartWidth,
            height = chartHeight,
            xTextPainter = xAxisPaint,
            yTextPainter = yAxisPaint
        )

        targetList.forEachIndexed { inx, mutableList ->
            val color = when (inx) {
                0 -> Color.Red
                1 -> Color.Black
                2 -> Color.Yellow
                3 -> Color.Blue
                else -> Color.Red
            }
            mutableList.forEachIndexed { index, pair ->
                if (index != mutableList.lastIndex) {
                    val nextElement = mutableList.elementAt(index + 1)
                    drawLine(
                        color = color,
                        start = Offset(pair.first, pair.second),
                        end = Offset(nextElement.first, nextElement.second),
                        strokeWidth = 2.0F
                    )
                }
                drawCircle(
                    color = color,
                    radius = 10F,
                    center = Offset(
                        pair.first,
                        pair.second
                    ),
                    style = Stroke(width = 2F)
                )
            }
        }

        if (state.value == WindowState.Display) {
            rangeList.forEach {
                it.forEach { pair ->
                    if (pair.first.x == point.value.first && pair.first.y == point.value.second) {
                        drawPopWindowAndContent(
                            this,
                            topLeftPosition.value.second,
                            topLeftPosition.value.first,
                            "当前时间为：01/0${(pair.second.split("-").first()).toInt() + 1} 10:50",
                            "当前血压值为：${pair.second.split("-").last()}",
                            animatedFloatState.value
                        )
                    }
                }
            }
        }
    }
}

/**
 * 绘制边框
 */
fun lineChartDrawBorder(
    scope: DrawScope,
    width: Float,
    height: Float,
    color: Color
) {
    scope.drawRect(
        color = color,
        style = Stroke(width = 1F),
        size = Size(width = width, height = height)
    )
}

/**
 * 绘制横轴虚线
 */
fun lineChartYAxisSolid(
    scope: DrawScope,
    ySegment: Int,
    width: Float,
    height: Float,
    color: Color
) {
    for (i in 1..ySegment) {
        scope.drawLine(
            color = color,
            start = Offset(0.0F, height / ySegment * i),
            end = Offset(width, height / ySegment * i),
            strokeWidth = 1.0F,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15F, 15F), 0f),
        )
    }
}

/**
 * 绘制纵轴的刻度
 */
fun drawXAxisScale(
    scope: DrawScope,
    xSegment: Int,
    width: Float,
    height: Float,
    color: Color
) {
    for (i in 1..xSegment) {
        scope.drawLine(
            color = color,
            start = Offset(width / xSegment * i, height - 10),
            end = Offset(width / xSegment * i, height),
            strokeWidth = 1.0F
        )
    }
}

/**
 * 绘制坐标轴上的文字
 */
fun drawAxisText(
    scope: DrawScope,
    fontSize: Float,
    xSegment: Int,
    ySegment: Int,
    width: Float,
    height: Float,
    xTextPainter: android.graphics.Paint,
    yTextPainter: android.graphics.Paint
) {
    for (i in 0..xSegment) {
        scope.drawIntoCanvas {
            it.nativeCanvas.drawText(
                "01/0${i + 1}  10:50",
                (width / xSegment) * i,
                height + fontSize + 20,
                yTextPainter
            )
        }
    }

    for (i in 0..ySegment) {
        scope.drawIntoCanvas {
            it.nativeCanvas.drawText(
                "${(6 - i) * 30} mmHg",
                -50F,
                (height / ySegment) * i - (xTextPainter.fontMetrics.top / 2) - (xTextPainter.fontMetrics.bottom / 2),
                xTextPainter
            )
        }
    }
}

fun calculateYPosition2(
    targetFloat: Float,
    interval: Float
): Float {
    return ((180 - targetFloat).roundToInt() / 30) * interval + ((180 - targetFloat) % 30) / 30F * interval
}

