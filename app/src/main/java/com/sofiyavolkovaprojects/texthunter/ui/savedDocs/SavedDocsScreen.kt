package com.sofiyavolkovaprojects.texthunter.ui.savedDocs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissValue.DismissedToEnd
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sofiyavolkovaprojects.texthunter.R.drawable
import com.sofiyavolkovaprojects.texthunter.R.string
import com.sofiyavolkovaprojects.texthunter.data.local.database.DocumentItem
import com.sofiyavolkovaprojects.texthunter.ui.components.CustomCircularProgressBar
import com.sofiyavolkovaprojects.texthunter.ui.components.InfoMessage
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.EditText
import com.sofiyavolkovaprojects.texthunter.ui.savedDocs.SavedDocsUiState.Empty
import com.sofiyavolkovaprojects.texthunter.ui.savedDocs.SavedDocsUiState.Error
import com.sofiyavolkovaprojects.texthunter.ui.savedDocs.SavedDocsUiState.Loading
import com.sofiyavolkovaprojects.texthunter.ui.savedDocs.SavedDocsUiState.Success
import com.sofiyavolkovaprojects.texthunter.ui.theme.THTheme

@Composable
fun SavedDocsScreen(
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit = {},
    viewModel: SavedDocsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var docList: List<DocumentItem> = remember { mutableStateListOf() }

    when (uiState) {
        is Success ->
            docList = (uiState as Success).data

        Loading -> CustomCircularProgressBar()
        Error -> InfoMessage(
            imagePainter = painterResource(drawable.error_message_01),
            title = stringResource(string.th_error_title),
            bodyText = stringResource(string.th_error_body_message)
        )
        Empty -> InfoMessage(
            title = stringResource(string.th_saved_docs_screen_no_saved_docs_message_title),
            bodyText = stringResource(string.th_saved_docs_screen_no_saved_docs_body_message)
        )
    }

    SavedDocsScreen(
        modifier = modifier,
        docList = docList,
        navigateTo = navigateTo
    ) { document -> viewModel.removeDocument(document) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SavedDocsScreen(
    modifier: Modifier = Modifier,
    docList: List<DocumentItem>,
    navigateTo: (String) -> Unit = {},
    onSwipeCard: (DocumentItem) -> Unit,
) {
    LazyColumn {
        items(docList) { document ->
            DocumentCard(
                modifier = Modifier.animateItemPlacement(),
                navigateTo = navigateTo,
                document = document
            ) {
                onSwipeCard(document)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentCard(
    modifier: Modifier,
    navigateTo: (String) -> Unit,
    document: DocumentItem,
    onSwipeCard: () -> Unit,
) {
    SwipeToDismiss(
        modifier = modifier,
        state = getDismissState(onSwipeCard),
        background = { RemovedCard() },
        dismissContent = { DocumentCard(navigateTo, document) }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun getDismissState(onSwipeCard: () -> Unit) = rememberDismissState(
    confirmValueChange = {
        if (it == DismissedToEnd) {
            onSwipeCard()
        }
        false
    }
)

@Composable
private fun DocumentCard(
    navigateTo: (String) -> Unit,
    document: DocumentItem
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 276.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable {
                navigateTo(EditText.createNavTextRoute(id = document.uid))
            }

    ) {
        Image(
            modifier = Modifier
                .height(35.dp),
            painter = painterResource(id = drawable.card_background_01),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

        Text(
            text = document.title,
            modifier = Modifier
                .fillMaxWidth()
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

// Previews

@Preview(showBackground = true)
@Composable

private fun DefaultPreview() {
    THTheme {
        SavedDocsScreen(
            docList = listOf(DocumentItem("Documento 1", LoremIpsum(6).values.first()))
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    THTheme {
        SavedDocsScreen(
            docList = listOf(DocumentItem("Documento 1", LoremIpsum(50).values.first()))
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun RemovedCard() {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        modifier = Modifier
            .height(height = 276.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center

        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null
            )
        }
    }
}
