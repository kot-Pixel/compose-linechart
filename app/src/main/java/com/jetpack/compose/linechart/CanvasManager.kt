package com.jetpack.compose.linechart

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.Flow

/**
 * Canvas 绘制
 */
class CanvasManager(
    private val scope: CoroutineScope
) {
    val windowStateFlow: MutableSharedFlow<WindowState> = MutableSharedFlow()

    private val eventFlow = MutableSharedFlow<CanvasEvent>()

    private var timeJob: Job? = null

    private val _mTimeCountFlow: MutableSharedFlow<CanvasEvent> = MutableSharedFlow()

    private var count = 0

    /**
     * 启动定时器。
     */
    private fun startTimer() {
        timeJob = scope.launch {
            delay(1000)
            _mTimeCountFlow.emit(TimeAddEvent)
        }
    }

    /**
     * 结束定时器
     */
    private fun stopTimer() {
        timeJob?.apply {
            if (isActive) cancel()
            timeJob = null
        }
    }

    fun start() {
        scope.launch {
            eventFlow.collect {
                when (it) {
                    TimeAddEvent -> {
                        count++
                        if (count >= 5) {
                            stopTimer()
                            eventFlow.emit(DisableWindowEvent)
                        }
                    }
                    AbleWindowEvent -> {
                        startTimer()
                        windowStateFlow.emit(WindowState.Display)
                    }
                    DisableWindowEvent -> {
                        windowStateFlow.emit(WindowState.NotDisplay)
                    }
                    ResetTimerEvent -> {
                        count = 0
                    }
                }
            }
        }
    }

    fun enableDisplay() {
        scope.launch {
            eventFlow.emit(AbleWindowEvent)
        }
    }
}