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
    private val stopFocusSessionUseCase: StopFocusSessionUseCase
) : ViewModel() {

    val sessionPrefs: StateFlow<SessionPreferences> = sessionPreferencesManager.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionPreferences()
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

