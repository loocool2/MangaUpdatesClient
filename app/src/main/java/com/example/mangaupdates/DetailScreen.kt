package com.example.mangaupdates

import DetailViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items

import com.example.mangaupdates.MangaUpdatesApi.ListInfo

@Composable
fun DetailScreen(seriesInfo: MangaUpdatesApi.SeriesInfo, token: String,  userId: Long, viewModel: DetailViewModel = viewModel()) {
    val scrollState = rememberScrollState()

    val listStatus by viewModel.seriesListStatus.collectAsState()

    val lists by viewModel.userLists.collectAsState()
    val addResult by viewModel.addResult.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var pickedList by remember { mutableStateOf<ListInfo?>(null) }


    LaunchedEffect(seriesInfo.id, token, userId) {
        viewModel.fetchUserLists(userId, token)
        viewModel.fetchSeriesListStatus(seriesInfo.id, token)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Cover Image
        Image(
            painter = rememberAsyncImagePainter(seriesInfo.image.url.original),
            contentDescription = seriesInfo.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main Info
        Text(text = seriesInfo.title, style = MaterialTheme.typography.headlineSmall)
        Text(text = seriesInfo.type, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Year: ${seriesInfo.year}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Genres: ${seriesInfo.genres.joinToString { it.genre }}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = seriesInfo.description, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(24.dp))
        Text("Your List Status:", style = MaterialTheme.typography.titleMedium)

        listStatus?.status?.let {
            Text("Chapters Read: ${it.chapter ?: 0}")
            Text("Volumes Read: ${it.volume ?: 0}")
        } ?: Text("Not in list")

        Spacer(Modifier.height(24.dp))
        if (lists.isNotEmpty()) {
            Button(onClick = { showDialog = true }) {
                Text("Add to List")
            }
        }

        // show success / failure
        addResult?.let { success ->
            Text(
                if (success) "Added to \"${pickedList?.title}\" ✓"
                else "Failed to add to list",
                color = if (success) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Dialog to pick which list
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Pick a list") },
            text = {
                LazyColumn {
                    items(lists) { listInfo ->
                        Text(
                            listInfo.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pickedList = listInfo
                                    viewModel.addToList(seriesInfo, listInfo.list_id, token)
                                    showDialog = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = { /* TODO */ },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
