package com.jetpack.compose.linechart.charts

import android.graphics.Typeface
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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


@ExperimentalComposeUiApi
@Composable
fun LineChart(
    xSegment: Int = 10,
    ySegment: Int = 6,
    fontSize: Float = 30F,
    chartWidth: Float,
    chartHeight: Float,
    data: List<List<Float>> = listOf(
        listOf(80F, 90F, 100F, 120F, 85F, 90F, 98F, 120F, 100F, 130F, 135F, 95F),
        listOf(125F, 100F, 110F, 90F, 85F, 120F, 87F, 110F, 98F, 109F, 88F, 110F),
        listOf(80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F),
        listOf(98F, 110F, 108F, 99F, 109F, 110F, 132F, 95F, 120F, 86F, 98F, 120F)
    ),
) {

    val drawType = remember {
        mutableStateOf(DrawType())
    }
    val size = remember {
        mutableStateOf(Size.Zero)
    }
    val topLeftPosition = remember {
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
                    drawType.value = confirmDrawTypeByPosition(
                        it.x, it.y, availableWidth = size.value.width
                    )
                    topLeftPosition.value = when (drawType.value.vertical) {
                        DrawOnTop -> it.y - WindowHeight
                        DrawOnBottom -> it.y
                    } to when (drawType.value.horizontal) {
                        DrawOnLeft -> it.x - WindowWidth
                        DrawOnRight -> it.x
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

        drawPopWindowAndContent(
            this,
            topLeftPosition.value.second,
            topLeftPosition.value.first,
            "x",
            "y"
        )
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
