package com.aetherfocus.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherfocus.core.designsystem.theme.CrtRed
import com.aetherfocus.core.designsystem.theme.CrtWhite
import com.aetherfocus.core.designsystem.theme.CyberCyan
import com.aetherfocus.core.designsystem.theme.PhosphorGreen
import com.aetherfocus.core.designsystem.theme.TerminalAmber

enum class SystemStatusType {
    ONLINE,
    FOCUS_ACTIVE,
    WARNING,
    BLOCKED,
    SCANNING
}

@Composable
fun StatusIndicator(
    status: SystemStatusType,
    modifier: Modifier = Modifier
) {
    val (symbol, label, color) = when (status) {
        SystemStatusType.ONLINE -> Triple("●", "ONLINE", PhosphorGreen)
        SystemStatusType.FOCUS_ACTIVE -> Triple("◆", "FOCUS ACTIVE", CyberCyan)
        SystemStatusType.WARNING -> Triple("!", "WARNING", TerminalAmber)
        SystemStatusType.BLOCKED -> Triple("✕", "SHIELD ENGAGED", CrtRed)
        SystemStatusType.SCANNING -> Triple("◉", "SCANNING...", CyberCyan)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = CrtWhite,
            letterSpacing = 1.sp,
            fontSize = 11.sp
        )
    }
}

