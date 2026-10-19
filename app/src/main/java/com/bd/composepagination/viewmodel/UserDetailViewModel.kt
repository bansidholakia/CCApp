package com.bd.composepagination.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bd.composepagination.model.UserDetail
import com.bd.composepagination.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

sealed interface UserDetailUiState {
    data object Loading : UserDetailUiState
    data class Content(val user: UserDetail) : UserDetailUiState
    data class Error(val message: String) : UserDetailUiState
}

class UserDetailViewModel(
    private val userId: Int,
    private val repository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        loadUser()
    }

    fun loadUser() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UserDetailUiState.Loading
            try {
                _uiState.value = UserDetailUiState.Content(repository.getUser(userId))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = UserDetailUiState.Error(
                    exception.message ?: "Unable to load user details"
                )
            }
        }
    }
}

class UserDetailViewModelFactory(
    private val userId: Int,
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserDetailViewModel(userId, repository) as T
    }
}
