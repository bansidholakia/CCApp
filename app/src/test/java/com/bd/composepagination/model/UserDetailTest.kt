package com.bd.composepagination.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserDetailTest {
    @Test
    fun `detail rows include only available user information`() {
        val user = UserDetail(
            id = 7,
            firstName = "Ada",
            lastName = null,
            email = "",
            image = null,
            username = "ada",
            age = null,
            gender = "female",
            phone = null,
            birthDate = null,
            university = "",
            role = "admin",
            address = Address(address = null, city = "Toronto", state = null, postalCode = null, country = "Canada")
        )

        assertEquals("Ada", user.fullName)
        assertEquals(
            listOf("Username" to "ada", "Gender" to "female", "Role" to "Admin", "Address" to "Toronto, Canada"),
            user.detailRows()
        )
    }

    @Test
    fun `detail model falls back to id when name and address are unavailable`() {
        val user = UserDetail(
            id = 7,
            firstName = " ",
            lastName = null,
            email = null,
            image = null,
            username = null,
            age = null,
            gender = null,
            phone = null,
            birthDate = null,
            university = null,
            role = null,
            address = Address(null, null, null, null, null)
        )

        assertEquals("User #7", user.fullName)
        assertFalse(user.detailRows().any { it.first == "Address" })
        assertTrue(user.detailRows().isEmpty())
    }
}
