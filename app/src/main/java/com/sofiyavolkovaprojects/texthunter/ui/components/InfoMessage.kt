package com.sofiyavolkovaprojects.texthunter.ui.components

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
import com.sofiyavolkovaprojects.texthunter.R
import com.sofiyavolkovaprojects.texthunter.ui.theme.PurpleCactus

@Composable
@Preview(showBackground = true)
private fun MessageEmptyStateSample() {
    InfoMessage(title = "No file found", bodyText = LoremIpsum(80).values.first())
}

@Composable
internal fun InfoMessage(
    modifier: Modifier = Modifier,
    imagePainter: Painter = painterResource(R.mipmap.ic_empty_state),
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
            painter = imagePainter,
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = title,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            modifier = Modifier
                .padding(20.dp),
            text = bodyText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.background,
            textAlign = TextAlign.Center

        )
    }
}