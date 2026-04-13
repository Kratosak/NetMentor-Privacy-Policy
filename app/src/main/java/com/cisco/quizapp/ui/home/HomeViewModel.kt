package com.cisco.quizapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cisco.quizapp.data.local.entity.Topic
import com.cisco.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TopicWithCount(
    val topic: Topic,
    val questionCount: Int
)

data class HomeUiState(
    val topics: List<TopicWithCount> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTopics()
    }

    private fun loadTopics() {
        viewModelScope.launch {
            repository.getAllTopics().collect { topics ->
                val topicsWithCounts = topics.map { topic ->
                    TopicWithCount(
                        topic = topic,
                        questionCount = repository.getQuestionCountForTopic(topic.id)
                    )
                }
                _uiState.value = HomeUiState(
                    topics = topicsWithCounts,
                    isLoading = false
                )
            }
        }
    }
}
