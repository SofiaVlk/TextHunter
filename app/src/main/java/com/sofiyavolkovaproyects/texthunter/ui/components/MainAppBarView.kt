package com.sofiyavolkovaproyects.texthunter.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sofiyavolkovaproyects.texthunter.R
import com.sofiyavolkovaproyects.texthunter.ui.navigation.capitalizeConstant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainAppBar(
    currentRoute: String,
    mainRoute: String,
    onClick: () -> Unit = {}
) {

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.secondary
        ),
        actions = {
            Image(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape),
                painter = painterResource(R.mipmap.ic_logo_app),
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = currentRoute.substringBefore("/").capitalizeConstant(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            //No mostramos la flecha de retroceso si estamos en la primera pantalla
            if (currentRoute != mainRoute) {
                AppbarAction(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onClick
                )
            }
        },
    )
}

@Composable
private fun AppbarAction(
    imageVector: ImageVector,
    onClick: () -> Unit = {}
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }
}