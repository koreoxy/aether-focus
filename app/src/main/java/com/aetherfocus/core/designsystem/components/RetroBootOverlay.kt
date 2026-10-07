package com.aetherfocus.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherfocus.core.designsystem.theme.CrtBlack
import com.aetherfocus.core.designsystem.theme.CrtBlackAlt
import com.aetherfocus.core.designsystem.theme.CrtWhite
import com.aetherfocus.core.designsystem.theme.CyberCyan
import com.aetherfocus.core.designsystem.theme.PhosphorGreen
import com.aetherfocus.core.designsystem.theme.TerminalBorder
import com.aetherfocus.core.designsystem.theme.TextMuted
import kotlinx.coroutines.delay

/**
 * Animated Retro CRT Boot-up pop-up sequence from DESIGN.md Section 18.
 */
@Composable
fun RetroBootOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    var step by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(300)
        step = 1 // [ OK ] MEMORY
        delay(250)
        step = 2 // [ OK ] GUARDIAN CORE
        delay(250)
        step = 3 // [ OK ] DISTRACTION SHIELD
        delay(250)
        step = 4 // [ OK ] RADAR ENGINE
        delay(300)
        step = 5 // SYSTEM READY
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(2.dp, PhosphorGreen, RoundedCornerShape(0.dp))
                .background(CrtBlack)
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "AETHER INDUSTRIES",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                letterSpacing = 2.sp
            )
            Text(
                text = "FOCUS OPERATING SYSTEM",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PhosphorGreen,
                letterSpacing = 1.sp
            )
            Text(
                text = "BOOT SEQUENCE v1.0.4",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Diagnostic boot checklist
            BootStepRow(name = "MEMORY & REGISTERS", isReady = step >= 1)
            BootStepRow(name = "GUARDIAN FOCUS CORE", isReady = step >= 2)
            BootStepRow(name = "DISTRACTION SHIELD", isReady = step >= 3)
            BootStepRow(name = "RADAR INTERCEPTOR", isReady = step >= 4)

            Spacer(modifier = Modifier.height(16.dp))

            if (step >= 5) {
                Text(
                    text = "> SYSTEM INITIALIZED. ALL SYSTEMS GO.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = PhosphorGreen
                )

                Spacer(modifier = Modifier.height(18.dp))

                PixelButton(
                    text = "ENTER SYSTEM",
                    onClick = onDismiss,
                    variant = PixelButtonVariant.PRIMARY,
                    height = 42.dp
                )
            } else {
                Text(
                    text = "> INITIALIZING... █",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberCyan
                )
            }
        }

        CRTScanlines(scanlineAlpha = 0.12f)
    }
}

@Composable
private fun BootStepRow(name: String, isReady: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "> $name",
            style = MaterialTheme.typography.labelSmall,
            color = if (isReady) CrtWhite else TextMuted
        )
        Text(
            text = if (isReady) "[ OK ]" else "[ .. ]",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isReady) PhosphorGreen else TextMuted
        )
    }
}
