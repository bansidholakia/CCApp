package com.bd.composepagination.network

import com.bd.composepagination.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {

    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ) : UserResponse

    @GET("users/search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): UserResponse
}
