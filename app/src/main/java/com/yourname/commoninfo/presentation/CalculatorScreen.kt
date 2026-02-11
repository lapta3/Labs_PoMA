package com.yourname.commoninfo.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest


@Composable
fun CalculatorScreen(
    vm: CalculatorViewModel = viewModel()
) {
    val display by vm.display.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        vm.events.collectLatest { event ->
            when (event) {
                is CalculatorViewModel.UiEvent.Copy -> copyToClipboard(context, event.text)
                is CalculatorViewModel.UiEvent.Share -> shareText(context, event.text)

            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Display
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.large
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = display,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionButton("Copy", Modifier.weight(1f)) { vm.onCopy() }
            ActionButton("Share", Modifier.weight(1f)) { vm.onShare() }
        }

        // Keypad (adaptive grid via weights)
        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton("AC", Modifier.weight(1f)) { vm.onAction(CalcAction.Clear) }
                CalcButton("⌫", Modifier.weight(1f)) { vm.onAction(CalcAction.Backspace) }
                CalcButton("+/-", Modifier.weight(1f)) { vm.onAction(CalcAction.ToggleSign) }
                CalcButton("÷", Modifier.weight(1f)) { vm.onAction(CalcAction.Div) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DigitButton('7', Modifier.weight(1f), vm)
                DigitButton('8', Modifier.weight(1f), vm)
                DigitButton('9', Modifier.weight(1f), vm)
                CalcButton("×", Modifier.weight(1f)) { vm.onAction(CalcAction.Mul) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DigitButton('4', Modifier.weight(1f), vm)
                DigitButton('5', Modifier.weight(1f), vm)
                DigitButton('6', Modifier.weight(1f), vm)
                CalcButton("-", Modifier.weight(1f)) { vm.onAction(CalcAction.Sub) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DigitButton('1', Modifier.weight(1f), vm)
                DigitButton('2', Modifier.weight(1f), vm)
                DigitButton('3', Modifier.weight(1f), vm)
                CalcButton("+", Modifier.weight(1f)) { vm.onAction(CalcAction.Add) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton("0", Modifier.weight(2f)) { vm.onAction(CalcAction.Digit('0')) }
                CalcButton(".", Modifier.weight(1f)) { vm.onAction(CalcAction.Dot) }
                CalcButton("=", Modifier.weight(1f)) { vm.onAction(CalcAction.Equals) }
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun DigitButton(d: Char, modifier: Modifier, vm: CalculatorViewModel) {
    CalcButton(text = d.toString(), modifier = modifier) {
        vm.onAction(CalcAction.Digit(d))
    }
}

@Composable
private fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.large
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge)
    }
}
private fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("Result", text))
}

private fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share result"))
}
