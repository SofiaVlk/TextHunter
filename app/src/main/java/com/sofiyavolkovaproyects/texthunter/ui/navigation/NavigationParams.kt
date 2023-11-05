package com.sofiyavolkovaproyects.texthunter.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sofiyavolkovaproyects.texthunter.R
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavArg.TextNavArg
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavData.BottomItemNavData
import java.util.*

const val GALLERY_ROUTE = "gallery"
const val HUNTER_ROUTE = "hunter"
const val STORAGE_ROUTE = "storage"
const val EDIT_TEXT_ROUTE = "editText"

sealed class NavigationParams(
    var baseRoute: String,
    private val navArgs: List<NavArg> = emptyList(),
    val navData: NavData? = null
) {

    val route = kotlin.run {
        val argKeys = navArgs.map { "{${it.key}}" }
        listOf(baseRoute).plus(argKeys).joinToString("/")
    }

    val args = navArgs.map {
        navArgument(it.key) { type = it.navType }
    }

    data object Storage : NavigationParams(
        baseRoute = STORAGE_ROUTE,
        navData = BottomItemNavData(R.drawable.ic_text_snippet_24, STORAGE_ROUTE.capitalizeConstant())
    )

    data object Gallery : NavigationParams(
        baseRoute = GALLERY_ROUTE,
        navData = BottomItemNavData(R.drawable.ic_gallery_24, GALLERY_ROUTE.capitalizeConstant())
    )

    data object Hunter : NavigationParams(
        baseRoute = HUNTER_ROUTE,
        navData = BottomItemNavData(R.drawable.ic_camera_enhance_24, HUNTER_ROUTE.capitalizeConstant())
    )

    data object EditText : NavigationParams(
        baseRoute = EDIT_TEXT_ROUTE,
        navArgs = listOf(TextNavArg),
    )
    fun createNavTextRoute(text: String) = "$baseRoute/$text"

}

enum class NavArg(val key: String, val navType: NavType<*>) {
    IconNavArg(key = "icon", NavType.IntType),
    TitleNavArg(key = "title", NavType.StringType),
    TextNavArg(key = "text", NavType.StringType)
}

sealed class NavData {
    data class BottomItemNavData(val icon: Int, val title: String) : NavData()
}

fun String.capitalizeConstant() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
