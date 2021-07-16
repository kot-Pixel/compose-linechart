package com.jetpack.compose.linechart

const val WindowHeight = 120F

const val WindowWidth = 500F

const val TextFontSize = 30F

/**
 * 绘制点的默认偏移量。
 */
/*const val WindowXOffset = 50

const val WindowYOffset = 160*/

data class WindowPosition(
    var xPosition: Float,
    var yPosition: Float
)

sealed class DrawVerticalType

object DrawOnTop : DrawVerticalType()

object DrawOnBottom : DrawVerticalType()

sealed class DrawHorizontalType

object DrawOnLeft : DrawHorizontalType()

object DrawOnRight : DrawHorizontalType()

data class DrawType(
    val vertical: DrawVerticalType = DrawOnTop,
    val horizontal: DrawHorizontalType = DrawOnRight
)

/**
 * 通过点击point的x、y坐标和可用显示最大高度和宽度来确定显示方式
 */
fun confirmDrawTypeByPosition(
    clickXAxisValue: Float,
    clickYAxisValue: Float,
    availableWidth: Float
): DrawType {
    val verticalType = if (clickYAxisValue - WindowHeight < 0) DrawOnBottom else DrawOnTop
    val horizontalType =
        if (clickXAxisValue + WindowWidth > availableWidth) DrawOnLeft else DrawOnRight
    return DrawType(
        vertical = verticalType,
        horizontal = horizontalType
    )
}