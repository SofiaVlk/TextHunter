package com.sofiyavolkovaproyects.texthunter.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sofiyavolkovaproyects.texthunter.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainAppBar(
    navigationAvailable: Boolean = false,
    onclick: (Boolean) -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            if (navigationAvailable) {
                AppbarAction(
                    imageVector = Icons.Default.ArrowBack,
                    onclclick = { onclick }
                )
            }
        }
    )
}

@Composable
private fun AppbarAction(
    imageVector: ImageVector,
    onclclick: () -> Unit
) {
    IconButton(onClick = onclclick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }
}