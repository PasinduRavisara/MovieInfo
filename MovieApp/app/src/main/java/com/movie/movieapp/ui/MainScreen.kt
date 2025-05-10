package com.movie.movieapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.movie.movieapp.viewmodel.MovieViewModel
import kotlinx.coroutines.delay
import com.movie.movieapp.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun MainScreen(
    viewModel: MovieViewModel,
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    var searchQuery by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    var shouldNavigate by remember { mutableStateOf(false) }
    var showAddMoviesSnackbar by remember { mutableStateOf(false) }
    
    // Navigate to search_results after loading is done
    LaunchedEffect(isLoading, shouldNavigate) {
        if (!isLoading && shouldNavigate) {
            navController.navigate("search_results")
            shouldNavigate = false
        }
    }
    
    // Auto-hide Snackbar after 2 seconds
    LaunchedEffect(showAddMoviesSnackbar) {
        if (showAddMoviesSnackbar) {
            delay(2000)
            showAddMoviesSnackbar = false
        }
    }
    
    NavHost(navController, startDestination = "main") {
        composable("main") {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Settings button at the top
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { navController.navigate("settings") }) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = if (isDarkMode) Color.White else Color.Black
                                )
                            }
                        }

                        // App title and description
                        Text(
                            text = "🎬 MovieInfo",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = NetflixRed,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Your personal movie database",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 32.dp),
                            textAlign = TextAlign.Center
                        )

                        // Search card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    label = { Text("Search movies by title", color = if (isDarkMode) NetflixWhite else NetflixRed) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NetflixRed,
                                        unfocusedBorderColor = NetflixRed,
                                        focusedLabelColor = if (isDarkMode) NetflixWhite else NetflixRed,
                                        unfocusedLabelColor = if (isDarkMode) NetflixWhite else NetflixRed,
                                        cursorColor = NetflixRed
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { 
                                        if (searchQuery.isNotBlank()) {
                                            viewModel.searchMoviesByTitle(searchQuery)
                                            shouldNavigate = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = searchQuery.isNotBlank() && !isLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = NetflixRed, contentColor = NetflixWhite)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = NetflixWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Search", color = NetflixWhite)
                                }
                                if (isLoading) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    CircularProgressIndicator(color = NetflixRed)
                                }
                            }
                        }
                        
                        // Menu buttons card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NetflixMenuButton(
                                    text = "Add Movies to DB",
                                    icon = Icons.Default.Add,
                                    onClick = {
                                        viewModel.addHardcodedMovies()
                                        showAddMoviesSnackbar = true
                                    },
                                    isDarkMode = isDarkMode
                                )
                                Spacer(Modifier.height(12.dp))
                                NetflixMenuButton(
                                    text = "Search for Movies",
                                    icon = Icons.Default.Search,
                                    onClick = { navController.navigate("search_movies") },
                                    isDarkMode = isDarkMode
                                )
                                Spacer(Modifier.height(12.dp))
                                NetflixMenuButton(
                                    text = "Search for Actors",
                                    icon = Icons.Default.Person,
                                    onClick = { navController.navigate("search_actors") },
                                    isDarkMode = isDarkMode
                                )
                                Spacer(Modifier.height(12.dp))
                                NetflixMenuButton(
                                    text = "View Saved Movies",
                                    icon = Icons.Default.List,
                                    onClick = { navController.navigate("saved_movies") },
                                    isDarkMode = isDarkMode
                                )
                            }
                        }
                    }

                    if (showAddMoviesSnackbar) {
                        Snackbar(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                "Movies added to database",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        composable("search_movies") { SearchMoviesScreen(viewModel, isDarkMode) }
        composable("search_actors") { SearchActorsScreen(viewModel, isDarkMode) }
        composable("saved_movies") { SavedMoviesScreen(viewModel) }
        composable("settings") {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeChanged = onDarkModeChanged,
                onBackPressed = { navController.navigateUp() }
            )
        }
        composable("search_results") {
            SearchResultsScreen(
                viewModel = viewModel,
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}

@Composable
fun NetflixMenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val backgroundColor = if (isDarkMode) NetflixDarkGray.copy(alpha = 0.8f) else NetflixRed.copy(alpha = 0.08f)
    val contentColor = if (isDarkMode) NetflixWhite else NetflixBlack
    val iconColor = NetflixRed
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 18.sp, color = contentColor, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = iconColor)
    }
}

@Composable
fun SearchResultsScreen(
    viewModel: MovieViewModel,
    onBackPressed: () -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    var showSnackbar by remember { mutableStateOf(false) }
    
    // Auto-hide Snackbar after 2 seconds
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            delay(2000)
            showSnackbar = false
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NetflixRed
                        )
                    }
                    Text(
                        text = "Search Results",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NetflixRed
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
                
                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No movies found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { movie ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        text = movie.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "Year: ${movie.year}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Rated: ${movie.rated}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Runtime: ${movie.runtime}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Genre: ${movie.genre}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Director: ${movie.director}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Writers: ${movie.writer}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Actors: ${movie.actors}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = movie.plot,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(16.dp))

                                }
                            }
                        }
                    }
                }
            }
            
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    action = {
                        TextButton(
                            onClick = { showSnackbar = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(
                        "Movie has been added to the database",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: com.movie.movieapp.data.Movie,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Year: ${movie.year}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Genre: ${movie.genre}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Director: ${movie.director}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Actors: ${movie.actors}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.plot,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSaveClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save to Database")
            }
        }
    }
}
