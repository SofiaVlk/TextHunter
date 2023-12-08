package com.sofiyavolkovaprojects.texthunter.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sofiyavolkovaprojects.texthunter.R
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavArg.TextNavArg
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavArg.UidNavArg
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavData.BottomItemNavData
import java.util.Locale

private const val GALLERY_ROUTE = "gallery"
private const val HUNTER_ROUTE = "hunter"
private const val STORAGE_ROUTE = "storage"
private const val EDIT_TEXT_ROUTE = "editText"
private const val ERROR_ROUTE = "error"
private const val EMPTY = "Empty"
private const val TEXT = "text"
private const val ID = "id"


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
        navArgs = listOf(TextNavArg, UidNavArg),
    )
    data object Error : NavigationParams(
        baseRoute = ERROR_ROUTE
    )

    fun createNavTextRoute(text: String = EMPTY, id: Int = -1) = "$baseRoute/$text/$id"
}

enum class NavArg(val key: String, val navType: NavType<*>) {
    TextNavArg(key = TEXT, NavType.StringType),
    UidNavArg(key = ID, NavType.IntType)
}

sealed class NavData {
    data class BottomItemNavData(val icon: Int, val title: String) : NavData()
}

fun String.capitalizeConstant() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
