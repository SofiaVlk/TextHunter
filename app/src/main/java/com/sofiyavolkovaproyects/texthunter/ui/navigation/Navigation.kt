package com.sofiyavolkovaproyects.texthunter.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryScreen
import com.sofiyavolkovaproyects.texthunter.ui.hunter.HunterScreen
import com.sofiyavolkovaproyects.texthunter.ui.landingitemtype.LandingItemTypeScreen
import com.sofiyavolkovaproyects.texthunter.ui.navigation.BottomNavItem.Gallery
import com.sofiyavolkovaproyects.texthunter.ui.navigation.BottomNavItem.Hunter
import com.sofiyavolkovaproyects.texthunter.ui.navigation.BottomNavItem.Main
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.IconNavArg
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.TitleNavArg

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = Main.route,
        modifier = Modifier.padding(paddingValues = padding),

        builder = {

            composable(route = Main.route) { LandingItemTypeScreen(modifier = Modifier.padding(16.dp)) }

            composable(route = Gallery.route, arguments = Gallery.args) {

                val (icon, title) = it.getArguments()
                requireNotNull(icon)
                requireNotNull(title)
                GalleryScreen(icon = icon, description = title)
            }

            composable(route = Hunter.route, arguments = Hunter.args) {
                val (icon, title) = it.getArguments()

                requireNotNull(icon)
                requireNotNull(title)
                HunterScreen(icon, title)
            }
        })

}

internal fun NavBackStackEntry.getArguments(): Pair<Int?, String?> {
    val icon = this.arguments?.getInt(IconNavArg.key)
    val title = this.arguments?.getString(TitleNavArg.key, "")
    return Pair(icon, title)
}