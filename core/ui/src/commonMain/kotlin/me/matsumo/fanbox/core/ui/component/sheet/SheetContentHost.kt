package me.matsumo.fanbox.core.ui.component.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.LocalOwnersProvider
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop

@Composable
internal fun ColumnScope.SheetContentHost(
    backStackEntry: NavBackStackEntry?,
    sheetState: ModalBottomSheetState,
    saveableStateHolder: SaveableStateHolder,
    onSheetShown: (entry: NavBackStackEntry) -> Unit,
    onSheetDismissed: (entry: NavBackStackEntry) -> Unit,
) {
    if (backStackEntry != null) {
        val currentOnSheetShown by rememberUpdatedState(onSheetShown)
        val currentOnSheetDismissed by rememberUpdatedState(onSheetDismissed)
        LaunchedEffect(sheetState, backStackEntry) {
            snapshotFlow { sheetState.isVisible }
                // We are only interested in changes in the sheet's visibility
                .distinctUntilChanged()
                // distinctUntilChanged emits the initial value which we don't need
                .drop(1)
                .collect { visible ->
                    if (visible) {
                        currentOnSheetShown(backStackEntry)
                    } else {
                        currentOnSheetDismissed(backStackEntry)
                    }
                }
        }
        backStackEntry.LocalOwnersProvider(saveableStateHolder) {
            val content = (backStackEntry.destination as BottomSheetNavigator.Destination).content
            content(backStackEntry)
        }
    }
}
