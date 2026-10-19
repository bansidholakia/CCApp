package com.bd.composepagination.network

import com.bd.composepagination.model.UserDetail
import com.bd.composepagination.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

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

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDetail
}
