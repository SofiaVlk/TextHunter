package com.sofiyavolkovaproyects.texthunter.ui.savedDocs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sofiyavolkovaproyects.texthunter.R
import com.sofiyavolkovaproyects.texthunter.data.local.database.DocumentItem
import com.sofiyavolkovaproyects.texthunter.ui.components.CustomCircularProgressBar
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.EditText
import com.sofiyavolkovaproyects.texthunter.ui.theme.THTheme

@Composable
fun SavedDocsScreen(
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit = {},
    viewModel: SavedDocsViewModel = hiltViewModel()
) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    when (items) {
        is SavedDocsUiState.Success -> SavedDocsScreen(
            docList = (items as SavedDocsUiState.Success).data,
            navigateTo = navigateTo
        )
        is SavedDocsUiState.Loading -> CustomCircularProgressBar()
        else -> Unit
    }
}

@Composable
internal fun SavedDocsScreen(
    docList: List<DocumentItem>,
    navigateTo: (String) -> Unit = {},
) {
    LazyColumn {

        items(docList) { document ->
            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .defaultMinSize(minHeight = 276.dp)
                    .padding(12.dp)
                    .clickable {
                        navigateTo(EditText.createNavTextRoute(document.body))
                    }

                ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(60.dp),
                    painter = painterResource(id = R.drawable.card_background_01),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )

                Text(
                    text = document.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.inverseSurface
                )

                Text(
                    text = document.body,
                    modifier = Modifier
                        .padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    maxLines = 6,
                    overflow = Ellipsis,
                )
            }
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    THTheme {
        SavedDocsScreen(
            listOf(DocumentItem("Documento 1", LoremIpsum(6).values.first())))
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    THTheme {
        SavedDocsScreen(
            listOf(DocumentItem("Documento 1", LoremIpsum(50).values.first())))
    }
}
