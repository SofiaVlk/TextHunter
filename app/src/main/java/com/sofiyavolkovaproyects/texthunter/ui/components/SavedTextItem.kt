package com.sofiyavolkovaproyects.texthunter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Diseño del item para mostrar el texto en la panralla de Storage
@Composable
@Preview(showBackground = true)
private fun SavedTextItemSample() {
    SavedTextItem("Text 1", LoremIpsum(50).values.first())
}
@Composable
internal fun SavedTextItem(
    title: String,
    bodyText: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .bottomBorder(1.dp, Color.LightGray),
            contentAlignment = Alignment.Center,
        ) {

            Text(
                modifier = Modifier
                    .padding(6.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Normal
            )
        }

        Text(
            modifier = Modifier
                .padding(6.dp),
            text = bodyText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            maxLines = 3
        )
    }
}