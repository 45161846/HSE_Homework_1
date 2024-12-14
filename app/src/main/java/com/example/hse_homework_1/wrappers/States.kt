package com.example.hse_homework_1.wrappers

sealed class DecimalFieldState {

    data object Default : DecimalFieldState()

    data class DecimalValue(
        val value: Double
    ) : DecimalFieldState() {
        override fun toString(): String {
            return value.toString()
        }
    }

    data class ErrorType(
        val message: String
    ) : DecimalFieldState()
}

sealed class ScreenState(
    open val median: DecimalFieldState,
    open val variance: DecimalFieldState,
    open val result: String = String()
) {

    data object Default : ScreenState(
        median = DecimalFieldState.Default,
        variance = DecimalFieldState.Default,
        result = String()
    )

    data class OnInput(
        override val median: DecimalFieldState,
        override val variance: DecimalFieldState,
        override val result: String = String()
    ) : ScreenState(
        median, variance, result
    )

    data class ComputedResult(
        override val median: DecimalFieldState,
        override val variance: DecimalFieldState,
        override val result: String
    ) : ScreenState(
        median, variance, result
    )

    data class ErrorInput(
        override val median: DecimalFieldState,
        override val variance: DecimalFieldState,
        override val result: String
    ) : ScreenState(
        median, variance, result
    )
}