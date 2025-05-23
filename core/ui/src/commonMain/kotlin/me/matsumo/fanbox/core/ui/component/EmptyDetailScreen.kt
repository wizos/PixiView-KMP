package me.matsumo.fanbox.core.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.matsumo.fanbox.core.model.Destination
import me.matsumo.fanbox.core.resources.Res
import me.matsumo.fanbox.core.resources.empty_detail_description
import me.matsumo.fanbox.core.resources.empty_detail_title
import me.matsumo.fanbox.core.ui.view.EmptyView

fun NavGraphBuilder.emptyDetailScreen() {
    composable<Destination.Empty> {
        EmptyView(
            modifier = Modifier.fillMaxSize(),
            titleRes = Res.string.empty_detail_title,
            messageRes = Res.string.empty_detail_description,
        )
    }
}
