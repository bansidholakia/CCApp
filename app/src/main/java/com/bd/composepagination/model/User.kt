package com.bd.composepagination.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String
)

fun User.matchesFullName(query: String): Boolean =
    "$firstName $lastName".contains(query.trim(), ignoreCase = true)

data class UserResponse(
    val users: List<User>,
    val skip: Int,
    val limit: Int,
    val total: Int
)
