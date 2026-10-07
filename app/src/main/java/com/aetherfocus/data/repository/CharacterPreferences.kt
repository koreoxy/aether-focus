package com.aetherfocus.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aetherfocus.core.model.CharacterProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "character_prefs")

@Singleton
class CharacterPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_NAME = stringPreferencesKey("operator_name")
        private val KEY_TITLE = stringPreferencesKey("operator_title")
        private val KEY_LEVEL = intPreferencesKey("operator_level")
        private val KEY_XP = intPreferencesKey("operator_xp")
        private val KEY_IS_CREATED = booleanPreferencesKey("is_created")
    }

    val characterFlow: Flow<CharacterProfile> = context.dataStore.data.map { prefs ->
        CharacterProfile(
            operatorName = prefs[KEY_NAME] ?: "AGENT-01",
            title = prefs[KEY_TITLE] ?: "GUARDIAN",
            level = prefs[KEY_LEVEL] ?: 1,
            currentXp = prefs[KEY_XP] ?: 0,
            isCreated = prefs[KEY_IS_CREATED] ?: false
        )
    }

    suspend fun saveCharacter(name: String, title: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NAME] = name.ifBlank { "AGENT-01" }
            prefs[KEY_TITLE] = title.ifBlank { "GUARDIAN" }
            prefs[KEY_LEVEL] = 1
            prefs[KEY_XP] = 0
            prefs[KEY_IS_CREATED] = true
        }
    }
}