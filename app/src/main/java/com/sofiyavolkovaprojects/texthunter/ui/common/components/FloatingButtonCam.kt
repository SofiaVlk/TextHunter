package com.sofiyavolkovaprojects.texthunter.ui.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(showBackground = true)
fun FloatingButtonCamSample() {
    FloatingButtonCam(
        text = "Title",
        expanded = true
    )
}
@Composable
internal fun FloatingButtonCam(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.AddAPhoto,
    expanded: Boolean = false,
    onClick: () -> Unit = {}
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        text = {
            Text(
                modifier = modifier
                    .padding(6.dp),
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Medium,
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        expanded = expanded,
        shape = RoundedCornerShape(50),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.background
    )
}


