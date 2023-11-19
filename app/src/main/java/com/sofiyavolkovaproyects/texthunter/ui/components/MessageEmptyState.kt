package com.sofiyavolkovaproyects.texthunter.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.sofiyavolkovaproyects.texthunter.R
import com.sofiyavolkovaproyects.texthunter.ui.theme.PurpleCactus

@Composable
@Preview(showBackground = true)
private fun MessageEmptyStateSample() {
    MessageEmptyState(title = "No file found", bodyText = LoremIpsum(80).values.first())
}

@Composable
internal fun MessageEmptyState(
    modifier: Modifier = Modifier,
    emptyPainter: Painter = painterResource(R.mipmap.ic_empty_state),
    title: String,
    bodyText: String
) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(PurpleCactus),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            modifier = Modifier.size(200.dp),
            painter = emptyPainter,
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            modifier = Modifier
                .padding(20.dp),
            text = bodyText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            textAlign = TextAlign.Center

        )
    }
}