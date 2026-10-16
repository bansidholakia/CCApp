package com.bd.composepagination.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.bd.composepagination.model.User
import com.bd.composepagination.viewmodel.UserViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UserScreen(
    viewModel: UserViewModel
) {

    val users =
        viewModel.users.collectAsLazyPagingItems()

    val isRefreshing =
        users.loadState.refresh is LoadState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Users (${users.itemCount})"
                    )
                }
            )
        }
    ) {
        innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,

            onRefresh = {
                users.refresh()
            },

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(
                    count = users.itemCount,
                    key = users.itemKey { user ->
                        user.id
                    }
                ) { index ->

                    val user = users[index]

                    if (user != null) {

                        UserItem(user)

                        HorizontalDivider()
                    }
                }

                /*
                 * Loading next page
                 */
                if (users.loadState.append is LoadState.Loading) {

                    item {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator()
                        }
                    }
                }

                /*
                 * Error while loading next page
                 */
                if (users.loadState.append is LoadState.Error) {

                    item {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {

                            Text(
                                text = "Failed to load more users"
                            )

                            Button(
                                onClick = {
                                    users.retry()
                                }
                            ) {

                                Text("Retry")
                            }
                        }
                    }
                }
            }

            /*
             * Initial loading error
             */
            if (
                users.loadState.refresh is LoadState.Error &&
                users.itemCount == 0
            ) {

                val error =
                    users.loadState.refresh as LoadState.Error

                ErrorScreen(
                    message =
                        error.error.message
                            ?: "Something went wrong",
                    onRetry = {
                        users.retry()
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = message
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = onRetry
            ) {

                Text("Retry")
            }
        }
    }
}


@Composable
fun UserItem(
    user: User
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {

        AsyncImage(
            model = user.image,
            contentDescription = "Avatar of ${user.firstName} ${user.lastName}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column {

            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}