package com.cisco.quizapp.ui.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cisco.quizapp.data.local.entity.Question
import com.cisco.quizapp.data.local.entity.Topic
import com.cisco.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: QuizRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val topics: StateFlow<List<Topic>> = repository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getQuestionsForTopic(topicId: Long): Flow<List<Question>> =
        repository.getQuestionsByTopic(topicId)

    suspend fun getQuestionById(id: Long): Question? = repository.getQuestionById(id)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() { _message.value = null }

    // ── Topics ──────────────────────────────────────────────────────────────────

    fun saveTopic(topic: Topic) = viewModelScope.launch {
        if (topic.id == 0L) repository.insertTopic(topic) else repository.updateTopic(topic)
    }

    fun deleteTopic(topic: Topic) = viewModelScope.launch { repository.deleteTopic(topic) }

    // ── Questions ────────────────────────────────────────────────────────────────

    fun saveQuestion(question: Question) = viewModelScope.launch {
        if (question.id == 0L) repository.insertQuestion(question)
        else repository.updateQuestion(question)
    }

    fun deleteQuestion(question: Question) = viewModelScope.launch {
        repository.deleteQuestion(question)
    }

    // ── Export ──────────────────────────────────────────────────────────────────

    fun exportToJson() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val allTopics = repository.getAllTopics().first()
            val allQuestions = repository.getAllQuestions().first()

            val topicsArr = JSONArray()
            allTopics.forEach { t ->
                topicsArr.put(JSONObject().apply {
                    put("id", t.id)
                    put("name", t.name)
                    put("description", t.description)
                    put("iconName", t.iconName)
                    put("difficultyLevel", t.difficultyLevel)
                })
            }

            val questionsArr = JSONArray()
            allQuestions.forEach { q ->
                questionsArr.put(JSONObject().apply {
                    put("id", q.id)
                    put("topicId", q.topicId)
                    put("questionText", q.questionText)
                    put("optionA", q.optionA)
                    put("optionB", q.optionB)
                    put("optionC", q.optionC)
                    put("optionD", q.optionD)
                    put("correctAnswer", q.correctAnswer)
                    put("explanation", q.explanation)
                    put("difficulty", q.difficulty)
                })
            }

            val root = JSONObject().apply {
                put("topics", topicsArr)
                put("questions", questionsArr)
            }

            val file = File(context.getExternalFilesDir(null), "cisco_quiz_export.json")
            file.writeText(root.toString(2))
            _message.value = "Exported ${allTopics.size} topics, ${allQuestions.size} questions\n${file.absolutePath}"
        } catch (e: IOException) {
            _message.value = "Export failed: could not write file (${e.message})"
        } catch (e: JSONException) {
            _message.value = "Export failed: could not serialize data (${e.message})"
        }
    }

    // ── Import ──────────────────────────────────────────────────────────────────

    fun importFromUri(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val content = context.contentResolver.openInputStream(uri)
                ?.bufferedReader()?.readText()
                ?: error("Cannot read selected file")

            val root = JSONObject(content)

            val topicsArr = root.getJSONArray("topics")
            val topics = (0 until topicsArr.length()).map { i ->
                val o = topicsArr.getJSONObject(i)
                Topic(
                    id = o.getLong("id"),
                    name = o.getString("name"),
                    description = o.getString("description"),
                    iconName = o.getString("iconName"),
                    difficultyLevel = o.getInt("difficultyLevel")
                )
            }

            val questionsArr = root.getJSONArray("questions")
            val questions = (0 until questionsArr.length()).map { i ->
                val o = questionsArr.getJSONObject(i)
                Question(
                    id = o.getLong("id"),
                    topicId = o.getLong("topicId"),
                    questionText = o.getString("questionText"),
                    optionA = o.getString("optionA"),
                    optionB = o.getString("optionB"),
                    optionC = o.getString("optionC"),
                    optionD = o.getString("optionD"),
                    correctAnswer = o.getString("correctAnswer"),
                    explanation = o.getString("explanation"),
                    difficulty = o.getInt("difficulty")
                )
            }

            repository.insertTopics(topics)
            repository.insertQuestions(questions)
            _message.value = "Imported ${topics.size} topics, ${questions.size} questions"
        } catch (e: IOException) {
            _message.value = "Import failed: could not read file (${e.message})"
        } catch (e: JSONException) {
            _message.value = "Import failed: invalid JSON format (${e.message})"
        } catch (e: IllegalStateException) {
            _message.value = "Import failed: ${e.message}"
        }
    }
}
