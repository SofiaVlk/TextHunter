package com.sofiyavolkovaproyects.texthunter.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavData.BottomItemNavData
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Gallery
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Hunter
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Storage


@Composable
fun BottomNavigationBar(currentRoute: String, onNavIconClick: (String) -> Unit) {

    var itemRoute: String by remember { mutableStateOf("") }
    val items = listOf(
        Storage,
        Gallery,
        Hunter
    )

    itemRoute = if (items.any { it.route == currentRoute }) currentRoute else itemRoute

    NavigationBar {
        items.forEach { item ->
            val data = item.navData as BottomItemNavData
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = data.icon),
                        contentDescription = data.title
                    )
                },
                label = { Text(text = data.title) },
                selected = itemRoute == item.route,
                onClick = { onNavIconClick(item.route) }
            )
        }
    }
}
