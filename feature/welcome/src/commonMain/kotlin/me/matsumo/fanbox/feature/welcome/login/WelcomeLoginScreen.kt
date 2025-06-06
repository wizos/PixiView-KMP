package me.matsumo.fanbox.feature.welcome.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.matsumo.fanbox.core.logs.category.WelcomeLog
import me.matsumo.fanbox.core.resources.Res
import me.matsumo.fanbox.core.resources.vec_welcome_plus
import me.matsumo.fanbox.core.resources.welcome_login_button_login
import me.matsumo.fanbox.core.resources.welcome_login_button_next
import me.matsumo.fanbox.core.resources.welcome_login_message
import me.matsumo.fanbox.core.resources.welcome_login_other_title
import me.matsumo.fanbox.core.resources.welcome_login_ready_message
import me.matsumo.fanbox.core.resources.welcome_login_ready_title
import me.matsumo.fanbox.core.resources.welcome_login_title
import me.matsumo.fanbox.core.resources.welcome_login_toast_failed
import me.matsumo.fanbox.core.ui.AsyncLoadContents
import me.matsumo.fanbox.core.ui.extensition.LocalNavigationType
import me.matsumo.fanbox.core.ui.extensition.LocalSnackbarHostState
import me.matsumo.fanbox.core.ui.extensition.NavigatorExtension
import me.matsumo.fanbox.core.ui.extensition.PixiViewNavigationType
import me.matsumo.fanbox.core.ui.extensition.ToastExtension
import me.matsumo.fanbox.core.ui.theme.bold
import me.matsumo.fanbox.core.ui.theme.center
import me.matsumo.fanbox.feature.welcome.WelcomeIndicatorItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun WelcomeLoginScreen(
    navigateToHome: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WelcomeLoginViewModel = koinViewModel(),
    navigatorExtension: NavigatorExtension = koinInject(),
    toastExtension: ToastExtension = koinInject(),
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val navigationType = LocalNavigationType.current.type
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val isLoginError by viewModel.triggerLoginError.collectAsStateWithLifecycle(-1)

    LaunchedEffect(true) {
        viewModel.fetchLoggedIn()
    }

    if (isLoginError != -1) {
        LaunchedEffect(isLoginError) {
            toastExtension.show(snackbarHostState, Res.string.welcome_login_toast_failed)
        }
    }

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
        retryAction = { viewModel.fetchLoggedIn() },
    ) { isLoggedIn ->
        if (isLoggedIn) {
            LaunchedEffect(isLoggedIn) {
                WelcomeLog.loggedIn()
                navigateToHome.invoke()
            }
        }

        if (navigationType != PixiViewNavigationType.PermanentNavigationDrawer) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FirstSection()

                SecondSection(
                    modifier = Modifier.weight(1f),
                    isLoggedIn = isLoggedIn,
                    navigateToLoginScreen = {
                        viewModel.logout()
                        navigateToLoginScreen.invoke()
                    },
                    navigateToHome = navigateToHome,
                    navigateToHelp = { navigatorExtension.navigateToWebPage(it, "") },
                    onSetSessionId = viewModel::setSessionId,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FirstSection(
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(24.dp))

                Column(Modifier.weight(1f)) {
                    Box(Modifier.weight(2f))

                    SecondSection(
                        modifier = Modifier.weight(3f),
                        isLoggedIn = isLoggedIn,
                        navigateToLoginScreen = {
                            viewModel.logout()
                            navigateToLoginScreen.invoke()
                        },
                        navigateToHome = navigateToHome,
                        navigateToHelp = { navigatorExtension.navigateToWebPage(it, "") },
                        onSetSessionId = viewModel::setSessionId,
                    )
                }
            }
        }
    }
}

@Composable
private fun FirstSection(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.padding(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp,
        ),
        painter = painterResource(Res.drawable.vec_welcome_plus),
        contentDescription = null,
    )
}

@Composable
private fun SecondSection(
    isLoggedIn: Boolean,
    onSetSessionId: (String) -> Unit,
    navigateToLoginScreen: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToHelp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowLoginDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(if (isLoggedIn) Res.string.welcome_login_ready_title else Res.string.welcome_login_title),
            style = MaterialTheme.typography.displaySmall.bold(),
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(if (isLoggedIn) Res.string.welcome_login_ready_message else Res.string.welcome_login_message),
            style = MaterialTheme.typography.bodySmall.center(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            modifier = Modifier.padding(bottom = 8.dp),
            onClick = { isShowLoginDialog = true },
        ) {
            Text(
                text = stringResource(Res.string.welcome_login_other_title),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        WelcomeIndicatorItem(
            modifier = Modifier.padding(bottom = 24.dp),
            max = 2,
            step = 2,
        )

        if (isLoggedIn) {
            Button(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                onClick = { navigateToHome.invoke() },
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(Res.string.welcome_login_button_next),
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                onClick = { navigateToLoginScreen.invoke() },
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(Res.string.welcome_login_button_login),
                )
            }
        }
    }

    if (isShowLoginDialog) {
        WelcomeLoginDialog(
            onDismissRequest = { isShowLoginDialog = false },
            onClickHelp = navigateToHelp,
            onClickLogin = { sessionId ->
                onSetSessionId.invoke(sessionId)
                isShowLoginDialog = false
            },
        )
    }
}
