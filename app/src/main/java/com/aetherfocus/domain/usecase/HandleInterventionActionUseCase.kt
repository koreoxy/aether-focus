package com.aetherfocus.domain.usecase

import com.aetherfocus.core.datastore.SessionPreferencesManager
import com.aetherfocus.core.model.InterventionAction
import com.aetherfocus.domain.repository.DistractionRepository
import com.aetherfocus.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HandleInterventionActionUseCase @Inject constructor(
    private val distractionRepository: DistractionRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val sessionPreferencesManager: SessionPreferencesManager,
    private val stopFocusSessionUseCase: StopFocusSessionUseCase
) {
    suspend operator fun invoke(
        packageName: String,
        appName: String,
        action: InterventionAction
    ): Result<Unit> {
        return runCatching {
            val prefs = sessionPreferencesManager.preferencesFlow.first()
            val sessionId = prefs.activeSessionId

            if (sessionId > 0) {
                // Record intervention event in database
                distractionRepository.recordDistraction(
                    sessionId = sessionId,
                    packageName = packageName,
                    appName = appName,
                    actionTaken = action
                )

                // Increment distraction counter in Room
                focusSessionRepository.incrementDistraction(sessionId)
            }

            when (action) {
                InterventionAction.BACK_TO_WORK -> {
                    // Handled by UI / Overlay (closes distraction and brings user back to home/work)
                }
                InterventionAction.SNOOZE_5_MIN -> {
                    // Set grace period for 5 minutes (5 * 60 * 1000 ms)
                    sessionPreferencesManager.setGracePeriod(5 * 60 * 1000L)
                }
                InterventionAction.STOP_SESSION -> {
                    // User explicitly stops the session
                    stopFocusSessionUseCase(isCompletedNaturally = false)
                }
            }
        }
    }
}

