package com.fameafrica.afm2026.ui.screen.infrastructure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.fameafrica.afm2026.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfrastructureScreen(
    onBack: () -> Unit,
    viewModel: InfrastructureViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Infrastructure",
                        style = FameTypography.titleLarge,
                        color = FameColors.WarmIvory
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FameColors.WarmIvory
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FameColors.StadiumBlack
                )
            )
        }
    ) { paddingValues ->
        // Infrastructure content
    }
}