package com.cisco.quizapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cisco.quizapp.ui.admin.AdminHomeScreen
import com.cisco.quizapp.ui.admin.AdminQuestionsScreen
import com.cisco.quizapp.ui.admin.QuestionFormScreen
import com.cisco.quizapp.ui.home.HomeScreen
import com.cisco.quizapp.ui.quiz.QuizScreen
import com.cisco.quizapp.ui.result.ResultScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Quiz : Screen("quiz/{topicId}/{questionCount}") {
        fun createRoute(topicId: Long, questionCount: Int) = "quiz/$topicId/$questionCount"
    }

    // topicId + questionCount are included so Retry can restart the same quiz
    object Result : Screen("result/{topicId}/{questionCount}/{score}/{total}/{missedIds}") {
        fun createRoute(
            topicId: Long,
            questionCount: Int,
            score: Int,
            total: Int,
            missedIds: String
        ) = "result/$topicId/$questionCount/$score/$total/$missedIds"
    }

    object Admin : Screen("admin")

    object AdminQuestions : Screen("admin/questions/{topicId}") {
        fun createRoute(topicId: Long) = "admin/questions/$topicId"
    }

    // questionId = -1L means new question
    object AdminQuestionForm : Screen("admin/question_form/{topicId}/{questionId}") {
        fun createRoute(topicId: Long, questionId: Long) = "admin/question_form/$topicId/$questionId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartQuiz = { topicId, questionCount ->
                    navController.navigate(Screen.Quiz.createRoute(topicId, questionCount))
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
                }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.LongType },
                navArgument("questionCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            val questionCount = backStackEntry.arguments?.getInt("questionCount") ?: 10

            QuizScreen(
                onNavigateToResult = { score, total, missedIds ->
                    navController.navigate(
                        Screen.Result.createRoute(topicId, questionCount, score, total, missedIds)
                    ) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.LongType },
                navArgument("questionCount") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("missedIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            val questionCount = backStackEntry.arguments?.getInt("questionCount") ?: 10

            ResultScreen(
                onRetry = {
                    navController.navigate(Screen.Quiz.createRoute(topicId, questionCount)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Admin ──────────────────────────────────────────────────────────────

        composable(Screen.Admin.route) {
            AdminHomeScreen(
                onNavigateToQuestions = { topicId ->
                    navController.navigate(Screen.AdminQuestions.createRoute(topicId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AdminQuestions.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            AdminQuestionsScreen(
                topicId = topicId,
                onNavigateToForm = { tid, questionId ->
                    navController.navigate(Screen.AdminQuestionForm.createRoute(tid, questionId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AdminQuestionForm.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.LongType },
                navArgument("questionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            val questionId = backStackEntry.arguments?.getLong("questionId") ?: -1L
            QuestionFormScreen(
                topicId = topicId,
                questionId = questionId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
