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

package com.sofiyavolkovaproyects.texthunter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sofiyavolkovaproyects.texthunter.ui.components.BottomNavigationBar
import com.sofiyavolkovaproyects.texthunter.ui.components.MainAppBar
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavHostContainer
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Storage
import com.sofiyavolkovaproyects.texthunter.ui.theme.TextHunterApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: ""

            TextHunterApp {
                Scaffold(
                    topBar = {
                        MainAppBar(currentRoute = currentRoute, mainRoute = Storage.route) {
                            navController.popBackStack()
                        }
                    },
                    bottomBar = {
                        BottomNavigationBar(currentRoute) { navController.navigate(it) }
                    }
                ) { padding ->
                    NavHostContainer(Modifier.padding(padding), navController)
                }
            }
        }
    }
}