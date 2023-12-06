package com.sofiyavolkovaproyects.texthunter.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable


@Composable
fun TextHunterApp(content: @Composable () -> Unit) {
    //Establece el tema de la aplicación
    THTheme {
        //Establece la superficie de fondo
        Surface(color = MaterialTheme.colorScheme.background){
            content()
        }
    }
}