package com.yourname.commoninfo.presentation

import androidx.lifecycle.ViewModel
import com.yourname.commoninfo.domain.CalculatorEngine
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class CalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine()

    private val _display = MutableStateFlow(engine.getDisplay())
    val display: StateFlow<String> = _display.asStateFlow()

    private val eventsChannel = Channel<UiEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    fun onAction(action: CalcAction) {
        when (action) {
            is CalcAction.Digit -> engine.onDigit(action.d)
            CalcAction.Dot -> engine.onDot()
            CalcAction.Clear -> engine.onClear()
            CalcAction.Backspace -> engine.onBackspace()
            CalcAction.ToggleSign -> engine.onToggleSign()
            CalcAction.Add -> engine.onOperator(CalculatorEngine.Operator.ADD)
            CalcAction.Sub -> engine.onOperator(CalculatorEngine.Operator.SUB)
            CalcAction.Mul -> engine.onOperator(CalculatorEngine.Operator.MUL)
            CalcAction.Div -> engine.onOperator(CalculatorEngine.Operator.DIV)
            CalcAction.Equals -> engine.onEquals()
        }
        _display.value = engine.getDisplay()
    }

    fun onCopy() {
        eventsChannel.trySend(UiEvent.Copy(_display.value))
    }

    fun onShare() {
        eventsChannel.trySend(UiEvent.Share(_display.value))
    }

    sealed interface UiEvent {
        data class Copy(val text: String) : UiEvent
        data class Share(val text: String) : UiEvent
    }
}

sealed interface CalcAction {
    data class Digit(val d: Char) : CalcAction
    data object Dot : CalcAction
    data object Clear : CalcAction
    data object Backspace : CalcAction
    data object ToggleSign : CalcAction
    data object Add : CalcAction
    data object Sub : CalcAction
    data object Mul : CalcAction
    data object Div : CalcAction
    data object Equals : CalcAction
}
