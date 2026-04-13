package com.cisco.quizapp.ui.quiz

import android.os.CountDownTimer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cisco.quizapp.data.local.entity.Question
import com.cisco.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswered: Boolean = false,
    val score: Int = 0,
    val timeLeftSeconds: Int = QuizViewModel.QUESTION_TIME_SECONDS,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val missedQuestionIds: List<Long> = emptyList()
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int get() = questions.size
    val progressFraction: Float
        get() = if (totalQuestions == 0) 0f
                else (currentIndex + 1).toFloat() / totalQuestions.toFloat()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val QUESTION_TIME_SECONDS = 30
        const val ARG_TOPIC_ID = "topicId"
        const val ARG_QUESTION_COUNT = "questionCount"
    }

    private val topicId: Long = checkNotNull(savedStateHandle[ARG_TOPIC_ID])
    private val questionCount: Int = checkNotNull(savedStateHandle[ARG_QUESTION_COUNT])

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val questions = repository.getRandomQuestionsForTopic(topicId, questionCount)
            _uiState.value = _uiState.value.copy(
                questions = questions,
                isLoading = false
            )
            startTimer()
        }
    }

    fun onAnswerSelected(answer: String) {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        if (state.isAnswered) return

        countDownTimer?.cancel()
        val isCorrect = answer == question.correctAnswer
        val newScore = if (isCorrect) state.score + 1 else state.score
        val missedIds = if (!isCorrect) state.missedQuestionIds + question.id
                        else state.missedQuestionIds

        _uiState.value = state.copy(
            selectedAnswer = answer,
            isAnswered = true,
            score = newScore,
            missedQuestionIds = missedIds
        )
    }

    fun onNextQuestion() {
        val state = _uiState.value
        if (!state.isAnswered) return

        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.totalQuestions) {
            _uiState.value = state.copy(isFinished = true)
        } else {
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                selectedAnswer = null,
                isAnswered = false,
                timeLeftSeconds = QUESTION_TIME_SECONDS
            )
            startTimer()
        }
    }

    private fun onTimeExpired() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        if (state.isAnswered) return
        val missedIds = state.missedQuestionIds + question.id
        _uiState.value = state.copy(
            selectedAnswer = null,
            isAnswered = true,
            timeLeftSeconds = 0,
            missedQuestionIds = missedIds
        )
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(QUESTION_TIME_SECONDS * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(
                    timeLeftSeconds = (millisUntilFinished / 1000).toInt()
                )
            }

            override fun onFinish() {
                onTimeExpired()
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
