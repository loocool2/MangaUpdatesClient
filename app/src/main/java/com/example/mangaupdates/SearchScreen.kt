package com.example.mangaupdates


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.navigation.NavController

@Composable
fun SearchScreen(username: String, navController: NavController) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<MangaUpdatesApi.SeriesInfo>()) }
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp)) {
        Text("Welcome $username", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Manga Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                try {
                    val request = MangaUpdatesApi.SearchRequest(search = query)
                    val response = ApiClient.api.searchManga(request)

                    // Extract SeriesInfo from the nested 'record' field
                    val seriesList = response.results.map { it.record }

                    results = seriesList
                    Log.d("SearchScreen", "Search successful: ${seriesList.size} items found")

                } catch (e: Exception) {
                    Log.e("SearchError", e.message ?: "Unknown error")
                    results = emptyList()
                }
            }
        }) {
            Text("Search")
        }


        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(results) { series ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val imageUrl = series.image.url.thumb

                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = series.title,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        contentScale = ContentScale.FillHeight
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set("series", series)
                                navController.navigate("details")
                            }
                            .padding(8.dp)
                    ) {
                        Text(series.title, style = MaterialTheme.typography.titleMedium)
                        Text("Type: ${series.type ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                        Text("Year: ${series.year ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)

                    }
                }
            }
        }

    }
}
