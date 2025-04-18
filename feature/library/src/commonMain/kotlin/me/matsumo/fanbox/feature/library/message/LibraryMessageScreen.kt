package me.matsumo.fanbox.feature.library.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import me.matsumo.fanbox.core.model.Destination
import me.matsumo.fanbox.core.resources.Res
import me.matsumo.fanbox.core.resources.error_no_data
import me.matsumo.fanbox.core.resources.error_no_data_message
import me.matsumo.fanbox.core.resources.library_navigation_message
import me.matsumo.fanbox.core.ui.AsyncLoadContents
import me.matsumo.fanbox.core.ui.component.PixiViewTopBar
import me.matsumo.fanbox.core.ui.extensition.LocalNavigationType
import me.matsumo.fanbox.core.ui.extensition.PixiViewNavigationType
import me.matsumo.fanbox.core.ui.extensition.drawVerticalScrollbar
import me.matsumo.fanbox.core.ui.view.EmptyView
import me.matsumo.fanbox.feature.library.message.items.LibraryMessageItem
import me.matsumo.fankt.fanbox.domain.model.FanboxNewsLetter
import me.matsumo.fankt.fanbox.domain.model.id.FanboxCreatorId
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LibraryMessageRoute(
    openDrawer: () -> Unit,
    navigateTo: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryMessageViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = viewModel::fetch,
    ) { uiState ->
        LibraryMessageScreen(
            modifier = Modifier.fillMaxSize(),
            messages = uiState.messages.toImmutableList(),
            openDrawer = openDrawer,
            onClickCreator = { navigateTo(Destination.CreatorTop(it, true)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryMessageScreen(
    messages: ImmutableList<FanboxNewsLetter>,
    openDrawer: () -> Unit,
    onClickCreator: (FanboxCreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationType = LocalNavigationType.current.type
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiViewTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.library_navigation_message),
                navigationIcon = Icons.Default.Menu,
                onClickNavigation = if (navigationType != PixiViewNavigationType.PermanentNavigationDrawer) openDrawer else null,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            HorizontalDivider()
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            if (messages.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.drawVerticalScrollbar(state),
                    state = state,
                ) {
                    items(
                        items = messages,
                        key = { it.id.uniqueValue },
                    ) {
                        LibraryMessageItem(
                            modifier = Modifier.fillMaxWidth(),
                            message = it,
                            onClickCreator = onClickCreator,
                        )

                        HorizontalDivider()
                    }
                }
            } else {
                EmptyView(
                    modifier = Modifier.fillMaxSize(),
                    titleRes = Res.string.error_no_data,
                    messageRes = Res.string.error_no_data_message,
                )
            }
        }
    }
}
