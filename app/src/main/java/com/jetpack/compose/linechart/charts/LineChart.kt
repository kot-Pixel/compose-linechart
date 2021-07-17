package com.jetpack.compose.linechart.charts

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.jetpack.compose.linechart.ChartHeight
import com.jetpack.compose.linechart.ChartWidth

@Composable
fun LineChart(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .offset(80.dp, 50.dp),
    xSegment: Int = 10,
    xSegmentList: List<String> = listOf(),
    ySegment: Int = 6,
    ySegmentList: List<String> = listOf(),
    fontSize: Float = 30F
) {
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

    Canvas(modifier = modifier) {
        lineChartDrawBorder(
            scope = this,
            width = ChartWidth,
            height = ChartHeight,
            color = Color.Black
        )
        lineChartYAxisSolid(
            scope = this,
            ySegment = ySegment,
            width = ChartWidth,
            height = ChartHeight,
            color = Color.Black
        )
        drawXAxisScale(
            scope = this,
            xSegment = xSegment,
            width = ChartWidth,
            height = ChartHeight,
            color = Color.Black
        )

        drawAxisText(
            scope = this,
            fontSize = fontSize,
            xSegment = xSegment,
            ySegment = ySegment,
            width = ChartWidth,
            height = ChartHeight,
            xTextPainter = xAxisPaint,
            yTextPainter = yAxisPaint
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
                "x${i}",
                (width / xSegment) * i,
                height + fontSize + 10,
                yTextPainter
            )
        }
    }

    for (i in 0..ySegment) {
        scope.drawIntoCanvas {
            it.nativeCanvas.drawText(
                "y${i}",
                -50F,
                (height / ySegment) * i - (xTextPainter.fontMetrics.top / 2) - (xTextPainter.fontMetrics.bottom / 2),
                xTextPainter
            )
        }
    }
}
