package com.aetherfocus.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.aetherfocus.core.designsystem.theme.*
import com.aetherfocus.core.designsystem.components.*
import com.aetherfocus.feature.profile.CharacterProfileScreen
import com.aetherfocus.core.common.PermissionUtils

enum class NavigationTab {
    TERMINAL, PROFILE
}

@Composable
fun AetherMainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sessionPrefs by viewModel.sessionPrefs.collectAsState()
    val permissionState by viewModel.permissionState.collectAsState()

    var showBootSequence by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(NavigationTab.TERMINAL) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.checkPermissions(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissions(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var goalText by remember { mutableStateOf("Build Aether-Focus Core") }
    var selectedDuration by remember { mutableIntStateOf(25) }

    val sessionStats by viewModel.sessionStats.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()
    val topDistractions by viewModel.topDistractions.collectAsState()

    Scaffold(
        containerColor = VoidBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cyber Tab Bar Header
                CyberTabBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )

                when (currentTab) {
                    NavigationTab.TERMINAL -> {
                        // Main Terminal Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            TerminalOSHeader(isFocusActive = sessionPrefs.isFocusActive)

                            Spacer(modifier = Modifier.height(16.dp))

                            AnimatedVisibility(visible = !permissionState.allGranted) {
                                Column {
                                    PermissionProtocolsPanel(
                                        permissionState = permissionState,
                                        onRequestUsageAccess = {
                                            context.startActivity(PermissionUtils.createUsageStatsSettingsIntent())
                                        },
                                        onRequestOverlay = {
                                            context.startActivity(PermissionUtils.createOverlaySettingsIntent(context))
                                        },
                                        onRequestNotification = {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            if (sessionPrefs.isFocusActive) {
                                ActiveFocusCorePanel(
                                    goalTitle = sessionPrefs.activeGoalTitle,
                                    startTime = sessionPrefs.sessionStartTime,
                                    targetMinutes = sessionPrefs.sessionTargetDurationMinutes,
                                    onAbortClicked = {
                                        viewModel.stopSession(context)
                                    }
                                )
                            } else {
                                MissionSetupPanel(
                                    goal = goalText,
                                    onGoalChange = { goalText = it },
                                    selectedDuration = selectedDuration,
                                    onDurationSelected = { selectedDuration = it },
                                    isStartEnabled = permissionState.allGranted,
                                    onStartClicked = {
                                        viewModel.startSession(context, goalText, selectedDuration)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            MissionTelemetryPanel(stats = sessionStats)

                            Spacer(modifier = Modifier.height(16.dp))

                            RecentMissionsLogPanel(sessions = recentSessions)

                            Spacer(modifier = Modifier.height(16.dp))

                            BlockedTargetMatrixPanel()

                            Spacer(modifier = Modifier.height(16.dp))

                            TerminalFooterPrompt()
                        }
                    }

                    NavigationTab.PROFILE -> {
                        CharacterProfileScreen(
                            totalFocusHours = (sessionStats.totalFocusMinutes / 60f),
                            focusStreakDays = sessionStats.currentStreakDays,
                            completedMissionsCount = sessionStats.completedSessionsCount
                        )
                    }
                }
            }

            CRTScanlines(scanlineAlpha = 0.07f)

            RetroBootOverlay(
                isVisible = showBootSequence,
                onDismiss = { showBootSequence = false }
            )
        }
    }
}

@Composable
private fun CyberTabBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CrtBlack)
            .border(1.dp, TerminalBorderDim, RoundedCornerShape(0.dp))
            .padding(4.dp)
    ) {
        TabItem(
            label = "[01] TERMINAL CORE",
            isSelected = selectedTab == NavigationTab.TERMINAL,
            onClick = { onTabSelected(NavigationTab.TERMINAL) },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        TabItem(
            label = "[02] GUARDIAN PROFILE",
            isSelected = selectedTab == NavigationTab.PROFILE,
            onClick = { onTabSelected(NavigationTab.PROFILE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                1.dp,
                if (isSelected) PhosphorGreen else TerminalBorderDim,
                RoundedCornerShape(0.dp)
            )
            .background(if (isSelected) PhosphorGreen.copy(alpha = 0.15f) else CrtBlackAlt)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PhosphorGreen else TextMuted,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TerminalOSHeader(isFocusActive: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
            .background(CrtBlack)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AETHER-FOCUS OS v1.0",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PhosphorGreen,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "CYBERPUNK DISTRACTION PREVENTION SYSTEM",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }

            StatusIndicator(
                status = if (isFocusActive) SystemStatusType.FOCUS_ACTIVE else SystemStatusType.ONLINE
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = TerminalBorderDim)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "> OPERATOR: AGENT-01",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
            )
            Text(
                text = "CORE: " + (if (isFocusActive) "ACTIVE" else "STANDBY"),
                style = MaterialTheme.typography.labelSmall,
                color = if (isFocusActive) PhosphorGreen else TextMuted
            )
        }
    }
}

@Composable
private fun PermissionProtocolsPanel(
    permissionState: PermissionState,
    onRequestUsageAccess: () -> Unit,
    onRequestOverlay: () -> Unit,
    onRequestNotification: () -> Unit
) {
    TerminalPanel(
        title = "SYSTEM PROTOCOLS REQUIRED",
        borderColor = TerminalAmber,
        titleColor = TerminalAmber
    ) {
        Column {
            Text(
                text = "> SYSTEM ALERT: GUARDIAN ENGINE DISABLED",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TerminalAmber
            )
            Text(
                text = "Grant required OS permissions to initialize app interception protocols.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            ProtocolRow(
                index = "01",
                name = "USAGE ACCESS STATS",
                isGranted = permissionState.hasUsageStats,
                onExecute = onRequestUsageAccess
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtocolRow(
                index = "02",
                name = "DISPLAY OVERLAY ENGINE",
                isGranted = permissionState.hasOverlay,
                onExecute = onRequestOverlay
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Spacer(modifier = Modifier.height(8.dp))
                ProtocolRow(
                    index = "03",
                    name = "STATUS BAR NOTIFICATION",
                    isGranted = permissionState.hasNotification,
                    onExecute = onRequestNotification
                )
            }
        }
    }
}

@Composable
private fun ProtocolRow(
    index: String,
    name: String,
    isGranted: Boolean,
    onExecute: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isGranted) TerminalBorder else TerminalAmber.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
            .background(CrtBlackAlt)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "[$index] $name",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isGranted) PhosphorGreen else CrtWhite
            )
            Text(
                text = if (isGranted) "> STATUS: OK" else "> STATUS: ACCESS DENIED",
                style = MaterialTheme.typography.bodySmall,
                color = if (isGranted) PhosphorGreen.copy(alpha = 0.7f) else TerminalAmber,
                fontSize = 10.sp
            )
        }

        if (!isGranted) {
            PixelButton(
                text = "EXECUTE",
                onClick = onExecute,
                variant = PixelButtonVariant.AMBER,
                modifier = Modifier.width(110.dp),
                height = 34.dp
            )
        } else {
            Text(
                text = "[ OK ]",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = PhosphorGreen
            )
        }
    }
}

