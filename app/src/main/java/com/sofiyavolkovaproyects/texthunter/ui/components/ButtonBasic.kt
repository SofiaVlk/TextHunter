package com.sofiyavolkovaproyects.texthunter.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(showBackground = true)
fun ButtonBasicSample() {
    ButtonBasic(
        icon = Icons.AutoMirrored.Filled.MenuBook,
        text = "Title"
    )
}
@Composable
internal fun ButtonBasic(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        onClick = onClick
    )  {
        Row {
            icon?.let {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = icon,
                    contentDescription = null
                )
            }
            Text( modifier = Modifier.padding(horizontal = 10.dp),
                text = text)
        }
    }
}