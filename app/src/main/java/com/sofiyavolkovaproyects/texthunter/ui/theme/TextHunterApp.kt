package com.sofiyavolkovaproyects.texthunter.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable


@Composable
fun TextHunterApp(content: @Composable () -> Unit) {
    THTheme {
        Surface(color = MaterialTheme.colorScheme.background){
            content()
        }
    }
}