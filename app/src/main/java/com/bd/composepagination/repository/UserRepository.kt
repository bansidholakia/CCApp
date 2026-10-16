package com.bd.composepagination.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bd.composepagination.model.User
import com.bd.composepagination.network.UserApi
import com.bd.composepagination.paging.UserPagingSource
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val api: UserApi
) {
    fun getUsers(query: String): Flow<PagingData<User>> {

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                UserPagingSource(api, query)
            }
        ).flow
    }
}
