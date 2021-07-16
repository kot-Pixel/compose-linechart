package com.jetpack.compose.linechart.charts

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.jetpack.compose.linechart.R
import kotlin.math.roundToInt

val borderColor = Color(0xFF818181)

@Composable
fun ChartWithAxis(
    list1: List<Float> = listOf(80F, 90F, 100F, 120F, 85F, 90F, 98F, 120F, 100F, 130F, 135F, 95F),
    list2: List<Float> = listOf(125F, 100F, 110F, 90F, 85F, 120F, 87F, 110F, 98F, 109F, 88F, 110F),
    list3: List<Float> = listOf(80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F, 80F),
    list4: List<Float> = listOf(98F, 110F, 108F, 99F, 109F, 110F, 132F, 95F, 120F, 86F, 98F, 120F)
) {
    val xOffset = LocalContext.current.resources.getDimension(R.dimen.line_chart_x_axis_offset)
    val yOffset = LocalContext.current.resources.getDimension(R.dimen.line_chart_y_axis_offset)
    val chartWidth = LocalContext.current.resources.getDimension(R.dimen.line_chart_width)
    val chartHeight = LocalContext.current.resources.getDimension(R.dimen.line_chart_height)
    val interval = LocalContext.current.resources.getDimension(R.dimen.line_chart_dot_line_margin)
    val xAxisPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = LocalContext.current.resources.getDimension(R.dimen.line_chart_x_text_size)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val yAxisPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = LocalContext.current.resources.getDimension(R.dimen.line_chart_x_text_size)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = android.graphics.Paint.Align.RIGHT
    }
    val pointRadius = dimensionResource(id = R.dimen.line_chart_data_point_radius).value
    val pointWidth = dimensionResource(id = R.dimen.line_chart_data_point_width).value
    Canvas(
        modifier = Modifier.size(
            width = dimensionResource(id = R.dimen.line_chart_part_width),
            height = dimensionResource(id = R.dimen.line_chart_part_height)
        )
    ) {

        drawLine(
            color = borderColor,
            start = Offset(xOffset, yOffset),
            end = Offset(xOffset + chartWidth, yOffset),
            strokeWidth = 2.0F
        )

        drawLine(
            color = borderColor,
            start = Offset(xOffset, yOffset),
            end = Offset(xOffset, yOffset + chartHeight),
            strokeWidth = 2.0F
        )

        drawLine(
            color = borderColor,
            start = Offset(xOffset, yOffset + chartHeight),
            end = Offset(xOffset + chartWidth, yOffset + chartHeight),
            strokeWidth = 2.0F
        )

        drawLine(
            color = borderColor,
            start = Offset(xOffset + chartWidth, yOffset),
            end = Offset(xOffset + chartWidth, yOffset + chartHeight),
            strokeWidth = 2.0F
        )

        for (i in 1..5) {
            drawLine(
                color = borderColor,
                start = Offset(xOffset, yOffset + (chartHeight / 6) * i),
                end = Offset(xOffset + chartWidth, yOffset + (chartHeight / 6) * i),
                strokeWidth = 1.0F,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(interval, interval), 0f),
            )
        }


        /**
         * 绘制底部xAxis的坐标
         */
        for (i in 0..5) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "${(6 - i) * 30} mmHg",
                    xOffset / 10 * 9,
                    yOffset + (chartHeight / 6) * i - (yAxisPaint.fontMetrics.top / 2) - (yAxisPaint.fontMetrics.bottom / 2),
                    yAxisPaint
                )
            }
        }

        /**
         *  绘制底部的刻度。
         */
        for (i in 1..11) {
            drawLine(
                color = borderColor,
                start = Offset(xOffset + chartWidth / 11 * i, (yOffset + chartHeight) - 15),
                end = Offset(xOffset + chartWidth / 11 * i, yOffset + chartHeight),
                strokeWidth = 1.0F,
            )
        }

        /**
         * 绘制底部xAxis的坐标
         */
        for (i in 0..11) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "01/0${i + 1}  10:50",
                    xOffset + chartWidth / 11 * i,
                    (yOffset + chartHeight) + 50,
                    xAxisPaint
                )
            }
        }

        mutableListOf<Pair<Float, Float>>().also {
            list1.forEachIndexed { index, value ->
                it.add(
                    xOffset + chartWidth / 11 * index to calculateYPosition(
                        value,
                        yOffset,
                        (chartHeight / 6)
                    )
                )
            }
        }.let {
            it.forEachIndexed { index, pair ->
                if (index != it.lastIndex) {
                    val nextElement = it.elementAt(index + 1)
                    drawLine(
                        color = Color.Red,
                        start = Offset(pair.first, pair.second),
                        end = Offset(nextElement.first, nextElement.second),
                        strokeWidth = 1.0F
                    )
                }
                drawCircle(
                    color = Color.Red,
                    radius = pointRadius,
                    center = Offset(
                        pair.first,
                        pair.second
                    ),
                    style = Stroke(width = pointWidth)
                )
            }
        }

        mutableListOf<Pair<Float, Float>>().also {
            list2.forEachIndexed { index, value ->
                it.add(
                    xOffset + chartWidth / 11 * index to calculateYPosition(
                        value,
                        yOffset,
                        (chartHeight / 6)
                    )
                )
            }
        }.let {
            it.forEachIndexed { index, pair ->
                if (index != it.lastIndex) {
                    val nextElement = it.elementAt(index + 1)
                    drawLine(
                        color = Color.Blue,
                        start = Offset(pair.first, pair.second),
                        end = Offset(nextElement.first, nextElement.second),
                        strokeWidth = 1.0F
                    )
                }
                drawCircle(
                    color = Color.Blue,
                    radius = pointRadius,
                    center = Offset(
                        pair.first,
                        pair.second
                    ),
                    style = Stroke(width = pointWidth)
                )
            }
        }


        mutableListOf<Pair<Float, Float>>().also {
            list3.forEachIndexed { index, value ->
                it.add(
                    xOffset + chartWidth / 11 * index to calculateYPosition(
                        value,
                        yOffset,
                        (chartHeight / 6)
                    )
                )
            }
        }.let {
            it.forEachIndexed { index, pair ->
                if (index != it.lastIndex) {
                    val nextElement = it.elementAt(index + 1)
                    drawLine(
                        color = Color.Green,
                        start = Offset(pair.first, pair.second),
                        end = Offset(nextElement.first, nextElement.second),
                        strokeWidth = 1.0F
                    )
                }
                drawCircle(
                    color = Color.Green,
                    radius = pointRadius,
                    center = Offset(
                        pair.first,
                        pair.second
                    ),
                    style = Stroke(width = pointWidth)
                )
            }
        }

        mutableListOf<Pair<Float, Float>>().also {
            list4.forEachIndexed { index, value ->
                it.add(
                    xOffset + chartWidth / 11 * index to calculateYPosition(
                        value,
                        yOffset,
                        (chartHeight / 6)
                    )
                )
            }
        }.let {
            it.forEachIndexed { index, pair ->
                if (index != it.lastIndex) {
                    val nextElement = it.elementAt(index + 1)
                    drawLine(
                        color = Color.Black,
                        start = Offset(pair.first, pair.second),
                        end = Offset(nextElement.first, nextElement.second),
                        strokeWidth = 1.0F
                    )
                }
                drawCircle(
                    color = Color.Black,
                    radius = pointRadius,
                    center = Offset(
                        pair.first,
                        pair.second
                    ),
                    style = Stroke(width = pointWidth)
                )
            }
        }
    }
}

fun calculateYPosition(
    targetFloat: Float,
    yOffset: Float,
    interval: Float
): Float {
    return yOffset + ((180 - targetFloat).roundToInt() / 30) * interval + ((180 - targetFloat) % 30) / 30F * interval
}
