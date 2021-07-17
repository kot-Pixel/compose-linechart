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

    private var count = 0

    /**
     * 启动定时器。
     */
    private fun startTimer() {
        timeJob = scope.launch {
            while (true) {
                delay(1000)
                eventFlow.emit(TimeAddEvent)
            }
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
                println("canvas Event $it")
                when (it) {
                    TimeAddEvent -> {
                        count++
                        if (count >= 5) {
                            windowStateFlow.emit(WindowState.NotDisplay)
                            stopTimer()
                        }
                    }
                    AbleWindowEvent -> {
                        stopTimer()
                        count = 0
                        startTimer()
                        windowStateFlow.emit(WindowState.Display)
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