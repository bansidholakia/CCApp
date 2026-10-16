package com.bd.composepagination.network

import com.bd.composepagination.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {

    @GET("users")
    suspend fun getUser(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ) : UserResponse
}