package com.sofiyavolkovaproyects.texthunter.ui.navigation

import androidx.navigation.NavHostController

fun NavHostController.navigatePopUpToStartDestination(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = false }
        launchSingleTop = true
        restoreState = false
    }
}