package com.cisco.quizapp.ui.result

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

enum class GradeLabel(val label: String) {
    BEGINNER("Try harder! Focus, think, do it!"),
    INTERMEDIATE("You know more than you think. Prove it — retry."),
    EXPERT("Basics nailed. Time to level up.")
}

data class ResultUiState(
    val score: Int = 0,
    val total: Int = 0,
    val missedQuestions: List<Question> = emptyList(),
    val isLoading: Boolean = true
) {
    val percentage: Int get() = if (total == 0) 0 else score * 100 / total
    val grade: GradeLabel
        get() = when {
            percentage >= 80 -> GradeLabel.EXPERT
            percentage >= 50 -> GradeLabel.INTERMEDIATE
            else -> GradeLabel.BEGINNER
        }
}

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val ARG_SCORE = "score"
        const val ARG_TOTAL = "total"
        const val ARG_MISSED_IDS = "missedIds"
    }

    private val score: Int = checkNotNull(savedStateHandle[ARG_SCORE])
    private val total: Int = checkNotNull(savedStateHandle[ARG_TOTAL])
    private val missedIds: String = savedStateHandle[ARG_MISSED_IDS] ?: "none"

    private val _uiState = MutableStateFlow(ResultUiState(score = score, total = total))
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        loadMissedQuestions()
    }

    private fun loadMissedQuestions() {
        viewModelScope.launch {
            val ids = missedIds.split(",").mapNotNull { it.toLongOrNull() }
            val missed = ids.mapNotNull { repository.getQuestionById(it) }
            _uiState.value = _uiState.value.copy(
                missedQuestions = missed,
                isLoading = false
            )
        }
    }
}
