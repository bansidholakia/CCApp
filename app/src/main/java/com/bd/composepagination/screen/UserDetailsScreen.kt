package com.bd.composepagination.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bd.composepagination.model.UserDetail
import com.bd.composepagination.viewmodel.UserDetailUiState
import com.bd.composepagination.viewmodel.UserDetailViewModel

@Composable
fun UserDetailsScreen(
    viewModel: UserDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        DetailsTopBar(onBack = onBack)
        when (val state = uiState) {
            UserDetailUiState.Loading -> LoadingContent()
            is UserDetailUiState.Content -> UserDetailsContent(state.user)
            is UserDetailUiState.Error -> DetailsError(state.message, viewModel::loadUser)
        }
    }
}

@Composable
private fun DetailsTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(60.dp).background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp)
        ) {
            Text("‹", color = MaterialTheme.colorScheme.onPrimary, fontSize = 34.sp)
        }
        Text(
            text = "User details",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun UserDetailsContent(user: UserDetail) {
    val rows = user.detailRows()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 28.dp)
    ) {
        item {
            UserAvatar(user)
            Spacer(Modifier.height(16.dp))
            Text(user.fullName, color = MaterialTheme.colorScheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("#${user.id}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.height(28.dp))
        }
        items(rows.size) { index ->
            val (label, value) = rows[index]
            DetailRow(label, value)
        }
    }
}

@Composable
private fun UserAvatar(user: UserDetail) {
    Box(
        modifier = Modifier.size(112.dp).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val image = user.image?.takeIf { it.isNotBlank() }
        var hasImageError by remember(image) { mutableStateOf(false) }
        if (image == null || hasImageError) {
            Text(
                text = user.fullName.first().uppercase(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            AsyncImage(
                model = image,
                contentDescription = "Avatar of ${user.fullName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { hasImageError = true }
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(3.dp))
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun LoadingContent() = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun DetailsError(message: String, onRetry: () -> Unit) = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Text(message, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
