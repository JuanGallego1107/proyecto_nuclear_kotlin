package com.apps_moviles.proyecto_nuclear_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apps_moviles.proyecto_nuclear_kotlin.model.Interaction
import com.apps_moviles.proyecto_nuclear_kotlin.model.InteractionWithRelations
import com.apps_moviles.proyecto_nuclear_kotlin.data.InteractionRepository
import com.apps_moviles.proyecto_nuclear_kotlin.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InteractionViewModel(
    private val repository: InteractionRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val loggedUserId = prefs.userId.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _interactions = MutableStateFlow<List<InteractionWithRelations>>(emptyList())
    val interactions: StateFlow<List<InteractionWithRelations>> = _interactions

    private val _selectedInteraction = MutableStateFlow<InteractionWithRelations?>(null)
    val selectedInteraction: StateFlow<InteractionWithRelations?> = _selectedInteraction

    fun loadAll() {
        viewModelScope.launch {
            _interactions.value = repository.getAll()
        }
    }

    fun loadById(id: Int) {
        viewModelScope.launch {
            _selectedInteraction.value = repository.getById(id)
        }
    }

    fun loadByUserId(userId: Int) {
        viewModelScope.launch {
            _interactions.value = repository.getByUserId(userId)
        }
    }

    fun createInteraction(interaction: Interaction) {
        viewModelScope.launch {
            repository.insert(interaction)
            loadById(selectedInteraction.value?.interaction?.id ?: 1)
        }
    }

    fun completeInteraction(id: Int, itemId: Int) {
        viewModelScope.launch {
            repository.markInteraction(id,itemId)
            loadById(selectedInteraction.value?.interaction?.id ?: 1)
        }
    }

    class InteractionViewModelFactory(
        private val repository: InteractionRepository,
        private val prefs: UserPreferences
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InteractionViewModel::class.java)) {
                return InteractionViewModel(repository, prefs) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
