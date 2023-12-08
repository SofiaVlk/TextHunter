package com.sofiyavolkovaprojects.texthunter.ui.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sofiyavolkovaprojects.texthunter.R.drawable
import com.sofiyavolkovaprojects.texthunter.R.string
import com.sofiyavolkovaprojects.texthunter.ui.common.components.InfoMessage
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocScreen
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryScreen
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterScreen
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavArg.TextNavArg
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavArg.UidNavArg
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.EditText
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.Error
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.Gallery
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.Hunter
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.Storage
import com.sofiyavolkovaprojects.texthunter.ui.savedDocs.SavedDocsScreen

@Composable
fun NavHostContainer(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    textToSpeech: TextToSpeech
) {

    NavHost(
        navController = navController,
        startDestination = Storage.route,
        modifier = modifier,
        builder = {

            //Navega a la ruta establecida, si hay un error, muestra un pantalla genérica de error.
            val navigator: (String) -> Unit = {
                try {
                    navController.navigate(it)
                } catch (e: Exception) {
                    navController.navigate(Error.route)
                }
            }

            //Se configura el composable que tiene que cargarse según la ruta
            composable(route = Storage.route) {
                SavedDocsScreen(modifier = modifier, navigateTo = navigator)
            }

            composable(route = Gallery.route) {
                GalleryScreen(
                    navigateTo = navigator
                )
            }

            composable(route = Hunter.route) {
                HunterScreen(modifier = modifier, navigateTo = navigator)
            }

            composable(route = Error.route) {
                InfoMessage(
                    imagePainter = painterResource(drawable.error_message_01),
                    title =  Error.route.capitalizeConstant(),
                    bodyText = stringResource(string.th_error_body_message)
                )
            }

            composable(route = EditText.route, arguments = EditText.args) {
                val text = it.arguments?.getString(TextNavArg.key, "") ?: ""
                val id = it.arguments?.getInt(UidNavArg.key, -1) ?: -1
                EditDocScreen(
                    text = text,
                    id = id,
                    navigateTo = navigator,
                    textToSpeech = textToSpeech
                )
            }
        }
    )
}