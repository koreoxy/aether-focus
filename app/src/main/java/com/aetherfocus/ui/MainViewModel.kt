package com.aetherfocus.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherfocus.core.common.PermissionUtils
import com.aetherfocus.core.datastore.SessionPreferences
import com.aetherfocus.core.datastore.SessionPreferencesManager
import com.aetherfocus.core.model.InterventionTheme
import com.aetherfocus.domain.usecase.StartFocusSessionUseCase
import com.aetherfocus.domain.usecase.StopFocusSessionUseCase
import com.aetherfocus.service.ServiceController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.aetherfocus.core.model.DistractionBreakdownItem
import com.aetherfocus.core.model.FocusSession
import com.aetherfocus.core.model.SessionStats
import com.aetherfocus.domain.repository.DistractionRepository
import com.aetherfocus.domain.repository.FocusSessionRepository

data class PermissionState(
    val hasUsageStats: Boolean = false,
    val hasOverlay: Boolean = false,
    val hasNotification: Boolean = false
) {
    val allGranted: Boolean
        get() = hasUsageStats && hasOverlay && hasNotification
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionPreferencesManager: SessionPreferencesManager,
    private val startFocusSessionUseCase: StartFocusSessionUseCase,
    private val stopFocusSessionUseCase: StopFocusSessionUseCase,
    private val focusSessionRepository: FocusSessionRepository,
    private val distractionRepository: DistractionRepository
) : ViewModel() {

    val sessionPrefs: StateFlow<SessionPreferences> = sessionPreferencesManager.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionPreferences()
        )

    val sessionStats: StateFlow<SessionStats> = focusSessionRepository.getSessionStatsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionStats()
        )

    val recentSessions: StateFlow<List<FocusSession>> = focusSessionRepository.getRecentCompletedSessions(limit = 5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val topDistractions: StateFlow<List<DistractionBreakdownItem>> = distractionRepository.getTopDistractingApps(limit = 3)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    fun checkPermissions(context: Context) {
        _permissionState.value = PermissionState(
            hasUsageStats = PermissionUtils.hasUsageStatsPermission(context),
            hasOverlay = PermissionUtils.hasOverlayPermission(context),
            hasNotification = PermissionUtils.hasNotificationPermission(context)
        )
    }

    fun startSession(context: Context, goal: String, durationMinutes: Int) {
        viewModelScope.launch {
            val result = startFocusSessionUseCase(
                goalTitle = goal,
                durationMinutes = durationMinutes,
                theme = InterventionTheme.MOTIVATIONAL
            )
            if (result.isSuccess) {
                ServiceController.startGuardian(context)
            }
        }
    }

    fun stopSession(context: Context) {
        viewModelScope.launch {
            stopFocusSessionUseCase(isCompletedNaturally = false)
            ServiceController.stopGuardian(context)
        }
    }
}

