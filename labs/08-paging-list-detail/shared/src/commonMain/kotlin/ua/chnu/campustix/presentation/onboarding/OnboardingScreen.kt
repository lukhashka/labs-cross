package ua.chnu.campustix.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.campustix.domain.onboarding.OnboardingPage
import ua.chnu.campustix.presentation.ui.ObserveEvents
import ua.chnu.campustix.presentation.ui.ScreenLayout
import ua.chnu.campustix.presentation.ui.currentScreenLayout
import ua.chnu.campustix.resources.Res
import ua.chnu.campustix.resources.ic_onboarding_events
import ua.chnu.campustix.resources.ic_onboarding_qr
import ua.chnu.campustix.resources.ic_onboarding_reviews
import ua.chnu.campustix.resources.ic_onboarding_ticket
import ua.chnu.campustix.resources.onboarding_events_text
import ua.chnu.campustix.resources.onboarding_events_title
import ua.chnu.campustix.resources.onboarding_next
import ua.chnu.campustix.resources.onboarding_page_indicator
import ua.chnu.campustix.resources.onboarding_qr_text
import ua.chnu.campustix.resources.onboarding_qr_title
import ua.chnu.campustix.resources.onboarding_reviews_text
import ua.chnu.campustix.resources.onboarding_reviews_title
import ua.chnu.campustix.resources.onboarding_skip
import ua.chnu.campustix.resources.onboarding_start
import ua.chnu.campustix.resources.onboarding_ticket_text
import ua.chnu.campustix.resources.onboarding_ticket_title
import kotlin.math.absoluteValue

private val SinglePaneIllustrationSize = 180.dp
private val TwoPaneIllustrationSize = 300.dp
private val TextMaxWidth = 440.dp

@Composable
fun OnboardingScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // PagerState is created above the layout switch, so the current page survives
    // a change from one pane to two panes (rotation, window resize)
    val pagerState = rememberPagerState(pageCount = { state.pages.size })
    val scope = rememberCoroutineScope()

    // Swipe by finger -> tell the ViewModel which page is shown
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onAction(OnboardingAction.OnPageChanged(page))
        }
    }

    ObserveEvents(viewModel.events) { event ->
        when (event) {
            is OnboardingEvent.ScrollToPage -> scope.launch {
                pagerState.animateScrollToPage(event.page)
            }
            OnboardingEvent.NavigateToAuth -> onNavigateToAuth()
            OnboardingEvent.NavigateToHome -> onNavigateToHome()
        }
    }

    when (currentScreenLayout()) {
        ScreenLayout.SinglePane -> OnboardingSinglePane(state, pagerState, viewModel::onAction)
        ScreenLayout.TwoPane -> OnboardingTwoPane(state, pagerState, viewModel::onAction)
    }
}

// Phone in portrait: Skip on top, the whole page swipes, indicator and button at the bottom
@Composable
private fun OnboardingSinglePane(
    state: OnboardingState,
    pagerState: PagerState,
    onAction: (OnboardingAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 8.dp)) {
            if (!state.isLastPage) {
                SkipButton(onAction, Modifier.align(Alignment.CenterEnd))
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { index ->
            val content = state.pages[index].toUi()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .graphicsLayer { applyPageOffset(pagerState.offsetOf(index)) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Illustration(content, Modifier.size(SinglePaneIllustrationSize))
                Spacer(Modifier.height(40.dp))
                PageTexts(content, TextAlign.Center)
            }
        }

        PagerIndicator(
            count = state.pages.size,
            current = state.currentPage,
            modifier = Modifier.padding(vertical = 24.dp),
        )

        NextButton(
            isLastPage = state.isLastPage,
            onAction = onAction,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
        )
    }
}

