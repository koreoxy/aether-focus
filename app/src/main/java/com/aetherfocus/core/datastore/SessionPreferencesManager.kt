package com.aetherfocus.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aetherfocus.core.model.InterventionTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aether_session_prefs")

@Singleton
class SessionPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferenceKeys {
        val IS_FOCUS_ACTIVE = booleanPreferencesKey("is_focus_active")
        val ACTIVE_SESSION_ID = longPreferencesKey("active_session_id")
        val ACTIVE_GOAL_TITLE = stringPreferencesKey("active_goal_title")
        val SESSION_START_TIME = longPreferencesKey("session_start_time")
        val SESSION_TARGET_MINUTES = intPreferencesKey("session_target_minutes")
        val GRACE_PERIOD_END_TIME = longPreferencesKey("grace_period_end_time")
        val BLOCKED_PACKAGES = stringSetPreferencesKey("blocked_packages")
        val INTERVENTION_THEME = stringPreferencesKey("intervention_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    val preferencesFlow: Flow<SessionPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val themeStr = prefs[PreferenceKeys.INTERVENTION_THEME] ?: InterventionTheme.MOTIVATIONAL.name
            val theme = runCatching { InterventionTheme.valueOf(themeStr) }.getOrDefault(InterventionTheme.MOTIVATIONAL)

            SessionPreferences(
                isFocusActive = prefs[PreferenceKeys.IS_FOCUS_ACTIVE] ?: false,
                activeSessionId = prefs[PreferenceKeys.ACTIVE_SESSION_ID] ?: 0L,
                activeGoalTitle = prefs[PreferenceKeys.ACTIVE_GOAL_TITLE] ?: "",
                sessionStartTime = prefs[PreferenceKeys.SESSION_START_TIME] ?: 0L,
                sessionTargetDurationMinutes = prefs[PreferenceKeys.SESSION_TARGET_MINUTES] ?: 25,
                gracePeriodEndTime = prefs[PreferenceKeys.GRACE_PERIOD_END_TIME] ?: 0L,
                blockedPackageNames = prefs[PreferenceKeys.BLOCKED_PACKAGES] ?: emptySet(),
                interventionTheme = theme,
                soundEnabled = prefs[PreferenceKeys.SOUND_ENABLED] ?: true,
                vibrationEnabled = prefs[PreferenceKeys.VIBRATION_ENABLED] ?: true
            )
        }

    suspend fun startSession(
        sessionId: Long,
        goalTitle: String,
        targetDurationMinutes: Int,
        blockedPackages: Set<String>,
        theme: InterventionTheme
    ) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.IS_FOCUS_ACTIVE] = true
            prefs[PreferenceKeys.ACTIVE_SESSION_ID] = sessionId
            prefs[PreferenceKeys.ACTIVE_GOAL_TITLE] = goalTitle
            prefs[PreferenceKeys.SESSION_START_TIME] = System.currentTimeMillis()
            prefs[PreferenceKeys.SESSION_TARGET_MINUTES] = targetDurationMinutes
            prefs[PreferenceKeys.GRACE_PERIOD_END_TIME] = 0L
            prefs[PreferenceKeys.BLOCKED_PACKAGES] = blockedPackages
            prefs[PreferenceKeys.INTERVENTION_THEME] = theme.name
        }
    }

    suspend fun stopSession() {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.IS_FOCUS_ACTIVE] = false
            prefs[PreferenceKeys.ACTIVE_SESSION_ID] = 0L
            prefs[PreferenceKeys.ACTIVE_GOAL_TITLE] = ""
            prefs[PreferenceKeys.SESSION_START_TIME] = 0L
            prefs[PreferenceKeys.GRACE_PERIOD_END_TIME] = 0L
        }
    }

    suspend fun setGracePeriod(durationMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.GRACE_PERIOD_END_TIME] = System.currentTimeMillis() + durationMillis
        }
    }

    suspend fun updateBlockedPackages(packages: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.BLOCKED_PACKAGES] = packages
        }
    }

    suspend fun setInterventionTheme(theme: InterventionTheme) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.INTERVENTION_THEME] = theme.name
        }
    }
}

