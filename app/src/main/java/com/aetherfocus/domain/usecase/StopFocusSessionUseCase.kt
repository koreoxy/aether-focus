package com.aetherfocus.domain.usecase

import com.aetherfocus.core.datastore.SessionPreferencesManager
import com.aetherfocus.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StopFocusSessionUseCase @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository,
    private val sessionPreferencesManager: SessionPreferencesManager
) {
    suspend operator fun invoke(isCompletedNaturally: Boolean = false): Result<Unit> {
        return runCatching {
            val prefs = sessionPreferencesManager.preferencesFlow.first()
            if (prefs.isFocusActive && prefs.activeSessionId > 0) {
                val elapsedSeconds = if (prefs.sessionStartTime > 0) {
                    ((System.currentTimeMillis() - prefs.sessionStartTime) / 1000).toInt()
                } else 0

                if (isCompletedNaturally) {
                    focusSessionRepository.completeSession(prefs.activeSessionId, elapsedSeconds)
                } else {
                    focusSessionRepository.cancelSession(prefs.activeSessionId, elapsedSeconds)
                }
            }
            sessionPreferencesManager.stopSession()
        }
    }
}

