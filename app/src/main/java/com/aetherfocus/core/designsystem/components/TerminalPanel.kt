package com.aetherfocus.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherfocus.core.designsystem.theme.CrtBlack
import com.aetherfocus.core.designsystem.theme.CrtBlackAlt
import com.aetherfocus.core.designsystem.theme.CrtWhite
import com.aetherfocus.core.designsystem.theme.PhosphorGreen
import com.aetherfocus.core.designsystem.theme.TerminalBorder
import com.aetherfocus.core.designsystem.theme.TextMuted

/**
 * Terminal window panel with ASCII header style and sharp borders from DESIGN.md.
 */
@Composable
fun TerminalPanel(
    title: String,
    modifier: Modifier = Modifier,
    borderColor: Color = TerminalBorder,
    titleColor: Color = PhosphorGreen,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(0.dp))
            .background(CrtBlack)
    ) {
        // Terminal Window Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CrtBlackAlt)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "┌─[ ",
                style = MaterialTheme.typography.labelSmall,
                color = borderColor
            )
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                letterSpacing = 1.sp
            )
            Text(
                text = " ]",
                style = MaterialTheme.typography.labelSmall,
                color = borderColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = borderColor
            )
        }

        // Inner Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            content()
        }
    }
}
