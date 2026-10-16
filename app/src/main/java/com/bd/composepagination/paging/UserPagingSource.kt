package com.bd.composepagination.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bd.composepagination.model.User
import com.bd.composepagination.model.matchesFullName
import com.bd.composepagination.network.UserApi

class UserPagingSource(
    private val api: UserApi,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {

        val skip = params.key ?: 0
        val limit = params.loadSize

        return try {

            val response = if (query.isBlank()) {
                api.getUsers(limit = limit, skip = skip)
            } else {
                api.searchUsers(query = query, limit = limit, skip = skip)
            }
            val responseUsers = response.users
            val users = responseUsers.filter { it.matchesFullName(query) }

            LoadResult.Page(
                data = users,

                prevKey = if (skip == 0) {
                    null
                } else {
                    maxOf(0, skip - limit)
                },

                nextKey = if (
                    skip + responseUsers.size >= response.total
                ) {
                    null
                } else {
                    skip + responseUsers.size
                }
            )

        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        val anchorPosition =
            state.anchorPosition ?: return null

        val anchorPage =
            state.closestPageToPosition(anchorPosition)
                ?: return null

        return anchorPage.prevKey?.let {
            it + state.config.pageSize
        } ?: anchorPage.nextKey?.let {
            maxOf(0, it - state.config.pageSize)
        }
    }
}
