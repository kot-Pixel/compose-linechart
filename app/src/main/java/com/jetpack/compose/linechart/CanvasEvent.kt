package com.jetpack.compose.linechart

/**
 * Canvas Event
 */
sealed class CanvasEvent

object AbleWindowEvent : CanvasEvent()

object TimeAddEvent : CanvasEvent()