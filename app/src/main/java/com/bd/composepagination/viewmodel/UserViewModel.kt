package com.bd.composepagination.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bd.composepagination.repository.UserRepository

class UserViewModel(
    repository: UserRepository
) : ViewModel() {

    val users =
        repository
            .getUsers()
            .cachedIn(viewModelScope)
}