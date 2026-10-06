package com.aetherfocus.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aetherfocus.core.designsystem.theme.CrtWhite
import com.aetherfocus.core.designsystem.theme.PhosphorGreen
import com.aetherfocus.core.designsystem.theme.TextMutedDark

/**
 * Terminal Block Progress Bar [██████████░░░░░░] 60% as specified in DESIGN.md.
 */
@Composable
fun TerminalProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    totalBlocks: Int = 18,
    accentColor: Color = PhosphorGreen
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val filledCount = (clampedProgress * totalBlocks).toInt()
    val emptyCount = (totalBlocks - filledCount).coerceAtLeast(0)
    val percent = (clampedProgress * 100).toInt()

    val filledStr = "█".repeat(filledCount)
    val emptyStr = "░".repeat(emptyCount)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "[",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            fontSize = 12.sp
        )
        Text(
            text = filledStr,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            fontSize = 12.sp
        )
        Text(
            text = emptyStr,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = TextMutedDark,
            fontSize = 12.sp
        )
        Text(
            text = "] ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            fontSize = 12.sp
        )
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = CrtWhite,
            fontSize = 11.sp
        )
    }
}

