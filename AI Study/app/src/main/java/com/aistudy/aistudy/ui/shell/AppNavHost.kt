package com.aistudy.aistudy.ui.shell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aistudy.aistudy.ui.auth.LoginScreen
import com.aistudy.aistudy.ui.auth.RegisterScreen
import com.aistudy.aistudy.ui.auth.WelcomeScreen
import com.aistudy.aistudy.ui.notifications.NotificationDetailScreen
import com.aistudy.aistudy.ui.notifications.NotificationsScreen
import com.aistudy.aistudy.ui.onboarding.OnboardingScreen
import com.aistudy.aistudy.ui.questiondetail.QuestionDetailScreen
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.screens.ask.AskScreen
import com.aistudy.aistudy.ui.screens.history.HistoryScreen
import com.aistudy.aistudy.ui.screens.practice.PracticeScreen
import com.aistudy.aistudy.ui.screens.profile.ProfileScreen
import com.aistudy.aistudy.ui.screens.progress.ProgressScreen

/**
 * Moi man hinh nhan [AppDependencies] + callback dieu huong, va TO TU tao
 * ViewModel cua no. `AppNavHost` khong biet constructor cua ViewModel nao - do
 * la viec cua tung man hinh.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    deps: AppDependencies,
    startDestination: String = Routes.ASK,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                deps = deps,
                onSignIn = { navController.navigate(Routes.LOGIN) },
                onSignUp = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                deps = deps,
                onBack = navController::popBackStack,
                onSignedIn = { navController.popBackStack(Routes.WELCOME, inclusive = true) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                deps = deps,
                onBack = navController::popBackStack,
                onRegistered = { navController.popBackStack(Routes.WELCOME, inclusive = true) }
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                deps = deps,
                onBack = navController::popBackStack,
                editMode = false
            )
        }
        composable(Routes.ASK) {
            AskScreen(deps = deps)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                deps = deps,
                onOpenQuestion = { navController.navigate(Routes.questionDetail(it)) }
            )
        }
        composable(Routes.PRACTICE) {
            PracticeScreen(deps = deps)
        }
        composable(Routes.PROGRESS) {
            ProgressScreen(deps = deps)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                deps = deps,
                onEditPreferences = { navController.navigate(Routes.ONBOARDING) },
                onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
            )
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                deps = deps,
                onBack = navController::popBackStack,
                onOpenNotification = { id ->
                    navController.navigate(Routes.notificationDetail(id))
                }
            )
        }
        composable(
            route = Routes.NOTIFICATION_DETAIL,
            arguments = listOf(
                navArgument(Routes.ARG_NOTIFICATION_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            NotificationDetailScreen(
                deps = deps,
                notificationId = backStackEntry.arguments?.getLong(Routes.ARG_NOTIFICATION_ID) ?: 0L,
                onBack = navController::popBackStack,
                onOpenDeepLink = { deepLink ->
                    runCatching {
                        navController.navigate(deepLink) { launchSingleTop = true }
                    }.onFailure { navController.popBackStack() }
                }
            )
        }
        composable(
            route = Routes.QUESTION_DETAIL,
            arguments = listOf(
                navArgument(Routes.ARG_QUESTION_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            QuestionDetailScreen(
                deps = deps,
                questionId = backStackEntry.arguments?.getLong(Routes.ARG_QUESTION_ID) ?: 0L,
                onBack = navController::popBackStack
            )
        }
    }
}