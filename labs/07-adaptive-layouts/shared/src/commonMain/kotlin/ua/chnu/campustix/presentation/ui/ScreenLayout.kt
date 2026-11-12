package ua.chnu.campustix.presentation.ui

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

// How a screen arranges its content
enum class ScreenLayout {
    // phone in portrait: one column
    SinglePane,
    // phone in landscape, tablet, desktop, browser: two panes side by side
    TwoPane,
}

// Width >= 600 dp (Medium or Expanded) is enough room for two panes
fun WindowSizeClass.toScreenLayout(): ScreenLayout =
    if (isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        ScreenLayout.TwoPane
    } else {
        ScreenLayout.SinglePane
    }

// Recomposes when the window size class changes: rotation, window resize, split screen
@Composable
fun currentScreenLayout(): ScreenLayout =
    currentWindowAdaptiveInfoV2().windowSizeClass.toScreenLayout()

// Height >= 480 dp: false for a phone in landscape, where extra decoration does not fit
@Composable
fun isWindowHeightAtLeastMedium(): Boolean =
    currentWindowAdaptiveInfoV2().windowSizeClass
        .isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
