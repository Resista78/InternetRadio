package com.armanmaurya.internetradio.ui.mobile.screens.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.armanmaurya.internetradio.ui.shared.viewmodels.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStationScreen(
    stationUuid: String?,
    viewModel: LibraryViewModel,
    onNavigateBack: () -> Unit
) {
    val stations by viewModel.stations.collectAsStateWithLifecycle()
    val station = if (stationUuid != null) stations.find { it.stationUuid == stationUuid } else null

    val isEditing = stationUuid != null

    if (isEditing && station == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Station") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Station not found")
            }
        }
    } else {
        var name by remember { mutableStateOf(station?.name ?: "") }
        var url by remember { mutableStateOf(station?.url ?: "") }
        var favicon by remember { mutableStateOf(station?.favicon ?: "") }
        var tags by remember { mutableStateOf(station?.tags?.joinToString(", ") ?: "") }
        var country by remember { mutableStateOf(station?.country ?: "") }
        var language by remember { mutableStateOf(station?.language ?: "") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditing) "Edit Station" else "Add Custom Station") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Stream URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = favicon,
                    onValueChange = { favicon = it },
                    label = { Text("Favicon URL (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Country (Optional)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text("Language (Optional)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (isEditing && station != null) {
                            val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            viewModel.updateStation(
                                stationUuid = station.stationUuid,
                                name = name,
                                url = url,
                                favicon = favicon,
                                tags = tagList
                            )
                        } else {
                            viewModel.addStation(
                                name = name,
                                url = url,
                                favicon = favicon,
                                tags = tags,
                                country = country,
                                state = "",
                                language = language
                            )
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && url.isNotBlank()
                ) {
                    Text(if (isEditing) "Save Changes" else "Add Station")
                }
            }
        }
    }
}
