package com.apps_moviles.proyecto_nuclear_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apps_moviles.proyecto_nuclear_kotlin.data.RatingRepository
import com.apps_moviles.proyecto_nuclear_kotlin.model.Rating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingViewModel(private val repository: RatingRepository) : ViewModel() {

    private val _ratings = MutableStateFlow<List<Rating>>(emptyList())
    val ratings: StateFlow<List<Rating>> = _ratings

    private val _ratingDetail = MutableStateFlow<Rating?>(null)
    val ratingDetail: StateFlow<Rating?> = _ratingDetail

    fun loadAll() {
        viewModelScope.launch {
            _ratings.value = repository.getAll()
        }
    }

    fun loadById(id: Int) {
        viewModelScope.launch {
            _ratingDetail.value = repository.getById(id)
        }
    }

    fun loadByUserId(userId: Int) {
        viewModelScope.launch {
            _ratings.value = repository.getByUserId(userId)
        }
    }

    fun insert(rating: Rating) {
        viewModelScope.launch {
            repository.insert(rating)
            loadAll()
        }
    }
}

class RatingViewModelFactory(
    private val repository: RatingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            return RatingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
