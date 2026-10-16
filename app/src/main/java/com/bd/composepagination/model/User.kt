package com.bd.composepagination.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String
)

data class UserResponse(
    val users: List<User>,
    val skip: Int,
    val limit: Int,
    val total: Int
)