@Composable
private fun MissionSetupPanel(
    goal: String,
    onGoalChange: (String) -> Unit,
    selectedDuration: Int,
    onDurationSelected: (Int) -> Unit,
    isStartEnabled: Boolean,
    onStartClicked: () -> Unit
) {
    TerminalPanel(
        title = "MISSION SETUP",
        borderColor = TerminalBorder,
        titleColor = PhosphorGreen
    ) {
        Column {
            Text(
                text = "> ENTER MISSION OBJECTIVE:",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
                    .background(CrtBlackAlt)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "> ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PhosphorGreen,
                        fontWeight = FontWeight.Bold
                    )
                    BasicTextField(
                        value = goal,
                        onValueChange = onGoalChange,
                        singleLine = true,
                        cursorBrush = SolidColor(PhosphorGreen),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = CrtWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "> SELECT TARGET TIME:",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 25, 45, 60).forEach { mins ->
                    val isSelected = selectedDuration == mins
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (isSelected) PhosphorGreen else TerminalBorderDim,
                                RoundedCornerShape(0.dp)
                            )
                            .background(if (isSelected) PhosphorGreen.copy(alpha = 0.2f) else CrtBlackAlt)
                            .clickable { onDurationSelected(mins) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSelected) "[ $mins M ]" else "$mins M",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PhosphorGreen else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            PixelButton(
                text = if (isStartEnabled) "INITIALIZE FOCUS CORE" else "PERMISSIONS REQUIRED",
                onClick = onStartClicked,
                enabled = isStartEnabled,
                variant = PixelButtonVariant.PRIMARY,
                height = 50.dp
            )
        }
    }
}