// Wide window: illustration pager on the left, texts and buttons on the right
@Composable
private fun OnboardingTwoPane(
    state: OnboardingState,
    pagerState: PagerState,
    onAction: (OnboardingAction) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left pane: colored background goes under the status bar, content does not
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical)),
        ) {
            // Only the illustration swipes here, the texts on the right follow the current page
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Illustration(
                        content = state.pages[index].toUi(),
                        modifier = Modifier
                            .sizeIn(maxWidth = TwoPaneIllustrationSize, maxHeight = TwoPaneIllustrationSize)
                            .aspectRatio(1f)
                            .graphicsLayer { applyPageOffset(pagerState.offsetOf(index)) },
                        background = MaterialTheme.colorScheme.background,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End + WindowInsetsSides.Vertical))
                .verticalScroll(rememberScrollState())
                .padding(40.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        ) {
            // Text changes with a fade when the page changes
            AnimatedContent(
                targetState = state.pages[state.currentPage],
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                contentAlignment = Alignment.CenterStart,
            ) { page ->
                PageTexts(page.toUi(), TextAlign.Start)
            }
            PagerIndicator(count = state.pages.size, current = state.currentPage)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NextButton(state.isLastPage, onAction, Modifier.widthIn(min = 180.dp))
                if (!state.isLastPage) {
                    SkipButton(onAction)
                }
            }
        }
    }
}

@Composable
private fun Illustration(
    content: OnboardingPageUi,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Box(
        modifier = modifier.clip(CircleShape).background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(content.image),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxSize(0.53f),
        )
    }
}

@Composable
private fun PageTexts(content: OnboardingPageUi, textAlign: TextAlign) {
    val alignment = if (textAlign == TextAlign.Center) Alignment.CenterHorizontally else Alignment.Start
    Column(
        modifier = Modifier.widthIn(max = TextMaxWidth),
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(content.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = textAlign,
        )
        Text(
            text = stringResource(content.text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = textAlign,
        )
    }
}

@Composable
private fun NextButton(isLastPage: Boolean, onAction: (OnboardingAction) -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = { onAction(OnboardingAction.OnNextClick) },
        modifier = modifier.height(52.dp),
    ) {
        Text(stringResource(if (isLastPage) Res.string.onboarding_start else Res.string.onboarding_next))
    }
}

@Composable
private fun SkipButton(onAction: (OnboardingAction) -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = { onAction(OnboardingAction.OnSkipClick) }, modifier = modifier) {
        Text(stringResource(Res.string.onboarding_skip))
    }
}

@Composable
private fun PagerIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.onboarding_page_indicator, current + 1, count)
    Row(
        modifier = modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val selected = index == current
            val width: Dp by animateDpAsState(if (selected) 28.dp else 10.dp)
            Box(
                modifier = Modifier
                    .size(width = width, height = 10.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}

// 0 = the page is in the center, 1 = the page is a whole screen away
private fun PagerState.offsetOf(page: Int): Float =
    ((currentPage - page) + currentPageOffsetFraction).absoluteValue.coerceIn(0f, 1f)

// fade and shrink neighbour pages while swiping
private fun GraphicsLayerScope.applyPageOffset(offset: Float) {
    alpha = lerp(0.4f, 1f, 1f - offset)
    val scale = lerp(0.85f, 1f, 1f - offset)
    scaleX = scale
    scaleY = scale
}

private class OnboardingPageUi(
    val image: DrawableResource,
    val title: StringResource,
    val text: StringResource,
)

private fun OnboardingPage.toUi(): OnboardingPageUi = when (this) {
    OnboardingPage.EVENTS -> OnboardingPageUi(
        Res.drawable.ic_onboarding_events, Res.string.onboarding_events_title, Res.string.onboarding_events_text
    )
    OnboardingPage.TICKET -> OnboardingPageUi(
        Res.drawable.ic_onboarding_ticket, Res.string.onboarding_ticket_title, Res.string.onboarding_ticket_text
    )
    OnboardingPage.QR_CODE -> OnboardingPageUi(
        Res.drawable.ic_onboarding_qr, Res.string.onboarding_qr_title, Res.string.onboarding_qr_text
    )
    OnboardingPage.REVIEWS -> OnboardingPageUi(
        Res.drawable.ic_onboarding_reviews, Res.string.onboarding_reviews_title, Res.string.onboarding_reviews_text
    )
}
