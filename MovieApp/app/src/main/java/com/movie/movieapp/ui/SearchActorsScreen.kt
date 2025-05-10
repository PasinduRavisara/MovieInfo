package com.movie.movieapp.ui

import com.movie.movieapp.viewmodel.MovieViewModel
import com.movie.movieapp.data.Movie
import com.movie.movieapp.ui.theme.NetflixRed
import com.movie.movieapp.ui.theme.NetflixWhite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchActorsScreen(viewModel: MovieViewModel, isDarkMode: Boolean) {
    var actor by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<Movie>()) }
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = actor,
                onValueChange = { actor = it },
                label = { Text("Enter actor name", color = NetflixRed) },
                placeholder = { Text("e.g. Tom Cruise") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                    unfocusedTextColor = if (isDarkMode) Color.White else Color.Black,
                    cursorColor = NetflixRed,
                    focusedBorderColor = NetflixRed,
                    unfocusedBorderColor = NetflixRed,
                    focusedLabelColor = NetflixRed,
                    unfocusedLabelColor = NetflixRed
                )
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.searchMoviesByActor(actor) { 
                        results = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = actor.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NetflixRed,
                    contentColor = NetflixWhite
                )
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = NetflixWhite)
                Spacer(Modifier.width(8.dp))
                Text("Search", color = NetflixWhite)
            }
            Spacer(Modifier.height(16.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                }
            } else if (results.isEmpty() && actor.isNotBlank()) {
                Text(
                    text = "No movies found for this actor",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results) { movie ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = movie.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Year: ${movie.year}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Actors: ${movie.actors}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (movie.imdbID.startsWith("tt")) "Source: OMDB" else "Source: Local Database",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
