package com.armanmaurya.internetradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.armanmaurya.internetradio.data.repository.SettingsRepository
import com.armanmaurya.internetradio.ui.navigation.AppNavHost
import com.armanmaurya.internetradio.ui.screens.player.PlayerSheetContent
import com.armanmaurya.internetradio.ui.player.PlayerViewModel
import com.armanmaurya.internetradio.ui.theme.InternetRadioTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appPreferences by settingsRepository.appPreferencesFlow
                .collectAsStateWithLifecycle(initialValue = com.armanmaurya.internetradio.data.model.AppPreferences())

            InternetRadioTheme(appPreferences = appPreferences) {
                val navController = rememberNavController()
                val playerViewModel: PlayerViewModel = hiltViewModel()
                val playbackState by playerViewModel.playbackState.collectAsStateWithLifecycle()

                val scope = rememberCoroutineScope()
                val scaffoldState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberStandardBottomSheetState(
                        initialValue = SheetValue.PartiallyExpanded,
                        skipHiddenState = false
                    )
                )

                val density = LocalDensity.current
                val sheetPeekHeight = if (playbackState.currentStation != null) 72.dp else 0.dp

                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeight,
                    sheetDragHandle = null,
                    sheetContent = {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .heightIn(min = 72.dp)
                        ) {
                            val fullHeight = constraints.maxHeight.toFloat()
                            val peekHeightPx = with(density) { 72.dp.toPx() }
                            
                            val progress by remember(fullHeight, peekHeightPx) {
                                derivedStateOf {
                                    val currentOffset = try {
                                        scaffoldState.bottomSheetState.requireOffset()
                                    } catch (e: Exception) {
                                        fullHeight - peekHeightPx
                                    }
                                    
                                    val totalRange = fullHeight - peekHeightPx
                                    if (totalRange > 0) {
                                        (1f - (currentOffset / totalRange)).coerceIn(0f, 1f)
                                    } else {
                                        0f
                                    }
                                }
                            }

                            val isFavorite by playerViewModel.isFavorite.collectAsStateWithLifecycle()

                            PlayerSheetContent(
                                playbackState = playbackState,
                                isFavorite = isFavorite,
                                progress = progress,
                                onTogglePlayPause = playerViewModel::togglePlayPause,
                                onStop = {
                                    playerViewModel.stop()
                                    scope.launch { scaffoldState.bottomSheetState.hide() }
                                },
                                onToggleFavorite = playerViewModel::toggleFavorite,
                                onCollapse = {
                                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                                },
                                onExpand = {
                                    scope.launch { scaffoldState.bottomSheetState.expand() }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}