package com.armanmaurya.internetradio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearchCleared: () -> Unit,
    onCountryClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onSettingsClick: () -> Unit,
    selectedCountryCode: String?,
    selectedLanguage: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val horizontalPadding by animateDpAsState(
        targetValue = if (isSearchExpanded) 0.dp else 16.dp,
        label = "SearchBarPadding"
    )

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { onSearchExpandedChange(false) },
                expanded = isSearchExpanded,
                onExpandedChange = onSearchExpandedChange,
                placeholder = { Text("Search stations") },
                leadingIcon = {
                    if (isSearchExpanded) {
                        IconButton(onClick = { onSearchExpandedChange(false) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {
                    if (isSearchActive) {
                        IconButton(onClick = onSearchCleared) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onCountryClick) {
                                Box {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = "Select Country",
                                        tint = if (!selectedCountryCode.isNullOrBlank()) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                    if (!selectedCountryCode.isNullOrBlank()) {
                                        Text(
                                            text = selectedCountryCode,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = 6.dp, y = (-4).dp)
                                        )
                                    }
                                }
                            }
                            IconButton(onClick = onLanguageClick) {
                                Box {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = "Select Language",
                                        tint = if (!selectedLanguage.isNullOrBlank()) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!selectedLanguage.isNullOrBlank()) {
                                        Text(
                                            text = selectedLanguage.take(2).uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = 6.dp, y = (-4).dp)
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = !isSearchExpanded,
                                enter = expandHorizontally() + fadeIn(),
                                exit = shrinkHorizontally() + fadeOut()
                            ) {
                                IconButton(onClick = onSettingsClick) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                        }
                    }
                }
            )
        },
        expanded = isSearchExpanded,
        onExpandedChange = onSearchExpandedChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(bottom = 4.dp)
    ) {
        content()
    }
}
