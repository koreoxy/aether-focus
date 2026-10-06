package com.aetherfocus.feature.intervention

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherfocus.core.datastore.SessionPreferences
import com.aetherfocus.core.datastore.SessionPreferencesManager
import com.aetherfocus.core.model.InterventionAction
import com.aetherfocus.domain.usecase.HandleInterventionActionUseCase
import com.aetherfocus.service.ServiceController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterventionViewModel @Inject constructor(
    private val sessionPreferencesManager: SessionPreferencesManager,
    private val handleInterventionActionUseCase: HandleInterventionActionUseCase
) : ViewModel() {

    val sessionPrefs: StateFlow<SessionPreferences> = sessionPreferencesManager.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionPreferences()
        )

    fun onBackToWork(context: Context, packageName: String, appName: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            handleInterventionActionUseCase(
                packageName = packageName,
                appName = appName,
                action = InterventionAction.BACK_TO_WORK
            )
            // Navigate back to the home screen (closing the distracting app)
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(homeIntent)
            onFinished()
        }
    }

    fun onSnooze5Minutes(packageName: String, appName: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            handleInterventionActionUseCase(
                packageName = packageName,
                appName = appName,
                action = InterventionAction.SNOOZE_5_MIN
            )
            onFinished()
        }
    }

    fun onStopSession(context: Context, packageName: String, appName: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            handleInterventionActionUseCase(
                packageName = packageName,
                appName = appName,
                action = InterventionAction.STOP_SESSION
            )
            ServiceController.stopGuardian(context)
            onFinished()
        }
    }
}

