package me.matsumo.fanbox.feature.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import me.matsumo.fanbox.core.model.Destination
import me.matsumo.fanbox.core.ui.AsyncLoadContents
import me.matsumo.fanbox.core.ui.extensition.LocalNavigationType
import me.matsumo.fanbox.core.ui.extensition.LocalSnackbarHostState
import me.matsumo.fanbox.core.ui.extensition.PixiViewNavigationType
import me.matsumo.fanbox.feature.library.component.LibraryCompactScreen
import me.matsumo.fanbox.feature.library.component.LibraryDestination
import me.matsumo.fanbox.feature.library.component.LibraryExpandedScreen
import me.matsumo.fanbox.feature.library.component.LibraryMediumScreen
import me.matsumo.fanbox.feature.library.discovery.navigateToLibraryDiscovery
import me.matsumo.fanbox.feature.library.home.navigateToLibraryHome
import me.matsumo.fanbox.feature.library.message.navigateToLibraryMessage
import me.matsumo.fanbox.feature.library.notify.navigateToLibraryNotify
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryScreen(
    navHostController: NavHostController,
    navigateTo: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = koinViewModel(),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val snackbarHostState = LocalSnackbarHostState.current
    val navigationType = LocalNavigationType.current.type
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
    ) {
        when (navigationType) {
            PixiViewNavigationType.BottomNavigation -> {
                LibraryCompactScreen(
                    modifier = Modifier.fillMaxSize(),
                    uiState = it,
                    drawerState = drawerState,
                    snackbarHostState = snackbarHostState,
                    navController = navHostController,
                    navigateTo = navigateTo,
                )
            }

            PixiViewNavigationType.NavigationRail -> {
                LibraryMediumScreen(
                    modifier = Modifier.fillMaxSize(),
                    uiState = it,
                    drawerState = drawerState,
                    snackbarHostState = snackbarHostState,
                    navController = navHostController,
                    navigateTo = navigateTo,
                )
            }

            PixiViewNavigationType.PermanentNavigationDrawer -> {
                LibraryExpandedScreen(
                    modifier = Modifier.fillMaxSize(),
                    uiState = it,
                    drawerState = drawerState,
                    snackbarHostState = snackbarHostState,
                    navController = navHostController,
                    navigateTo = navigateTo,
                )
            }
        }
    }
}

fun NavHostController.navigateToLibraryDestination(destination: LibraryDestination) {
    val navOption = navOptions {
        popUpTo(graph.findStartDestination().route.orEmpty()) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    when (destination) {
        LibraryDestination.Home -> navigateToLibraryHome(navOption)
        LibraryDestination.Discovery -> navigateToLibraryDiscovery(navOption)
        LibraryDestination.Notify -> navigateToLibraryNotify(navOption)
        LibraryDestination.Message -> navigateToLibraryMessage(navOption)
    }
}
