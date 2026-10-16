package com.bd.composepagination.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bd.composepagination.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class UserViewModel(
    repository: UserRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val users = _searchQuery
        .debounce { query -> if (query.isBlank()) 0L else 300L }
        .distinctUntilChanged()
        .flatMapLatest { query -> repository.getUsers(query) }
        .cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
