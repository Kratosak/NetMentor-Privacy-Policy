package com.cisco.quizapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cisco.quizapp.BuildConfig
import com.cisco.quizapp.data.local.entity.Topic
import com.cisco.quizapp.ui.ads.BannerAd
import com.cisco.quizapp.ui.theme.CiscoGold
import com.cisco.quizapp.ui.theme.CiscoGreen
import com.cisco.quizapp.ui.theme.CiscoRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartQuiz: (topicId: Long, questionCount: Int) -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTopic by remember { mutableStateOf<TopicWithCount?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    // Secret admin trigger: 5 taps on version text within 3 s between taps
    var adminTapCount by remember { mutableStateOf(0) }
    var adminLastTapMs by remember { mutableStateOf(0L) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "NetMentor Quiz",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Networking Mastery",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = { BannerAd() },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "Choose a Topic",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(uiState.topics) { topicWithCount ->
                    TopicCard(
                        topicWithCount = topicWithCount,
                        onStartClick = {
                            selectedTopic = topicWithCount
                            showSheet = true
                        }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "v${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember {
                                        androidx.compose.foundation.interaction.MutableInteractionSource()
                                    }
                                ) {
                                    val now = System.currentTimeMillis()
                                    if (now - adminLastTapMs > 3_000) adminTapCount = 0
                                    adminLastTapMs = now
                                    adminTapCount++
                                    if (adminTapCount >= 5) {
                                        adminTapCount = 0
                                        onNavigateToAdmin()
                                    }
                                }
                        )
                    }
                }
            }
        }

        if (showSheet) {
            selectedTopic?.let { topic ->
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    QuestionCountBottomSheet(
                        topic = topic,
                        onSelect = { count ->
                            showSheet = false
                            onStartQuiz(topic.topic.id, count)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicCard(
    topicWithCount: TopicWithCount,
    onStartClick: () -> Unit
) {
    val topic = topicWithCount.topic
    val difficultyColor = when (topic.difficultyLevel) {
        1 -> CiscoGreen
        2 -> CiscoGold
        else -> CiscoRed
    }
    val difficultyLabel = when (topic.difficultyLevel) {
        1 -> "Beginner"
        2 -> "Intermediate"
        else -> "Advanced"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TopicIcon(iconName = topic.iconName, difficultyLevel = topic.difficultyLevel)

            Text(
                text = topic.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Text(
                text = "${topicWithCount.questionCount} Questions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = difficultyColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = difficultyLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = difficultyColor,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Button(
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Start Quiz",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TopicIcon(iconName: String, difficultyLevel: Int) {
    val bgColor = when (difficultyLevel) {
        1 -> CiscoGreen.copy(alpha = 0.18f)
        2 -> MaterialTheme.colorScheme.surfaceVariant
        else -> CiscoRed.copy(alpha = 0.18f)
    }
    val iconColor = when (difficultyLevel) {
        1 -> CiscoGreen
        2 -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> CiscoRed
    }
    val icon = when (iconName) {
        "ic_osi"      -> Icons.Filled.Layers
        "ic_tcp"      -> Icons.Filled.CompareArrows
        "ic_subnet"   -> Icons.Filled.Language
        "ic_dns"      -> Icons.Filled.Dns
        "ic_dhcp"     -> Icons.Filled.Router
        "ic_vlan"     -> Icons.Filled.AccountTree
        "ic_routing"  -> Icons.Filled.AltRoute
        "ic_firewall" -> Icons.Filled.Security
        else          -> Icons.Filled.Layers
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun QuestionCountBottomSheet(
    topic: TopicWithCount,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            topic.topic.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "How many questions?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val maxQuestions = topic.questionCount
        listOf(10, 20, 30).forEach { count ->
            val available = count <= maxQuestions
            OutlinedButton(
                onClick = { onSelect(count) },
                enabled = available,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$count Questions",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        if (available) "~${count / 2} min"
                        else "Not enough Qs",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (available) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
