package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Question
import com.example.data.model.QuizCategory
import com.example.data.model.SecurityLog
import com.example.ui.theme.*
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel,
    quizViewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val isAuthenticated by adminViewModel.isAuthenticated.collectAsState()

    if (!isAuthenticated) {
        AdminLoginView(
            adminViewModel = adminViewModel,
            onBack = { quizViewModel.navigateTo(ScreenDestination.Home) }
        )
    } else {
        AdminDashboardView(
            adminViewModel = adminViewModel,
            onLogout = {
                adminViewModel.logout()
                quizViewModel.navigateTo(ScreenDestination.Home)
            }
        )
    }
}

@Composable
fun AdminLoginView(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBgDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.Start)
                .testTag("admin_login_back_button")
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(QuizPrimary, Color(0xFF6366F1)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Portal Administrativo Seguro",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = TextPrimary
        )

        Text(
            text = "Acesso restrito para gestão de perguntas, segurança e monetização.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(QuizSurface)
                .border(1.dp, CardBorderGlow, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Digite o PIN Mestre (Padrão: 9876)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { pinText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPin = !showPin }) {
                            Icon(
                                imageVector = if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuizPrimary,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = QuizRubyLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val success = adminViewModel.loginWithPin(pinText)
                        if (!success) {
                            errorMessage = "PIN incorreto. Tentativa registrada no log de segurança."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_submit_pin_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuizPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Acessar Painel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardView(
    adminViewModel: AdminViewModel,
    onLogout: () -> Unit
) {
    val selectedTab by adminViewModel.selectedTab.collectAsState()
    val analytics by adminViewModel.analytics.collectAsState()
    val questions by adminViewModel.questionsList.collectAsState()
    val logs by adminViewModel.securityLogs.collectAsState()
    val dailyConfig by adminViewModel.dailyConfig.collectAsState()
    val adConfig by adminViewModel.adConfig.collectAsState()

    val tabs = listOf("Visão Geral", "Perguntas (CRUD)", "Desafio Diário", "Segurança & Logs", "Monetização")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBgDark)
    ) {
        // TopBar do Painel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = QuizGold,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QUIZ MANIA ADMIN",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuizSurfaceVariant,
                    contentColor = QuizRubyLight
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("admin_logout_button")
            ) {
                Text("Sair", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Tabs com Scroll
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = QuizSurface,
            contentColor = QuizPrimaryLight,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = QuizPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { adminViewModel.selectTab(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Conteúdo da Tab
        when (selectedTab) {
            0 -> AdminAnalyticsTab(analytics = analytics, onRefresh = { adminViewModel.loadAnalytics() })
            1 -> AdminQuestionsTab(questions = questions, onAddQuestion = { cat, q, o0, o1, o2, o3, cor, exp, diff ->
                adminViewModel.addQuestion(cat, q, o0, o1, o2, o3, cor, exp, diff)
            }, onDeleteQuestion = { id -> adminViewModel.deleteQuestion(id) })
            2 -> AdminDailyChallengeTab(config = dailyConfig, onSave = { title, desc, cat, mult, coins ->
                adminViewModel.updateDailyChallenge(title, desc, cat, mult, coins)
            })
            3 -> AdminSecurityLogsTab(logs = logs)
            4 -> AdminMonetizationTab(adConfig = adConfig, onSave = { banner, inter, coins ->
                adminViewModel.updateAdSettings(banner, inter, coins)
            })
        }
    }
}

@Composable
fun AdminAnalyticsTab(
    analytics: com.example.data.model.SystemAnalytics?,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Métricas Operacionais em Tempo Real",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                    color = TextPrimary
                )
                IconButton(onClick = onRefresh) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Atualizar", tint = QuizPrimaryLight)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminKpiCard("Banco de Perguntas", "${analytics?.totalQuestionsInBank ?: 300}", QuizPrimary, Modifier.weight(1f))
                AdminKpiCard("Categorias Ativas", "${analytics?.activeCategoriesCount ?: 12}", QuizCyan, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminKpiCard("Partidas Jogadas", "${analytics?.totalMatchesPlayed ?: 0}", QuizGold, Modifier.weight(1f))
                AdminKpiCard("Assinantes VIP", "${analytics?.activeVipSubscribers ?: 0}", QuizEmerald, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminKpiCard("Anti-Cheat Alertas", "${analytics?.antiCheatInterceptionsCount ?: 0}", QuizRuby, Modifier.weight(1f))
                AdminKpiCard("Rate Limits Bloq.", "${analytics?.rateLimitBlockedRequests ?: 0}", QuizOrange, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AdminKpiCard(title: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(QuizSurfaceVariant)
            .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(text = title, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = accentColor)
        }
    }
}

@Composable
fun AdminQuestionsTab(
    questions: List<Question>,
    onAddQuestion: (String, String, String, String, String, String, Int, String, Int) -> Unit,
    onDeleteQuestion: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newQuestionText by remember { mutableStateOf("") }
    var newOpt0 by remember { mutableStateOf("") }
    var newOpt1 by remember { mutableStateOf("") }
    var newOpt2 by remember { mutableStateOf("") }
    var newOpt3 by remember { mutableStateOf("") }
    var newCorrectIndex by remember { mutableStateOf(0) }
    var newExplanation by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf("geral") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = { showAddDialog = !showAddDialog },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_add_question_toggle"),
                colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (showAddDialog) "Fechar Formulário" else "Adicionar Nova Pergunta", fontWeight = FontWeight.Bold)
            }
        }

        if (showAddDialog) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(QuizSurface)
                        .border(1.dp, QuizPrimaryLight, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Cadastrar Pergunta Segura", fontWeight = FontWeight.Bold, color = TextPrimary)

                        OutlinedTextField(
                            value = newQuestionText,
                            onValueChange = { newQuestionText = it },
                            label = { Text("Texto da Pergunta") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )

                        OutlinedTextField(
                            value = newOpt0,
                            onValueChange = { newOpt0 = it },
                            label = { Text("Opção A (Índice 0)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = newOpt1,
                            onValueChange = { newOpt1 = it },
                            label = { Text("Opção B (Índice 1)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = newOpt2,
                            onValueChange = { newOpt2 = it },
                            label = { Text("Opção C (Índice 2)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        OutlinedTextField(
                            value = newOpt3,
                            onValueChange = { newOpt3 = it },
                            label = { Text("Opção D (Índice 3)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )

                        OutlinedTextField(
                            value = newExplanation,
                            onValueChange = { newExplanation = it },
                            label = { Text("Explicação do Gabarito") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Opção Correta (0 a 3):", color = TextSecondary, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                (0..3).forEach { idx ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (newCorrectIndex == idx) QuizEmerald else QuizSurfaceLight)
                                            .clickable { newCorrectIndex = idx }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("${when(idx){0->"A";1->"B";2->"C";else->"D"}}", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (newQuestionText.isNotBlank() && newOpt0.isNotBlank() && newOpt1.isNotBlank()) {
                                    onAddQuestion(selectedCatId, newQuestionText, newOpt0, newOpt1, newOpt2, newOpt3, newCorrectIndex, newExplanation, 2)
                                    newQuestionText = ""
                                    newOpt0 = ""
                                    newOpt1 = ""
                                    newOpt2 = ""
                                    newOpt3 = ""
                                    newExplanation = ""
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = QuizEmerald),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Salvar Pergunta no Banco", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Perguntas Cadastradas (${questions.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        items(questions.take(50)) { q ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(QuizSurface)
                    .border(1.dp, CardBorderGlow, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = q.questionText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        Text(text = "Categoria: ${q.categoryId.uppercase()} | Correta: ${when(q.correctOptionIndex){0->"A";1->"B";2->"C";else->"D"}}", fontSize = 11.sp, color = QuizGold)
                    }

                    IconButton(onClick = { onDeleteQuestion(q.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Deletar", tint = QuizRubyLight)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDailyChallengeTab(
    config: com.example.data.model.DailyChallengeConfig,
    onSave: (String, String, String, Float, Int) -> Unit
) {
    var title by remember(config) { mutableStateOf(config.title) }
    var desc by remember(config) { mutableStateOf(config.description) }
    var categoryId by remember(config) { mutableStateOf(config.categoryId) }
    var multiplierText by remember(config) { mutableStateOf("${config.xpMultiplier}") }
    var coinsText by remember(config) { mutableStateOf("${config.coinReward}") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "Configuração do Desafio Diário", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(QuizSurface)
                    .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título do Desafio") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descrição / Instruções") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    OutlinedTextField(
                        value = multiplierText,
                        onValueChange = { multiplierText = it },
                        label = { Text("Multiplicador XP (ex: 2.0)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    OutlinedTextField(
                        value = coinsText,
                        onValueChange = { coinsText = it },
                        label = { Text("Recompensa em Moedas (ex: 150)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    Button(
                        onClick = {
                            val mult = multiplierText.toFloatOrNull() ?: 2.0f
                            val coins = coinsText.toIntOrNull() ?: 150
                            onSave(title, desc, categoryId, mult, coins)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = QuizGold, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Atualizar Desafio no Servidor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSecurityLogsTab(logs: List<SecurityLog>) {
    val dateFormat = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Trilha de Auditoria & Segurança em Tempo Real",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        items(logs) { log ->
            val sevColor = when (log.severity) {
                "ALERT" -> QuizRuby
                "WARN" -> QuizOrange
                "INFO" -> QuizCyan
                else -> QuizEmerald
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(QuizSurface)
                    .border(1.dp, CardBorderGlow, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(sevColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = log.eventType, fontSize = 10.sp, fontWeight = FontWeight.Black, color = sevColor)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = log.description, fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMonetizationTab(
    adConfig: com.example.data.model.AdSystemConfig,
    onSave: (Boolean, Int, Int) -> Unit
) {
    var bannerEnabled by remember(adConfig) { mutableStateOf(adConfig.bannerAdsEnabled) }
    var interFreqText by remember(adConfig) { mutableStateOf("${adConfig.interstitialIntervalMatches}") }
    var rewardedCoinsText by remember(adConfig) { mutableStateOf("${adConfig.rewardedVideoCoins}") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(text = "Gestão de Anúncios e Monetização", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(QuizSurface)
                    .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Banners de Anúncio", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Exibir para usuários não-VIP", fontSize = 12.sp, color = TextMuted)
                        }
                        Switch(
                            checked = bannerEnabled,
                            onCheckedChange = { bannerEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = QuizPrimary)
                        )
                    }

                    OutlinedTextField(
                        value = interFreqText,
                        onValueChange = { interFreqText = it },
                        label = { Text("Frequência Anúncio Intersticial (a cada X partidas)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    OutlinedTextField(
                        value = rewardedCoinsText,
                        onValueChange = { rewardedCoinsText = it },
                        label = { Text("Recompensa de Moedas por Vídeo Assistido") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    Button(
                        onClick = {
                            val freq = interFreqText.toIntOrNull() ?: 3
                            val coins = rewardedCoinsText.toIntOrNull() ?: 50
                            onSave(bannerEnabled, freq, coins)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salvar Configurações de Anúncios", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
