package com.jetpack.compose.linechart.charts

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private var _chartWidth: MutableStateFlow<Float> = MutableStateFlow(2300F)
    val chartWidth: StateFlow<Float> get() = _chartWidth

    private var _chartHeight: MutableStateFlow<Float> = MutableStateFlow(700F)
    val chartHeight: StateFlow<Float> get() = _chartHeight

    fun updateNewWidth(newOffset: Float) {
        _chartWidth.value = _chartWidth.value - newOffset
    }
}