@Composable
private fun ActiveFocusCorePanel(
    goalTitle: String,
    startTime: Long,
    targetMinutes: Int,
    onAbortClicked: () -> Unit
) {
    val elapsedSeconds = if (startTime > 0) {
        ((System.currentTimeMillis() - startTime) / 1000).toInt()
    } else 0
    val totalSeconds = targetMinutes * 60
    val remainingSeconds = (totalSeconds - elapsedSeconds).coerceAtLeast(0)
    val remMinutes = remainingSeconds / 60
    val remSecs = remainingSeconds % 60
    val progress = (elapsedSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    TerminalPanel(
        title = "FOCUS CORE : ENGAGED",
        borderColor = CyberCyan,
        titleColor = CyberCyan
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MISSION: ${goalTitle.uppercase()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CrtWhite,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(0.dp))
                    .background(CrtBlackAlt)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%02d:%02d".format(remMinutes, remSecs),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "> COUNTDOWN TO MISSION COMPLETION <",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TerminalProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                accentColor = CyberCyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "> GUARDIAN ENGINE: INTERCEPTING DISTRACTIONS",
                style = MaterialTheme.typography.bodySmall,
                color = PhosphorGreen,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            PixelButton(
                text = "ABORT MISSION",
                onClick = onAbortClicked,
                variant = PixelButtonVariant.DANGER,
                height = 46.dp
            )
        }
    }
}

@Composable
private fun BlockedTargetMatrixPanel() {
    TerminalPanel(
        title = "BLOCKED TARGET MATRIX",
        borderColor = TerminalBorder,
        titleColor = TextMuted
    ) {
        Column {
            Text(
                text = "> MONITORED DISTRACTION PACKAGES:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))

            val targets = listOf(
                "TIKTOK", "INSTAGRAM", "YOUTUBE",
                "X / TWITTER", "REDDIT", "FACEBOOK"
            )

            targets.chunked(2).forEach { rowTargets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTargets.forEach { target ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, TerminalBorderDim, RoundedCornerShape(0.dp))
                                .background(CrtBlackAlt)
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Text(
                                text = "[✕] $target",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CrtRed.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun TerminalFooterPrompt() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "AETHER-FOCUS-OS:~$ ",
            style = MaterialTheme.typography.bodySmall,
            color = PhosphorGreen
        )
        Text(
            text = "SYSTEM READY",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = " █",
            style = MaterialTheme.typography.bodySmall,
            color = PhosphorGreen
        )
    }
}

@Composable
private fun MissionTelemetryPanel(stats: com.aetherfocus.core.model.SessionStats) {
    TerminalPanel(
        title = "MISSION TELEMETRY & STATS",
        borderColor = TerminalBorder,
        titleColor = CyberCyan
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryStatBox(
                    label = "FOCUS TIME",
                    value = "${stats.totalFocusMinutes} MIN",
                    color = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                TelemetryStatBox(
                    label = "COMPLETED",
                    value = "${stats.completedSessionsCount} MISSIONS",
                    color = PhosphorGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryStatBox(
                    label = "DISTRACTIONS",
                    value = "${stats.totalDistractionsCount} SHIELDED",
                    color = CrtRed,
                    modifier = Modifier.weight(1f)
                )
                TelemetryStatBox(
                    label = "STREAK",
                    value = "${stats.currentStreakDays} DAYS",
                    color = CyberPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TelemetryStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, TerminalBorderDim, RoundedCornerShape(0.dp))
            .background(CrtBlackAlt)
            .padding(vertical = 10.dp, horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = "> $label",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun RecentMissionsLogPanel(sessions: List<com.aetherfocus.core.model.FocusSession>) {
    TerminalPanel(
        title = "RECENT MISSION LOGS",
        borderColor = TerminalBorder,
        titleColor = PhosphorGreen
    ) {
        Column {
            if (sessions.isEmpty()) {
                Text(
                    text = "> SEARCHING LOCAL MISSION DATABASE...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = "> NO COMPLETED MISSIONS LOGGED YET.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            } else {
                sessions.forEach { session ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, TerminalBorderDim, RoundedCornerShape(0.dp))
                            .background(CrtBlackAlt)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "● ${session.goalTitle.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CrtWhite
                            )
                            Text(
                                text = "> ${session.actualDurationSeconds / 60}m focused • ${session.distractionCount} distractions",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = "[ DONE ]",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PhosphorGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}