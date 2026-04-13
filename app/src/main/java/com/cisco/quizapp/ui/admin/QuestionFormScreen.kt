package com.cisco.quizapp.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cisco.quizapp.data.local.entity.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionFormScreen(
    topicId: Long,
    questionId: Long,   // -1L = new question
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("A") }
    var difficulty by remember { mutableStateOf(1) }
    var explanation by remember { mutableStateOf("") }
    var difficultyExpanded by remember { mutableStateOf(false) }

    val isEditing = questionId != -1L

    LaunchedEffect(questionId) {
        if (isEditing) {
            val q = viewModel.getQuestionById(questionId) ?: return@LaunchedEffect
            questionText = q.questionText
            optionA = q.optionA
            optionB = q.optionB
            optionC = q.optionC
            optionD = q.optionD
            correctAnswer = q.correctAnswer
            difficulty = q.difficulty
            explanation = q.explanation
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Question" else "Add Question") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Question Text") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            OutlinedTextField(
                value = optionA,
                onValueChange = { optionA = it },
                label = { Text("Option A") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = optionB,
                onValueChange = { optionB = it },
                label = { Text("Option B") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = optionC,
                onValueChange = { optionC = it },
                label = { Text("Option C") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = optionD,
                onValueChange = { optionD = it },
                label = { Text("Option D") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                Text(
                    "Correct Answer",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("A", "B", "C", "D").forEach { letter ->
                        FilterChip(
                            selected = correctAnswer == letter,
                            onClick = { correctAnswer = letter },
                            label = { Text(letter) }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = difficultyExpanded,
                onExpandedChange = { difficultyExpanded = it }
            ) {
                OutlinedTextField(
                    value = when (difficulty) { 1 -> "Easy"; 2 -> "Medium"; else -> "Hard" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Difficulty") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = difficultyExpanded,
                    onDismissRequest = { difficultyExpanded = false }
                ) {
                    listOf(1 to "Easy", 2 to "Medium", 3 to "Hard").forEach { (level, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                difficulty = level
                                difficultyExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = explanation,
                onValueChange = { explanation = it },
                label = { Text("Explanation") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 6
            )

            val isValid = questionText.isNotBlank() && optionA.isNotBlank() &&
                    optionB.isNotBlank() && optionC.isNotBlank() && optionD.isNotBlank()

            Button(
                onClick = {
                    if (isValid) {
                        viewModel.saveQuestion(
                            Question(
                                id = if (isEditing) questionId else 0L,
                                topicId = topicId,
                                questionText = questionText.trim(),
                                optionA = optionA.trim(),
                                optionB = optionB.trim(),
                                optionC = optionC.trim(),
                                optionD = optionD.trim(),
                                correctAnswer = correctAnswer,
                                explanation = explanation.trim(),
                                difficulty = difficulty
                            )
                        )
                        onBack()
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Save Question")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
