package com.aetherfocus.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetherfocus.data.repository.CharacterPreferences
import com.aetherfocus.core.model.CharacterProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterProfileViewModel @Inject constructor(
    private val characterPreferences: CharacterPreferences
) : ViewModel() {

    // Membaca data secara asynchronous dari DataStore (Penyimpanan HP)
    val characterProfile: StateFlow<CharacterProfile> = characterPreferences.characterFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CharacterProfile()
        )

    fun createCharacter(name: String, title: String) {
        viewModelScope.launch {
            characterPreferences.saveCharacter(name, title)
        }
    }
}