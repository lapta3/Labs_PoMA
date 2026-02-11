package com.yourname.commoninfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yourname.commoninfo.presentation.CalculatorScreen
import com.yourname.commoninfo.ui.theme.CommonInfoCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CommonInfoCalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}
