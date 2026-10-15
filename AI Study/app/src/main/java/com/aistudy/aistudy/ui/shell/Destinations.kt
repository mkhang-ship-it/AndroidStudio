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
    const val QUESTION_DETAIL = "question/{$ARG_QUESTION_ID}"

    fun questionDetail(id: Long): String = "question/$id"
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
