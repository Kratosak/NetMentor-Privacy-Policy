package com.cisco.quizapp.ui.quiz

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cisco.quizapp.ui.theme.CiscoGold
import com.cisco.quizapp.ui.theme.CiscoGreen
import com.cisco.quizapp.ui.theme.CiscoRed
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

// Test interstitial ad unit ID.
// TODO: Replace with your production ad unit ID before release:
//   ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX
private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-5522114715270354/2650419534"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateToResult: (score: Int, total: Int, missedIds: String) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    // Load the interstitial in the background as soon as the quiz screen opens.
    LaunchedEffect(Unit) {
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    // When the quiz finishes, show the interstitial (if ready) then navigate.
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            val missedIds = state.missedQuestionIds.joinToString(",").ifEmpty { "none" }
            val activity = context as? Activity
            val ad = interstitialAd
            if (ad != null && activity != null) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        onNavigateToResult(state.score, state.totalQuestions, missedIds)
                    }
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        onNavigateToResult(state.score, state.totalQuestions, missedIds)
                    }
                }
                ad.show(activity)
            } else {
                onNavigateToResult(state.score, state.totalQuestions, missedIds)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Question ${state.currentIndex + 1} of ${state.totalQuestions}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { state.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                },
                actions = {
                    TimerBadge(seconds = state.timeLeftSeconds)
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (state.isLoading || state.currentQuestion == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val question = state.currentQuestion!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Score tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            "Score: ${state.score}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Question card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                // Answer buttons
                listOf(
                    "A" to question.optionA,
                    "B" to question.optionB,
                    "C" to question.optionC,
                    "D" to question.optionD,
                ).forEach { (letter, text) ->
                    AnswerButton(
                        letter = letter,
                        text = text,
                        selectedAnswer = state.selectedAnswer,
                        correctAnswer = question.correctAnswer,
                        isAnswered = state.isAnswered,
                        onSelect = { viewModel.onAnswerSelected(letter) }
                    )
                }

                // Explanation + Next button (shown after answering)
                if (state.isAnswered) {
                    ExplanationCard(
                        isCorrect = state.selectedAnswer == question.correctAnswer,
                        explanation = question.explanation,
                        correctAnswer = question.correctAnswer,
                        wasTimedOut = state.selectedAnswer == null
                    )

                    Button(
                        onClick = viewModel::onNextQuestion,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            if (state.currentIndex + 1 < state.totalQuestions) "Next Question"
                            else "See Results",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AnswerButton(
    letter: String,
    text: String,
    selectedAnswer: String?,
    correctAnswer: String,
    isAnswered: Boolean,
    onSelect: () -> Unit
) {
    val targetBg = when {
        !isAnswered -> MaterialTheme.colorScheme.surface
        letter == correctAnswer -> CiscoGreen.copy(alpha = 0.22f)
        letter == selectedAnswer -> CiscoRed.copy(alpha = 0.22f)
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    }
    val targetBorder = when {
        !isAnswered -> MaterialTheme.colorScheme.outline
        letter == correctAnswer -> CiscoGreen
        letter == selectedAnswer -> CiscoRed
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    val contentColor = when {
        !isAnswered -> MaterialTheme.colorScheme.onSurface
        letter == correctAnswer -> CiscoGreen
        letter == selectedAnswer -> CiscoRed
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }
    val badgeBg = when {
        !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
        letter == correctAnswer -> CiscoGreen.copy(alpha = 0.3f)
        letter == selectedAnswer -> CiscoRed.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    val animatedBg by animateColorAsState(targetBg, tween(300), label = "bg")
    val animatedBorder by animateColorAsState(targetBorder, tween(300), label = "border")

    OutlinedButton(
        onClick = onSelect,
        enabled = !isAnswered,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, animatedBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = animatedBg,
            contentColor = contentColor,
            disabledContainerColor = animatedBg,
            disabledContentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeBg,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = letter,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}

@Composable
private fun ExplanationCard(
    isCorrect: Boolean,
    explanation: String,
    correctAnswer: String,
    wasTimedOut: Boolean
) {
    val (bgColor, textColor, label) = when {
        wasTimedOut -> Triple(CiscoGold.copy(alpha = 0.15f), CiscoGold, "Time's Up!")
        isCorrect -> Triple(CiscoGreen.copy(alpha = 0.15f), CiscoGreen, "Correct!")
        else -> Triple(CiscoRed.copy(alpha = 0.15f), CiscoRed, "Incorrect")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    label,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    style = MaterialTheme.typography.titleSmall
                )
                if (!isCorrect || wasTimedOut) {
                    Text(
                        " — Correct: $correctAnswer",
                        color = CiscoGreen,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TimerBadge(seconds: Int) {
    val color = when {
        seconds > 15 -> CiscoGreen
        seconds > 8 -> CiscoGold
        else -> CiscoRed
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            "\u23F1 ${seconds}s",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
