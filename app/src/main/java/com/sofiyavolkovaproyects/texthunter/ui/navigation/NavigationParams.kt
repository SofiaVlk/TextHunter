package com.sofiyavolkovaproyects.texthunter.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sofiyavolkovaproyects.texthunter.R
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.IconNavArg
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.TitleNavArg
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavData.GalleryNavData
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavData.HunterNavData
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavData.MainNavData
import java.util.*

const val GALLERY_ROUTE = "gallery"
const val HUNTER_ROUTE = "hunter"
const val MAIN_ROUTE = "main"


sealed class NavigationParams(
    var baseRoute: String,
    private val navArgs: List<NavArg> = emptyList(),
    val navData: NavData
) {

    val route = kotlin.run {
        val argKeys = navArgs.map { "{${it.key}}" }
        listOf(baseRoute).plus(argKeys).joinToString("/")
    }

    val args = navArgs.map {
        navArgument(it.key) { type = it.navType }
    }

    data object Main : NavigationParams(
        baseRoute = MAIN_ROUTE,
        navArgs = listOf(
            IconNavArg,
            TitleNavArg
        ),
        navData = MainNavData
    )
    data object Gallery : NavigationParams(
        baseRoute = GALLERY_ROUTE,
        navArgs = listOf(
            IconNavArg,
            TitleNavArg
        ),
        navData = GalleryNavData
    )

    data object Hunter : NavigationParams(
        baseRoute = HUNTER_ROUTE,
        navArgs = listOf(
            IconNavArg,
            TitleNavArg
        ), navData = HunterNavData
    )

    fun createNavRoute(icon: Int, title: String) = "$baseRoute/$icon/$title"

}

enum class NavArg(val key: String, val navType: NavType<*>) {
    IconNavArg("icon", NavType.IntType),
    TitleNavArg("title", NavType.StringType)
}

sealed class NavData(val icon: Int, val title: String) {
    data object GalleryNavData : NavData(R.drawable.ic_home, GALLERY_ROUTE.capitalizeConstant())
    data object HunterNavData : NavData(R.drawable.ic_search, HUNTER_ROUTE.capitalizeConstant())
    data object MainNavData : NavData(R.drawable.ic_profile, MAIN_ROUTE.capitalizeConstant())
}

fun String.capitalizeConstant() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
