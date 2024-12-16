package com.example.hse_homework_1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hse_homework_1.wrappers.DecimalFieldState
import com.example.hse_homework_1.wrappers.ScreenState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.commons.math3.distribution.LogNormalDistribution

class MainViewModel : ViewModel() {

    private val _screen: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Default)
    val screen: StateFlow<ScreenState> = _screen

    fun updateMedian(rawInput: String) {
        val prevState = screen.value

        if (rawInput.isEmpty()) {
            _screen.value = ScreenState.ErrorInput(
                median = DecimalFieldState.ErrorType("Заполните поле \"mu\""),
                variance = prevState.variance,
                result = "Некорректный ввод"
            )
            return
        }

        rawInput.toDoubleOrNull()?.let {

            prevState.median.let { median ->
                if (median is DecimalFieldState.DecimalValue && median.value == it) {
                    return
                }
            }

            _screen.value = ScreenState.OnInput(
                median = DecimalFieldState.DecimalValue(it),
                variance = prevState.variance,
                result = "Ожидается конец ввода"
            )
        } ?: {
            _screen.value = ScreenState.ErrorInput(
                median = DecimalFieldState.ErrorType(message = "Введённое значение не является числом"),
                variance = prevState.variance,
                result = "Некорректный ввод"
            )
        }
    }

    fun updateVariance(rawInput: String) {
        val prevState = screen.value

        if (rawInput.isEmpty()) {
            _screen.value = ScreenState.ErrorInput(
                median = prevState.median,
                variance = DecimalFieldState.ErrorType("Заполните поле \"sigma\""),
                result = "Некорректный ввод"
            )
            return
        }

        rawInput.toDoubleOrNull()?.let {

            prevState.variance.let { variance ->
                if (variance is DecimalFieldState.DecimalValue && variance.value == it) {
                    return
                }
            }

            _screen.value = ScreenState.OnInput(
                median = prevState.median,
                variance = DecimalFieldState.DecimalValue(it),
                result = "Ожидается конец ввода"
            )
        } ?: {
            _screen.value = ScreenState.ErrorInput(
                median = prevState.median,
                variance = DecimalFieldState.ErrorType(message = "Введённое значение не является числом"),
                result = "Некорректный ввод"
            )
        }
    }

    fun compute() {
        screen.value.let { params ->
            if (params.median is DecimalFieldState.DecimalValue && params.median is DecimalFieldState.DecimalValue) {
                val med = (params.median as DecimalFieldState.DecimalValue).value
                val vari = (params.variance as DecimalFieldState.DecimalValue).value

                val result = viewModelScope.async {
                    logRandom(med, vari)
                }

                viewModelScope.launch {
                    _screen.value = ScreenState.ComputedResult(
                        median = params.median,
                        variance = params.variance,
                        result = result.await().toString()
                    )
                }
            } else {
                _screen.value = ScreenState.ComputedResult(
                    median = params.median,
                    variance = params.variance,
                    result = "Некорректный ввод"
                )
            }
        }
    }

    private suspend fun logRandom(sigma: Double, mu: Double): Double =
        LogNormalDistribution(sigma, mu).sample()
}