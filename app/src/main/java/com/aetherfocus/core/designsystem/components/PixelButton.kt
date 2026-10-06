package com.aetherfocus.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherfocus.core.designsystem.theme.CrtBlack
import com.aetherfocus.core.designsystem.theme.PhosphorGreen
import com.aetherfocus.core.designsystem.theme.TextMutedDark
import com.aetherfocus.core.designsystem.theme.VoidBlack

enum class PixelButtonVariant {
    PRIMARY,   // Phosphor Green
    CYAN,      // Cyber Cyan
    DANGER,    // CRT Red
    AMBER      // Terminal Amber
}

/**
 * Terminal Command button with sharp pixel edges and bracket style from DESIGN.md.
 */
@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PixelButtonVariant = PixelButtonVariant.PRIMARY,
    enabled: Boolean = true,
    height: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val accentColor = when (variant) {
        PixelButtonVariant.PRIMARY -> PhosphorGreen
        PixelButtonVariant.CYAN -> com.aetherfocus.core.designsystem.theme.CyberCyan
        PixelButtonVariant.DANGER -> com.aetherfocus.core.designsystem.theme.CrtRed
        PixelButtonVariant.AMBER -> com.aetherfocus.core.designsystem.theme.TerminalAmber
    }

    val borderColor = if (enabled) accentColor else TextMutedDark
    val textColor = if (enabled) {
        if (isPressed) VoidBlack else accentColor
    } else TextMutedDark

    val backgroundColor = if (enabled && isPressed) accentColor else CrtBlack

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .border(1.dp, borderColor, RoundedCornerShape(0.dp))
            .background(backgroundColor, RoundedCornerShape(0.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = if (isPressed) "[ █ " else "[ ",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 13.sp
            )
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 1.sp,
                fontSize = 13.sp
            )
            Text(
                text = if (isPressed) " █ ]" else " ]",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 13.sp
            )
        }
    }
}

