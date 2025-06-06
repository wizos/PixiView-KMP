package me.matsumo.fanbox.feature.about.billing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import me.matsumo.fanbox.core.model.Destination
import me.matsumo.fanbox.core.ui.component.sheet.bottomSheet
import me.matsumo.fanbox.core.ui.extensition.BackHandler

fun NavGraphBuilder.billingPlusBottomSheet(
    terminate: () -> Unit,
) {
    bottomSheet<Destination.BillingPlusBottomSheet> {
        val referrer = it.toRoute<Destination.BillingPlusBottomSheet>().referrer.orEmpty()

        BackHandler {
            terminate()
        }

        BillingPlusRoute(
            modifier = Modifier.fillMaxSize(),
            referrer = referrer,
            terminate = terminate,
        )
    }
}
