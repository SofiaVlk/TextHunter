package com.sofiyavolkovaproyects.texthunter.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Gallery
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Hunter
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Main


@Composable
fun BottomNavigationBar(onNavIconClick: (String) -> Unit) {

    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        Main,
        Gallery,
        Hunter
    )

    NavigationBar{
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {  Icon(painter = painterResource(id = item.navData.icon), contentDescription = item.navData.title) },
                label = { Text(text = item.navData.title) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onNavIconClick(item.createNavRoute(item.navData.icon,item.navData.title))
                }

            )
        }

    }
}
