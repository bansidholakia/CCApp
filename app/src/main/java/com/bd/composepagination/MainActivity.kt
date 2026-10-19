package com.bd.composepagination

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bd.composepagination.model.User
import com.bd.composepagination.network.UserApi
import com.bd.composepagination.repository.UserRepository
import com.bd.composepagination.screen.UserScreen
import com.bd.composepagination.screen.UserDetailsScreen
import com.bd.composepagination.ui.theme.ComposePaginationTheme
import com.bd.composepagination.viewmodel.UserViewModel
import com.bd.composepagination.viewmodel.UserViewModelFactory
import com.bd.composepagination.viewmodel.UserDetailViewModel
import com.bd.composepagination.viewmodel.UserDetailViewModelFactory
import coil3.compose.AsyncImage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)

        val repository = UserRepository(api)

        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var themeOverride by rememberSaveable { mutableStateOf<Boolean?>(null) }
            val isDarkTheme = themeOverride ?: systemIsDark

            ComposePaginationTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "users") {
                    composable("users") {
                        val viewModel: UserViewModel = viewModel(
                            factory = UserViewModelFactory(repository)
                        )
                        UserScreen(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { themeOverride = !isDarkTheme },
                            onUserClick = { userId -> navController.navigate("user/$userId") }
                        )
                    }
                    composable(
                        route = "user/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
                        val viewModel: UserDetailViewModel = viewModel(
                            factory = UserDetailViewModelFactory(userId, repository)
                        )
                        UserDetailsScreen(viewModel = viewModel, onBack = navController::popBackStack)
                    }
                }
            }
        }
    }
}
