package com.aetherfocus.feature.intervention

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherfocus.core.designsystem.components.CRTScanlines
import com.aetherfocus.core.designsystem.components.RetroVideoFeed
import com.aetherfocus.core.designsystem.components.PixelButton
import com.aetherfocus.core.designsystem.components.PixelButtonVariant
import com.aetherfocus.core.designsystem.components.TerminalPanel
import com.aetherfocus.core.designsystem.theme.AetherFocusTheme
import com.aetherfocus.core.designsystem.theme.CrtBlack
import com.aetherfocus.core.designsystem.theme.CrtBlackAlt
import com.aetherfocus.core.designsystem.theme.CrtRed
import com.aetherfocus.core.designsystem.theme.CrtWhite
import com.aetherfocus.core.designsystem.theme.CyberCyan
import com.aetherfocus.core.designsystem.theme.PhosphorGreen
import com.aetherfocus.core.designsystem.theme.TerminalAmber
import com.aetherfocus.core.designsystem.theme.TerminalBorder
import com.aetherfocus.core.designsystem.theme.TerminalBorderDim
import com.aetherfocus.core.designsystem.theme.TextMuted
import com.aetherfocus.core.designsystem.theme.VoidBlack
import com.aetherfocus.service.InterventionLauncher
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.remember

@AndroidEntryPoint
class InterventionActivity : ComponentActivity() {

    private val viewModel: InterventionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val blockedPackage = intent.getStringExtra(InterventionLauncher.EXTRA_BLOCKED_PACKAGE) ?: "com.distraction.app"
        val appName = intent.getStringExtra(InterventionLauncher.EXTRA_APP_NAME) ?: "DISTRACTION TARGET"

        setContent {
            AetherFocusTheme {
                val context = LocalContext.current
                val sessionPrefs by viewModel.sessionPrefs.collectAsState()

                // Intercept back button to perform "Return to Mission"
                BackHandler {
                    viewModel.onBackToWork(context, blockedPackage, appName) {
                        finish()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VoidBlack
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        RetroDistractionAlertContent(
                            blockedAppName = appName,
                            goalTitle = sessionPrefs.activeGoalTitle,
                            remainingMinutes = calculateRemainingMinutes(
                                sessionPrefs.sessionStartTime,
                                sessionPrefs.sessionTargetDurationMinutes
                            ),
                            onBackToWork = {
                                viewModel.onBackToWork(context, blockedPackage, appName) {
                                    finish()
                                }
                            },
                            onSnooze5Minutes = {
                                viewModel.onSnooze5Minutes(blockedPackage, appName) {
                                    finish()
                                }
                            },
                            onStopSession = {
                                viewModel.onStopSession(context, blockedPackage, appName) {
                                    finish()
                                }
                            }
                        )

                        // CRT Scanline Overlay
                        CRTScanlines(scanlineAlpha = 0.09f)
                    }
                }
            }
        }
    }

    private fun calculateRemainingMinutes(startTime: Long, targetMinutes: Int): Int {
        if (startTime <= 0) return targetMinutes
        val elapsedMinutes = ((System.currentTimeMillis() - startTime) / (1000 * 60)).toInt()
        return (targetMinutes - elapsedMinutes).coerceAtLeast(1)
    }
}

@Composable
private fun RetroDistractionAlertContent(
    blockedAppName: String,
    goalTitle: String,
    remainingMinutes: Int,
    onBackToWork: () -> Unit,
    onSnooze5Minutes: () -> Unit,
    onStopSession: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "alert_pulse")
    val alertAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Red Cyberpunk Alert Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, CrtRed.copy(alpha = alertAlpha), RoundedCornerShape(0.dp))
                .background(CrtBlack)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Alert Header
            Text(
                text = "╔═══════════════════════════════════╗",
                style = MaterialTheme.typography.bodySmall,
                color = CrtRed,
                fontSize = 11.sp
            )
            Text(
                text = "! DISTRACTION DETECTED !",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CrtRed,
                letterSpacing = 2.sp
            )
            Text(
                text = "╚═══════════════════════════════════╝",
                style = MaterialTheme.typography.bodySmall,
                color = CrtRed,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Target Intruder Tag
            Box(
                modifier = Modifier
                    .border(1.dp, CrtRed, RoundedCornerShape(0.dp))
                    .background(CrtRed.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "TARGET: ${blockedAppName.uppercase()}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CrtWhite,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Video Feed Monitor Container (from DESIGN.md Section 14)
            TerminalPanel(
                title = "GUARDIAN FEED",
                borderColor = TerminalBorder,
                titleColor = CyberCyan
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Video Feed Container
                    RetroVideoFeed(
                        targetAppName = blockedAppName,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "MISSION: ${goalTitle.uppercase().ifEmpty { "ACTIVE FOCUS" }}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "TIME REMAINING: $remainingMinutes MINUTES",
                        style = MaterialTheme.typography.labelSmall,
                        color = PhosphorGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Command 1: Return to Mission (Primary Phosphor Green Action)
            PixelButton(
                text = "RETURN TO MISSION",
                onClick = onBackToWork,
                variant = PixelButtonVariant.PRIMARY,
                height = 50.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Command 2: 5 Min Recovery Mode (Cyan)
            PixelButton(
                text = "5 MIN RECOVERY MODE",
                onClick = onSnooze5Minutes,
                variant = PixelButtonVariant.CYAN,
                height = 42.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Command 3: Abort Mission (Red Danger)
            PixelButton(
                text = "ABORT MISSION",
                onClick = onStopSession,
                variant = PixelButtonVariant.DANGER,
                height = 38.dp
            )
        }
    }
}
