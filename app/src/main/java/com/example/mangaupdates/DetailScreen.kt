package com.example.mangaupdates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun DetailScreen(seriesInfo: MangaUpdatesApi.SeriesInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        AsyncImage(
            model = seriesInfo.image.url.original,
            contentDescription = seriesInfo.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = seriesInfo.title, style = MaterialTheme.typography.headlineSmall)
        Text(text = seriesInfo.type)
        Text(text = "Year: ${seriesInfo.year}")
        Text(text = seriesInfo.description)
        Text(text = "Genres: ${seriesInfo.genres.joinToString()}")

        seriesInfo.metadata?.user_list?.status?.let { status ->
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text("Your Progress:", style = MaterialTheme.typography.titleMedium)
            status.score?.let { Text("Score: $it") }
            status.chapter?.let { Text("Chapters Read: $it") }
            status.volume?.let { Text("Volumes Read: $it") }
        }
    }
}
