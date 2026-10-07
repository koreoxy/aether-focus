package com.aetherfocus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Tokens DESIGN.md
private val VoidBlack = Color(0xFF050608)
private val CRTBlack = Color(0xFF0A0D0F)
private val PhosphorGreen = Color(0xFF39FF88)
private val CyberCyan = Color(0xFF00E5FF)
private val CRTWhite = Color(0xFFD7FFE5)
private val TerminalBorder = Color(0xFF244B36)
private val TextMuted = Color(0xFF6F8F7A)

@Composable
fun RetroBootOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    var bootLogs by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            val steps = listOf(
                "AETHER INDUSTRIES FOCUS OS v1.0.4",
                "------------------------------------",
                "[ OK ] MEMORY MATRIX INITIALIZED",
                "[ OK ] FOCUS CORE ENGINE ONLINE",
                "[ OK ] DISTRACTION MONITOR READY",
                "[ OK ] BLOCKLIST PROTOCOLS LOADED",
                "[ OK ] SESSION TELEMETRY LINKED",
                "------------------------------------",
                "> SYSTEM READY. INITIALIZING INTERFACE..."
            )
            for (step in steps) {
                bootLogs = bootLogs + step
                delay(120)
            }
            delay(400)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VoidBlack)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
                    .background(CRTBlack)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "┌─[ AETHER-FOCUS BOOT SEQUENCE ]────────┐",
                        color = PhosphorGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    bootLogs.forEach { log ->
                        val color = when {
                            log.contains("[ OK ]") -> PhosphorGreen
                            log.contains("SYSTEM READY") -> CyberCyan
                            log.contains("AETHER INDUSTRIES") -> CRTWhite
                            else -> TextMuted
                        }
                        Text(
                            text = log,
                            color = color,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "> _",
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