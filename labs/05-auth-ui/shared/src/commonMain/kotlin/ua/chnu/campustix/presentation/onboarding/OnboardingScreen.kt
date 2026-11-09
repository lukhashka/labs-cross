package ua.chnu.campustix.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
            OnboardingEvent.NavigateToAuth -> onFinished()
        }
    }

    OnboardingContent(
        state = state,
        pagerState = pagerState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun OnboardingContent(
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
                TextButton(
                    onClick = { onAction(OnboardingAction.OnSkipClick) },
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Text(stringResource(Res.string.onboarding_skip))
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { index ->
            OnboardingPageContent(
                page = state.pages[index],
                // fade and shrink neighbour pages while swiping
                modifier = Modifier.graphicsLayer {
                    val offset = ((pagerState.currentPage - index) + pagerState.currentPageOffsetFraction)
                        .absoluteValue.coerceIn(0f, 1f)
                    alpha = lerp(0.4f, 1f, 1f - offset)
                    val scale = lerp(0.85f, 1f, 1f - offset)
                    scaleX = scale
                    scaleY = scale
                },
            )
        }

        PagerIndicator(
            count = state.pages.size,
            current = state.currentPage,
            modifier = Modifier.padding(vertical = 24.dp),
        )

        Button(
            onClick = { onAction(OnboardingAction.OnNextClick) },
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(
                stringResource(if (state.isLastPage) Res.string.onboarding_start else Res.string.onboarding_next)
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage, modifier: Modifier = Modifier) {
    val content = page.toUi()
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(content.image),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp),
            )
        }
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(content.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(content.text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 420.dp),
        )
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
            val width by animateDpAsState(if (selected) 28.dp else 10.dp)
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
