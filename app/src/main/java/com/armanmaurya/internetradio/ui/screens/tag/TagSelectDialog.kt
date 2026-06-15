package com.armanmaurya.internetradio.ui.screens.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TagSelectDialog(
    initialTags: Set<String>,
    onDismissRequest: () -> Unit,
    onSaveTags: (Set<String>) -> Unit,
    viewModel: TagSelectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTags by remember { mutableStateOf(initialTags) }
    var inputText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val availableTags = uiState.tags.map { it.name }
    val filteredAvailableTags = availableTags.filter {
        it.contains(inputText, ignoreCase = true) && !selectedTags.contains(it)
    }.take(5) // Limit to 5 suggestions for better UX

    val showCreateOption = inputText.isNotBlank() &&
            !availableTags.any { it.equals(inputText.trim(), ignoreCase = true) } &&
            !selectedTags.any { it.equals(inputText.trim(), ignoreCase = true) }

    val shouldShowDropdown = expanded && (filteredAvailableTags.isNotEmpty() || showCreateOption)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Tags") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = shouldShowDropdown,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                            expanded = true
                        },
                        label = { Text("Search tags") },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shouldShowDropdown) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val trimmed = inputText.trim()
                                if (trimmed.isNotBlank() && !selectedTags.any { it.equals(trimmed, ignoreCase = true) }) {
                                    selectedTags = selectedTags + trimmed
                                    inputText = ""
                                    expanded = false
                                }
                            }
                        ),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = shouldShowDropdown,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (showCreateOption) {
                            DropdownMenuItem(
                                text = { Text("Add \"${inputText.trim()}\"") },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                                onClick = {
                                    selectedTags = selectedTags + inputText.trim()
                                    inputText = ""
                                    expanded = false
                                }
                            )
                        }
                        filteredAvailableTags.forEach { tag ->
                            DropdownMenuItem(
                                text = { Text(tag) },
                                onClick = {
                                    selectedTags = selectedTags + tag
                                    inputText = ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedTags.isNotEmpty()) {
                    Text(
                        text = "Selected Tags",
                        style = MaterialTheme.typography.labelMedium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        selectedTags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { selectedTags = selectedTags - tag },
                                label = { Text(tag) },
                                shape = RoundedCornerShape(32.dp),
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove tag",
                                        modifier = Modifier.size(InputChipDefaults.AvatarSize)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSaveTags(selectedTags) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { selectedTags = emptySet() }) {
                    Text("Clear")
                }
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        }
    )
}
