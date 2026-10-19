package com.bd.composepagination.viewmodel

import com.bd.composepagination.model.User
import com.bd.composepagination.model.UserDetail
import com.bd.composepagination.model.UserResponse
import com.bd.composepagination.network.UserApi
import com.bd.composepagination.repository.UserRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `retries cancel the previous detail request and keep the latest result`() {
        val firstResponse = CompletableDeferred<UserDetail>()
        val secondResponse = CompletableDeferred<UserDetail>()
        val api = ControlledUserApi(firstResponse, secondResponse)
        val viewModel = UserDetailViewModel(7, UserRepository(api))

        viewModel.loadUser()
        secondResponse.complete(userDetail("Updated"))
        firstResponse.complete(userDetail("Stale"))

        assertTrue(api.firstRequestWasCancelled)
        assertEquals(
            UserDetailUiState.Content(userDetail("Updated")),
            viewModel.uiState.value
        )
    }

    @Test
    fun `load failure exposes an error state`() {
        val viewModel = UserDetailViewModel(7, UserRepository(FailingUserApi))

        assertEquals(
            UserDetailUiState.Error("Network unavailable"),
            viewModel.uiState.value
        )
    }

    private fun userDetail(firstName: String) = UserDetail(
        id = 7,
        firstName = firstName,
        lastName = "User",
        email = null,
        image = null,
        username = null,
        age = null,
        gender = null,
        phone = null,
        birthDate = null,
        university = null,
        role = null,
        address = null
    )

    private class ControlledUserApi(
        private val firstResponse: CompletableDeferred<UserDetail>,
        private val secondResponse: CompletableDeferred<UserDetail>
    ) : UserApi {
        private var requestCount = 0
        var firstRequestWasCancelled = false
            private set

        override suspend fun getUser(id: Int): UserDetail {
            val requestNumber = requestCount++
            val response = if (requestNumber == 0) firstResponse else secondResponse
            return try {
                response.await()
            } finally {
                if (requestNumber == 0) {
                    firstRequestWasCancelled = !currentCoroutineContext().isActive
                }
            }
        }

        override suspend fun getUsers(limit: Int, skip: Int): UserResponse = emptyUserResponse()

        override suspend fun searchUsers(query: String, limit: Int, skip: Int): UserResponse = emptyUserResponse()
    }

    private object FailingUserApi : UserApi {
        override suspend fun getUser(id: Int): UserDetail = throw IOException("Network unavailable")

        override suspend fun getUsers(limit: Int, skip: Int): UserResponse = emptyUserResponse()

        override suspend fun searchUsers(query: String, limit: Int, skip: Int): UserResponse = emptyUserResponse()
    }
}

private fun emptyUserResponse() = UserResponse(
    users = emptyList<User>(),
    skip = 0,
    limit = 0,
    total = 0
)
