package com.aetherfocus.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aetherfocus.core.model.CharacterProfile
import androidx.compose.foundation.clickable



private val VoidBlack = Color(0xFF050608)
private val CRTBlack = Color(0xFF0A0D0F)
private val CRTBlackAlt = Color(0xFF101519)
private val CRTWhite = Color(0xFFD7FFE5)
private val PhosphorGreen = Color(0xFF39FF88)
private val CyberCyan = Color(0xFF00E5FF)
private val CyberPurple = Color(0xFFB967FF)
private val TerminalBorder = Color(0xFF244B36)
private val TerminalBorderDim = Color(0xFF163223)
private val TextMuted = Color(0xFF6F8F7A)

data class Achievement(
    val title: String,
    val description: String,
    val isUnlocked: Boolean
)

@Composable
fun CharacterProfileScreen(
    totalFocusHours: Float = 0f,
    focusStreakDays: Int = 0,
    completedMissionsCount: Int = 0,
    viewModel: CharacterProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.characterProfile.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = VoidBlack
    ) {
        if (!profile.isCreated) {
            // Form Pembuatan Karakter Pertama Kali
            CreateCharacterForm(
                onCreateCharacter = { name, title ->
                    viewModel.createCharacter(name, title)
                }
            )
        } else {
            // Tampilan Profil Utama (Data Dinamis)
            DynamicProfileDashboard(
                profile = profile,
                totalFocusHours = totalFocusHours,
                focusStreakDays = focusStreakDays,
                completedMissionsCount = completedMissionsCount
            )
        }
    }
}

@Composable
private fun CreateCharacterForm(
    onCreateCharacter: (String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var titleInput by remember { mutableStateOf("SENTINEL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PhosphorGreen, RoundedCornerShape(0.dp))
                .background(CRTBlack)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "┌─[ INITIALIZE OPERATOR PROFILE ]──────────────┐",
                    color = PhosphorGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "> NO OPERATOR IDENTITY DETECTED.",
                    color = CRTWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
                Text(
                    text = "Please register your profile to begin focus missions.",
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "> ENTER OPERATOR CALLSIGN:",
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Input Box Name
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
                        .background(CRTBlackAlt)
                        .padding(10.dp)
                ) {
                    BasicTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        cursorBrush = SolidColor(PhosphorGreen),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = CRTWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "> SELECT OPERATOR TITLE:",
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("GUARDIAN", "SENTINEL", "CYBER ZEN").forEach { title ->
                        val isSelected = titleInput == title
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, if (isSelected) PhosphorGreen else TerminalBorderDim, RoundedCornerShape(0.dp))
                                .background(if (isSelected) PhosphorGreen.copy(alpha = 0.2f) else CRTBlackAlt)
                                .padding(vertical = 8.dp)
                                .noRippleClickable { titleInput = title },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) PhosphorGreen else TextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PhosphorGreen, RoundedCornerShape(0.dp))
                        .background(PhosphorGreen.copy(alpha = 0.2f))
                        .noRippleClickable {
                            onCreateCharacter(nameInput, titleInput)
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ INITIALIZE CHARACTER ]",
                        color = PhosphorGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicProfileDashboard(
    profile: CharacterProfile,
    totalFocusHours: Float,
    focusStreakDays: Int,
    completedMissionsCount: Int
) {
    // XP dinamis berdasarkan jam fokus (1 Jam = 200 XP)
    val dynamicXp = ((totalFocusHours * 200) % profile.maxXp).toInt()
    val dynamicLevel = profile.level + ((totalFocusHours * 200) / profile.maxXp).toInt()

    val achievements = listOf(
        Achievement("FIRST GUARDIAN", "Selesaikan sesi fokus pertama kamu", completedMissionsCount > 0),
        Achievement("DEEP WORK MASTER", "Selesaikan total 10 jam fokus", totalFocusHours >= 10f),
        Achievement("7-DAY SENTINEL", "Pertahankan streak fokus selama 7 hari", focusStreakDays >= 7),
        Achievement("CYBER ZEN", "Mencapai total 50 jam waktu fokus", totalFocusHours >= 50f)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CharacterHeaderCard(
                name = profile.operatorName,
                title = profile.title,
                level = dynamicLevel,
                currentXp = dynamicXp,
                maxXp = profile.maxXp
            )
        }

        item {
            StatsSection(
                streakDays = focusStreakDays,
                totalHours = totalFocusHours,
                completedMissions = completedMissionsCount
            )
        }

        item {
            Text(
                text = "┌─[ MISSION ACHIEVEMENTS ]─────────────────────────┐",
                color = TerminalBorder,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(achievements) { achievement ->
            AchievementCard(achievement = achievement)
        }

        item {
            Text(
                text = "└──────────────────────────────────────────────────┘",
                color = TerminalBorder,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CharacterHeaderCard(
    name: String,
    title: String,
    level: Int,
    currentXp: Int,
    maxXp: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
            .background(CRTBlack)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "> GUARDIAN MATRIX",
                    color = PhosphorGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SYS_ID: 0x884F",
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .border(1.dp, CyberCyan, RoundedCornerShape(0.dp))
                    .background(CRTBlackAlt),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(2).uppercase(),
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "> OPERATOR: ${name.uppercase()}",
                color = CRTWhite,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "[ LEVEL $level $title ]",
                color = CyberPurple,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            val progressRatio = (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)
            val totalBlocks = 18
            val filledBlocks = (progressRatio * totalBlocks).toInt()
            val asciiBar = "█".repeat(filledBlocks) + "░".repeat(totalBlocks - filledBlocks)

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "FOCUS XP", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    Text(text = "$currentXp / $maxXp XP", color = PhosphorGreen, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TerminalBorderDim, RoundedCornerShape(0.dp))
                        .background(CRTBlackAlt)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$asciiBar ${(progressRatio * 100).toInt()}%",
                        color = PhosphorGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsSection(
    streakDays: Int,
    totalHours: Float,
    completedMissions: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
                .background(CRTBlack)
                .padding(10.dp)
        ) {
            Column {
                Text(text = "> STREAK", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "$streakDays DAYS", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
                .background(CRTBlack)
                .padding(10.dp)
        ) {
            Column {
                Text(text = "> TOTAL FOCUS", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${"%.1f".format(totalHours)}H", color = CRTWhite, fontFamily = FontFamily.Monospace, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
                .background(CRTBlack)
                .padding(10.dp)
        ) {
            Column {
                Text(text = "> MISSIONS", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "$completedMissions DONE", color = PhosphorGreen, fontFamily = FontFamily.Monospace, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (achievement.isUnlocked) TerminalBorder else TerminalBorderDim, RoundedCornerShape(0.dp))
            .background(if (achievement.isUnlocked) CRTBlack else CRTBlackAlt)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (achievement.isUnlocked) "[★]" else "[✕]",
                color = if (achievement.isUnlocked) PhosphorGreen else TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    color = if (achievement.isUnlocked) CRTWhite else TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }

            Text(
                text = if (achievement.isUnlocked) "[ UNLOCKED ]" else "[ LOCKED ]",
                color = if (achievement.isUnlocked) PhosphorGreen else TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
// Extension Helper Modifier Click Tanpa Ripple Surface
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        onClick = onClick,
        indication = null,
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    )
)