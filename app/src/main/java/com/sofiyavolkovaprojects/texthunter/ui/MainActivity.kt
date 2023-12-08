/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sofiyavolkovaprojects.texthunter.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sofiyavolkovaprojects.texthunter.R.string
import com.sofiyavolkovaprojects.texthunter.ui.common.components.BottomNavigationBar
import com.sofiyavolkovaprojects.texthunter.ui.common.components.FloatingButtonCam
import com.sofiyavolkovaprojects.texthunter.ui.common.components.MainAppBar
import com.sofiyavolkovaprojects.texthunter.ui.common.theme.TextHunterApp
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavHostContainer
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.EditText
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.Hunter
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.Storage
import com.sofiyavolkovaprojects.texthunter.ui.navigation.navigatePopUpToStartDestination
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(
            this
        ) { status ->
            // set our locale only if init was success.
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale(Locale.getDefault().language)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Establece un contenedor
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: ""

            //Establece las configuraciones globales de la vista
            TextHunterApp {
                //Composable en el que se configuran diferentes elementos de la vista
                Scaffold(
                    topBar = {
                        MainAppBar(currentRoute = currentRoute, mainRoute = Storage.route) {
                            //Si hacemos click en la flecha de retroceso navegamos hacia atrás
                            navController.popBackStack()
                        }
                    },
                    bottomBar = {
                        //Establece una barra de navegación inferior en todas las pantallas menos
                        //en la de Editar documento
                        if (currentRoute != EditText.route) {
                            //Barra inferior de navegación con las pantallas principales
                            BottomNavigationBar(currentRoute) {
                                //se navega a la ruta seleccionada
                                navController.navigatePopUpToStartDestination(it)
                            }
                        }
                    },
                    //Configura FAB
                    floatingActionButton = {
                        //Solo se  muestra el FAB en la pantalla de Storage
                        if (currentRoute == Storage.route) {
                            FloatingButtonCam(
                                text = getString(string.th_fab_text),
                                expanded = true,
                                onClick = {
                                    //se navega siempre a la pantalla Hunter
                                    navController.navigatePopUpToStartDestination(Hunter.route)
                                }
                            )
                        }
                    }
                ) { padding ->
                    //Establece la navegación y pinta los contenidos
                    NavHostContainer(Modifier.padding(padding), navController, textToSpeechEngine)
                }
            }
        }
    }

    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }

}