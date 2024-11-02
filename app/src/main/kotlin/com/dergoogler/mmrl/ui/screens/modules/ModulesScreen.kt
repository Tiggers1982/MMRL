package com.dergoogler.mmrl.ui.screens.modules

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.datastore.modules.ModulesMenuCompat
import com.dergoogler.mmrl.model.local.LocalModule
import com.dergoogler.mmrl.model.online.VersionItem
import com.dergoogler.mmrl.ui.component.Loading
import com.dergoogler.mmrl.ui.component.PageIndicator
import com.dergoogler.mmrl.ui.component.SearchTopBar
import com.dergoogler.mmrl.ui.component.TopAppBarIcon
import com.dergoogler.mmrl.ui.utils.isScrollingUp
import com.dergoogler.mmrl.ui.utils.none
import com.dergoogler.mmrl.viewmodel.ModulesViewModel
import ext.dergoogler.mmrl.activity.MMRLComponentActivity

@Composable
fun ModulesScreen(
    @Suppress("UNUSED_PARAMETER")
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val list by viewModel.local.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val isScrollingUp by listState.isScrollingUp()
    val showFab by remember {
        derivedStateOf {
            isScrollingUp && !viewModel.isSearch && viewModel.isProviderAlive
        }
    }

    val download: (LocalModule, VersionItem, Boolean) -> Unit = { module, item, install ->
        viewModel.downloader(context, module, item) {
            if (install) {
                MMRLComponentActivity.startInstallActivity(
                    context = context,
                    uri = it.toUri()
                )
            }
        }
    }

    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::closeSearch
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                query = query,
                onQueryChange = viewModel::search,
                onOpenSearch = viewModel::openSearch,
                onCloseSearch = viewModel::closeSearch,
                setMenu = viewModel::setModulesMenu,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = scaleIn(
                    animationSpec = tween(100),
                    initialScale = 0.8f
                ),
                exit = scaleOut(
                    animationSpec = tween(100),
                    targetScale = 0.8f
                )
            ) {
                FloatingButton()
            }
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (viewModel.isLoading) {
                Loading()
            }

            if (list.isEmpty() && !viewModel.isLoading) {
                PageIndicator(
                    icon = R.drawable.keyframes,
                    text = if (viewModel.isSearch) R.string.search_empty else R.string.modules_empty,
                )
            }

            ModulesList(
                list = list,
                state = listState,
                isProviderAlive = viewModel.isProviderAlive,
                getModuleOps = viewModel::createModuleOps,
                getVersionItem = { viewModel.getVersionItem(it) },
                getProgress = { viewModel.getProgress(it) },
                onDownload = download
            )
        }
    }
}

@Composable
private fun TopBar(
    isSearch: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    setMenu: (ModulesMenuCompat) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var currentQuery by remember { mutableStateOf(query) }
    DisposableEffect(isSearch) {
        onDispose { currentQuery = "" }
    }

    SearchTopBar(
        isSearch = isSearch,
        query = currentQuery,
        onQueryChange = {
            onQueryChange(it)
            currentQuery = it
        },
        onClose = {
            onCloseSearch()
            currentQuery = ""
        },
        title = { TopAppBarIcon() },
        scrollBehavior = scrollBehavior,
        actions = {
            if (!isSearch) {
                IconButton(
                    onClick = onOpenSearch
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = null
                    )
                }
            }

            ModulesMenu(
                setMenu = setMenu
            )
        }
    )
}

@Composable
private fun FloatingButton() {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        MMRLComponentActivity.startInstallActivity(
            context = context,
            uri = uri
        )
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch("application/zip")
            }
        }
    }

    FloatingActionButton(
        interactionSource = interactionSource,
        onClick = {},
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.package_import),
            contentDescription = null
        )
    }
}