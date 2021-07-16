package com.jetpack.compose.linechart

/**
 * Canvas Event
 */
sealed class CanvasEvent

object AbleWindowEvent : CanvasEvent()

object DisableWindowEvent : CanvasEvent()

object TimeAddEvent : CanvasEvent()

object ResetTimerEvent: CanvasEvent()