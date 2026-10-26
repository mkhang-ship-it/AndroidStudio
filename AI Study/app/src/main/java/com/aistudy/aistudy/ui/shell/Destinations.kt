package com.aistudy.aistudy.ui.shell

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.aistudy.aistudy.R

data class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val outlinedIcon: ImageVector
)

/**
 * Route la hang so - khong viet chuoi tho trong `composable()`.
 *
 * Nhom 5 route top-level map 1-1 voi 5 nhom yeu cau chuc nang trong de bai
 * (xem docs/PHASE_1_STEP_BY_STEP_CODE.md Step 6). Cac route con la route
 * trong luoi: chay duoc khi da co session + da onboarding.
 */
object Routes {
    const val ARG_QUESTION_ID = "questionId"
    const val ARG_NOTIFICATION_ID = "notificationId"

    // Chua co session - AuthGate chuyen toi day truoc moi thu con lai
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Co session nhung chua onboarding (Profile.onboardedAt == null)
    const val ONBOARDING = "onboarding"

    // Da onboarding - duoc vao MainShell
    const val ASK = "ask"
    const val HISTORY = "history"
    const val PRACTICE = "practice"
    const val PROGRESS = "progress"
    const val PROFILE = "profile"

    // Route con, khong co tab
    const val NOTIFICATIONS = "notifications"
    const val NOTIFICATION_DETAIL = "notification/{$ARG_NOTIFICATION_ID}"
    const val QUESTION_DETAIL = "question/{$ARG_QUESTION_ID}"

    fun questionDetail(id: Long): String = "question/$id"

    fun notificationDetail(id: Long): String = "notification/$id"
}

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        Routes.ASK,
        R.string.nav_ask,
        Icons.Filled.Search,
        Icons.Outlined.Search
    ),
    TopLevelDestination(
        Routes.HISTORY,
        R.string.nav_history,
        Icons.AutoMirrored.Filled.List,
        Icons.AutoMirrored.Outlined.List
    ),
    TopLevelDestination(
        Routes.PRACTICE,
        R.string.nav_practice,
        Icons.Filled.Edit,
        Icons.Outlined.Edit
    ),
    TopLevelDestination(
        Routes.PROGRESS,
        R.string.nav_progress,
        Icons.Filled.Star,
        Icons.Outlined.Star
    ),
    TopLevelDestination(
        Routes.PROFILE,
        R.string.nav_profile,
        Icons.Filled.Person,
        Icons.Outlined.Person
    )
)

/**
 * Dieu huong tab theo dung mau bottom navigation.
 *
 * Hai loi Navigation Compose can chan:
 * 1. Route con (Thong bao / Chi tiet cau hoi) dang phu len tab: phai dong no
 *    truoc, neu khong `saveState` se luu ca route con vao tab va lan sau bam
 *    tab se khoi phuc nham man hinh.
 * 2. Tab dich da nam trong back stack: pop ve no thay vi `navigate` lai, vi
 *    `restoreState` co the khoi phuc ca cac route con da push len tab do.
 */
internal fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    val topLevelRoutes = TOP_LEVEL_DESTINATIONS.map { it.route }.toSet()
    while (currentDestination?.route !in topLevelRoutes) {
        if (!popBackStack()) break
    }

    if (popBackStack(destination.route, inclusive = false)) return

    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
