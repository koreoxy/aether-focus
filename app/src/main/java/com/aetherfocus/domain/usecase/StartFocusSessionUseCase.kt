package com.aetherfocus.domain.usecase

import com.aetherfocus.core.datastore.SessionPreferencesManager
import com.aetherfocus.core.model.InterventionTheme
import com.aetherfocus.domain.repository.BlockedAppRepository
import com.aetherfocus.domain.repository.FocusSessionRepository
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository,
    private val blockedAppRepository: BlockedAppRepository,
    private val sessionPreferencesManager: SessionPreferencesManager
) {
    suspend operator fun invoke(
        goalTitle: String,
        durationMinutes: Int,
        theme: InterventionTheme
    ): Result<Long> {
        val sanitizedGoal = goalTitle.trim().ifEmpty { "Focus Session" }
        val sanitizedDuration = durationMinutes.coerceIn(5, 180)

        return runCatching {
            blockedAppRepository.seedDefaultDistractionsIfNeeded()
            val blockedPackages = blockedAppRepository.getBlockedPackageNames()

            val sessionId = focusSessionRepository.startSession(
                goalTitle = sanitizedGoal,
                durationMinutes = sanitizedDuration,
                theme = theme
            )

            sessionPreferencesManager.startSession(
                sessionId = sessionId,
                goalTitle = sanitizedGoal,
                targetDurationMinutes = sanitizedDuration,
                blockedPackages = blockedPackages,
                theme = theme
            )

            sessionId
        }
    }
}

