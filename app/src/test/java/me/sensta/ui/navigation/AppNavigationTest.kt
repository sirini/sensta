package me.sensta.ui.navigation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNavigationTest {
    @Test
    fun `bottom navigation is hidden only on home and before route resolution`() {
        assertFalse(shouldShowBottomNavigation(null))
        assertFalse(shouldShowBottomNavigation(Screen.Home.route))
        assertTrue(shouldShowBottomNavigation(Screen.Explorer.route))
        assertTrue(shouldShowBottomNavigation(Screen.Profile.route))
    }

    @Test
    fun `successful login resumes upload only when upload opened login`() {
        assertTrue(
            shouldResumeUploadAfterLogin(
                hasCompleteSession = true,
                currentRoute = Screen.Login.route,
                previousRoute = Screen.Upload.route
            )
        )
        assertFalse(
            shouldResumeUploadAfterLogin(
                hasCompleteSession = false,
                currentRoute = Screen.Login.route,
                previousRoute = Screen.Upload.route
            )
        )
        assertFalse(
            shouldResumeUploadAfterLogin(
                hasCompleteSession = true,
                currentRoute = Screen.Login.route,
                previousRoute = Screen.Home.route
            )
        )
    }
}
