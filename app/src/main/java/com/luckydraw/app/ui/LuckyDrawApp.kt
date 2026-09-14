package com.luckydraw.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luckydraw.app.R
import com.luckydraw.app.ads.BannerAd
import com.luckydraw.app.ads.InterstitialManager
import com.luckydraw.app.ui.components.ResultOverlay
import com.luckydraw.app.ui.components.SuspenseOverlay
import com.luckydraw.app.ui.history.HistorySheet
import com.luckydraw.app.ui.names.NamesScreen
import com.luckydraw.app.ui.numbers.NumbersScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyDrawApp(viewModel: DrawViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })
    var showHistory by remember { mutableStateOf(false) }

    // Ponto único onde o anúncio se encaixa: entre o suspense e a revelação.
    val phase = state.phase
    LaunchedEffect(phase) {
        if (phase is DrawPhase.AwaitingAd) {
            val activity = context.findActivity()
            if (phase.showAd && activity != null) {
                InterstitialManager.show(activity) { viewModel.onAdFlowFinished() }
            } else {
                viewModel.onAdFlowFinished()
            }
        }
        if (phase is DrawPhase.Revealed) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    // Convite para avaliar: só depois do resultado na tela, para a pessoa estar no
    // melhor momento possível — nunca no meio de um sorteio.
    if (state.shouldAskReview) {
        LaunchedEffect(Unit) {
            val activity = context.findActivity()
            if (activity == null) {
                viewModel.onReviewAsked()
            } else {
                ReviewPrompt.request(activity) { viewModel.onReviewAsked() }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                actions = {
                    IconButton(onClick = { showHistory = true }) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = stringResource(R.string.history_open),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
        // navigationBarsPadding: com edge-to-edge o Scaffold não recua a bottomBar
        // sozinho, e o banner acabaria por cima dos botões do sistema.
        bottomBar = { BannerAd(Modifier.navigationBarsPadding()) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // Em edge-to-edge o adjustResize não encolhe mais a janela:
                // sem isto o teclado cobre o botão de sortear.
                .imePadding()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    listOf(R.string.tab_numbers, R.string.tab_names).forEachIndexed { index, res ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    text = stringResource(res),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            },
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (page) {
                        0 -> NumbersScreen(
                            input = state.numbers,
                            error = state.numbersError,
                            onMinChange = viewModel::onMinChange,
                            onMaxChange = viewModel::onMaxChange,
                            onCountChange = viewModel::onNumbersCountChange,
                            onRepeatsChange = viewModel::onNumbersRepeatsChange,
                            onDraw = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.drawNumbers()
                            },
                        )

                        else -> NamesScreen(
                            input = state.names,
                            nameCount = viewModel.parsedNames().size,
                            error = state.namesError,
                            onTextChange = viewModel::onNamesTextChange,
                            onCountChange = viewModel::onNamesCountChange,
                            onRepeatsChange = viewModel::onNamesRepeatsChange,
                            onDraw = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.drawNames()
                            },
                        )
                    }
                }
            }

            when (val current = state.phase) {
                is DrawPhase.Suspense -> SuspenseOverlay(
                    samples = current.samples,
                    label = stringResource(R.string.suspense_label),
                )

                is DrawPhase.Revealed -> {
                    val text = current.record.result.joinToString(", ")
                    ResultOverlay(
                        items = current.record.result,
                        onCopy = {
                            context.copyToClipboard(text)
                            Toast.makeText(context, R.string.copied_message, Toast.LENGTH_SHORT).show()
                        },
                        onShare = { context.shareText(text) },
                        onDrawAgain = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.drawAgain()
                        },
                        onDismiss = viewModel::dismissResult,
                    )
                }

                // AwaitingAd mantém o suspense na tela até o anúncio assumir.
                is DrawPhase.AwaitingAd, DrawPhase.Idle -> Unit
            }
        }
    }

    if (showHistory) {
        HistorySheet(
            records = state.history,
            onClear = {
                viewModel.clearHistory()
                showHistory = false
            },
            onDismiss = { showHistory = false },
        )
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

private fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("LuckyDraw", text))
}

/**
 * Compartilha o resultado com o link da loja junto.
 *
 * O app é usado em grupo — amigo secreto, rifa, festa —, então o resultado quase
 * sempre vai parar num grupo de mensagens. Sem o link, quem recebe não tem como
 * chegar ao app; com ele, cada sorteio compartilhado vira um convite.
 */
private fun Context.shareText(text: String) {
    val body = getString(
        R.string.share_body,
        text,
        getString(R.string.play_store_url, packageName),
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, body)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
}

