package com.apps_moviles.proyecto_nuclear_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apps_moviles.proyecto_nuclear_kotlin.data.ItemRepository
import com.apps_moviles.proyecto_nuclear_kotlin.data.ItemWithRelations
import com.apps_moviles.proyecto_nuclear_kotlin.data.UserPreferences
import com.apps_moviles.proyecto_nuclear_kotlin.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: ItemRepository, private val prefs: UserPreferences) : ViewModel() {

    val loggedUserId = prefs.userId.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _items = MutableStateFlow<List<ItemWithRelations>>(emptyList())
    val items: StateFlow<List<ItemWithRelations>> = _items

    private val _currentItem = MutableStateFlow<ItemWithRelations?>(null)
    val currentItem: StateFlow<ItemWithRelations?> = _currentItem

    fun loadItems(userId: Int?) {
        viewModelScope.launch {
            _items.value = repository.getAllItems(userId ?: 2)
        }
    }

    fun loadItemsByCategory(category: String, userId: Int?) {
        viewModelScope.launch {
            _items.value = repository.getItemsByCategory(category, userId ?: 2)
        }
    }

    fun loadItemById(id: Int) {
        viewModelScope.launch {
            _currentItem.value = repository.getItemById(id)
        }
    }

    fun loadItemsByUser(userId: Int) {
        viewModelScope.launch {
            _items.value = repository.getItemsByUser(userId)
        }
    }

    fun insert(item: Item) {
        viewModelScope.launch {
            repository.insert(item)
            loadItems(loggedUserId.value ?: 2)
        }
    }

    fun update(item: Item) {
        viewModelScope.launch {
            repository.update(item)
            loadItems(loggedUserId.value ?: 2)
        }
    }

    fun delete(item: Item) {
        viewModelScope.launch {
            repository.delete(item)
            loadItems(loggedUserId.value ?: 2)
        }
    }
}

class ItemViewModelFactory(private val repository: ItemRepository, private val prefs: UserPreferences) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
