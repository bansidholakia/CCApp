package com.bd.composepagination.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String
)

data class UserDetail(
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val image: String?,
    val username: String?,
    val age: Int?,
    val gender: String?,
    val phone: String?,
    val birthDate: String?,
    val university: String?,
    val role: String?,
    val address: Address?
) {
    val fullName: String
        get() = listOfNotNull(firstName?.takeIf { it.isNotBlank() }, lastName?.takeIf { it.isNotBlank() })
            .joinToString(" ")
            .ifBlank { "User #$id" }

    fun detailRows(): List<Pair<String, String>> = buildList {
        username?.takeIf { it.isNotBlank() }?.let { add("Username" to it) }
        email?.takeIf { it.isNotBlank() }?.let { add("Email" to it) }
        phone?.takeIf { it.isNotBlank() }?.let { add("Phone" to it) }
        age?.let { add("Age" to it.toString()) }
        gender?.takeIf { it.isNotBlank() }?.let { add("Gender" to it) }
        birthDate?.takeIf { it.isNotBlank() }?.let { add("Birth date" to it) }
        university?.takeIf { it.isNotBlank() }?.let { add("University" to it) }
        role?.takeIf { it.isNotBlank() }?.let { add("Role" to it.replaceFirstChar(Char::titlecase)) }
        address?.formatted?.let { add("Address" to it) }
    }
}

data class Address(
    val address: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?
) {
    val formatted: String?
        get() = listOfNotNull(address, city, state, postalCode, country)
            .map(String::trim)
            .filter(String::isNotBlank)
            .joinToString(", ")
            .ifBlank { null }
}

fun User.matchesFullName(query: String): Boolean =
    "$firstName $lastName".contains(query.trim(), ignoreCase = true)

data class UserResponse(
    val users: List<User>,
    val skip: Int,
    val limit: Int,
    val total: Int
)
