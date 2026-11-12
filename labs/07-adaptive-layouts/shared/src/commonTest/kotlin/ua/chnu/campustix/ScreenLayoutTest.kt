package ua.chnu.campustix

import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import ua.chnu.campustix.presentation.ui.ScreenLayout
import ua.chnu.campustix.presentation.ui.toScreenLayout
import kotlin.test.Test
import kotlin.test.assertEquals

class ScreenLayoutTest {

    // The same breakpoints Material uses: 600 dp and 840 dp
    private fun layoutFor(widthDp: Float, heightDp: Float): ScreenLayout =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp, heightDp).toScreenLayout()

    @Test
    fun phonePortraitUsesOnePane() {
        assertEquals(ScreenLayout.SinglePane, layoutFor(411f, 891f))
    }

    @Test
    fun phoneLandscapeUsesTwoPanes() {
        assertEquals(ScreenLayout.TwoPane, layoutFor(891f, 411f))
    }

    @Test
    fun tabletAndDesktopUseTwoPanes() {
        assertEquals(ScreenLayout.TwoPane, layoutFor(800f, 1280f))
        assertEquals(ScreenLayout.TwoPane, layoutFor(1280f, 800f))
    }

    @Test
    fun breakpointIs600dp() {
        assertEquals(ScreenLayout.SinglePane, layoutFor(599f, 800f))
        assertEquals(ScreenLayout.TwoPane, layoutFor(600f, 800f))
    }
}
