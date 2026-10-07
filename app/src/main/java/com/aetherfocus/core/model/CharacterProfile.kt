package com.aetherfocus.core.model

data class CharacterProfile(
    val operatorName: String = "AGENT-01",
    val title: String = "GUARDIAN",
    val level: Int = 1,
    val currentXp: Int = 0,
    val maxXp: Int = 1000,
    val isCreated: Boolean = false
)