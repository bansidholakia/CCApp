package com.bd.composepagination.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.bd.composepagination.model.User
import com.bd.composepagination.viewmodel.UserViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UserScreen(
    viewModel: UserViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val users = viewModel.users.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsState()
    var isUserRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(users.loadState.refresh) {
        if (users.loadState.refresh !is LoadState.Loading) {
            isUserRefreshing = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        InboxHeader(
            query = query,
            isDarkTheme = isDarkTheme,
            onQueryChange = viewModel::updateSearchQuery,
            onThemeToggle = onThemeToggle
        )
        PullToRefreshBox(
            isRefreshing = isUserRefreshing,
            onRefresh = {
                isUserRefreshing = true
                users.refresh()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                items(
                    count = users.itemCount,
                    key = users.itemKey { user ->
                        user.id
                    }
                ) { index ->

                    val user = users[index]

                    if (user != null) {
                        UserItem(user)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    }
                }

                /*
                 * Loading next page
                 */
                if (users.loadState.append is LoadState.Loading) {

                    item {

                        Box(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                    }
                }

                /*
                 * Error while loading next page
                 */
                if (users.loadState.append is LoadState.Error) {

                    item {

                        PagingError(onRetry = users::retry)
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

                ErrorScreen(error.error.message ?: "Something went wrong", users::retry)
            } else if (
                query.isNotBlank() &&
                users.itemCount == 0 &&
                users.loadState.refresh !is LoadState.Loading
            ) {
                EmptySearchResults(query = query)
            }
        }
    }
}

@Composable
private fun InboxHeader(
    query: String,
    isDarkTheme: Boolean,
    onQueryChange: (String) -> Unit,
    onThemeToggle: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(MaterialTheme.colorScheme.primary).padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DrawerIcon(modifier = Modifier.size(32.dp))
            Text(
                text = "Inbox", color = MaterialTheme.colorScheme.onPrimary, fontSize = 17.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center
            )
            IconButton(onClick = onThemeToggle, modifier = Modifier.size(32.dp)) {
                Text(
                    text = if (isDarkTheme) "☀" else "☾",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    fontSize = 21.sp
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(52.dp).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⌕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 27.sp)
            Spacer(Modifier.width(13.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text("Search", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(32.dp)) {
                    Text("×", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
private fun EmptySearchResults(query: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "No users found for \"$query\"",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DrawerIcon(modifier: Modifier = Modifier) {
    val iconColor = MaterialTheme.colorScheme.onPrimary
    Canvas(modifier = modifier.padding(7.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val inset = 1.dp.toPx()
        val centerY = size.height / 2
        drawLine(iconColor, start = androidx.compose.ui.geometry.Offset(inset, centerY - 5.dp.toPx()), end = androidx.compose.ui.geometry.Offset(size.width - inset, centerY - 5.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(iconColor, start = androidx.compose.ui.geometry.Offset(inset, centerY), end = androidx.compose.ui.geometry.Offset(size.width * 0.68f, centerY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        drawLine(iconColor, start = androidx.compose.ui.geometry.Offset(inset, centerY + 5.dp.toPx()), end = androidx.compose.ui.geometry.Offset(size.width - inset, centerY + 5.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(text = message, color = MaterialTheme.colorScheme.onSurface)

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
private fun PagingError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Failed to load more users", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun UserItem(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth().height(82.dp).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.image,
            contentDescription = "Avatar of ${user.firstName} ${user.lastName}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
        )

        Spacer(
            modifier = Modifier.width(13.dp)
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = user.email, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Text("#${user.id}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    }
}
