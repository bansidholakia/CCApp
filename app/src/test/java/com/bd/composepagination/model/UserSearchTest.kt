package com.bd.composepagination.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserSearchTest {
    private val user = User(
        id = 1,
        firstName = "Emily",
        lastName = "Johnson",
        email = "emily@example.com",
        image = ""
    )

    @Test
    fun `matchesFullName ignores case and whitespace`() {
        assertTrue(user.matchesFullName("  EMILY john  "))
    }

    @Test
    fun `matchesFullName does not match an email-only query`() {
        assertFalse(user.matchesFullName("example.com"))
    }
}
