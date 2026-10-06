package com.aetherfocus.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Aether-Focus Retro CRT Terminal × Cyberpunk TUI Color System (from DESIGN.md)
// ============================================================================

// Backgrounds
val VoidBlack = Color(0xFF050608)         // Main background, terminal background
val CrtBlack = Color(0xFF0A0D0F)          // Panels, terminal windows, cards
val CrtBlackAlt = Color(0xFF101519)       // Secondary surface, inputs, headers

// Borders
val TerminalBorder = Color(0xFF244B36)     // Primary terminal borders
val TerminalBorderDim = Color(0xFF162E21)  // Inactive / subtle borders
val TerminalBorderGlow = Color(0xFF39FF88) // Focused border glow

// Text
val CrtWhite = Color(0xFFD7FFE5)          // Primary text (Phosphor tinted white)
val TextMuted = Color(0xFF6F8F7A)         // Dimmed terminal text / comments
val TextMutedDark = Color(0xFF435A4D)     // Very dim lines / ASCII guides

// Accents
val PhosphorGreen = Color(0xFF39FF88)     // Primary Accent: active states, focus core, prompts
val CyberCyan = Color(0xFF00E5FF)         // Secondary Accent: telemetry, technical metadata
val TerminalAmber = Color(0xFFFFB000)     // Warning Accent: attention, break reminders
val CrtRed = Color(0xFFFF3B3B)            // Danger Accent: distraction detected, blocked targets
val CyberPurple = Color(0xFFB967FF)       // Rare Accent: XP, streaks, level

// Retro Aliases for backwards compatibility
val VioletPrimary = PhosphorGreen
val CyanAccent = CyberCyan
val RoseDistraction = CrtRed
val EmeraldSuccess = PhosphorGreen
val AmberWarning = TerminalAmber
val BackgroundDark = VoidBlack
val SurfaceDark = CrtBlack
val SurfaceDarkVariant = CrtBlackAlt
val BorderDark = TerminalBorder
val TextPrimaryDark = CrtWhite
val TextSecondaryDark = TextMuted
