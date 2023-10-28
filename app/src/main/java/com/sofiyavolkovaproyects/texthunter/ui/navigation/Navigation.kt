package com.sofiyavolkovaproyects.texthunter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sofiyavolkovaproyects.texthunter.ui.edittext.EditTextScreen
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryScreen
import com.sofiyavolkovaproyects.texthunter.ui.hunter.HunterScreen
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.IconNavArg
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.TextNavArg
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.TitleNavArg
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.EditText
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Gallery
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Hunter
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Main
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsScreen

@Composable
fun NavHostContainer(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    NavHost(
        navController = navController,
        startDestination = Main.route,
        modifier = modifier,
        builder = {

            composable(route = Main.route) { SavedDocsScreen(modifier = modifier) }

            composable(route = Gallery.route) { GalleryScreen(modifier) }

            composable(route = Hunter.route) { HunterScreen(modifier, navController = navController) }

            composable(route = EditText.route, arguments = EditText.args) {
                val text = it.arguments?.getString(TextNavArg.key, "") ?: ""
                EditTextScreen(modifier,text = text)
            }
        }
    )
}

internal fun NavBackStackEntry.getArguments(): Pair<Int?, String?> {
    val icon = this.arguments?.getInt(IconNavArg.key)
    val title = this.arguments?.getString(TitleNavArg.key, "")
    val text = this.arguments?.getString(TextNavArg.key, "")
    return Pair(icon, title)
}