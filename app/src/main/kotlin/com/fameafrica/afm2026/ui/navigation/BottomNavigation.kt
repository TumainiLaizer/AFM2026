package com.fameafrica.afm2026.ui.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fameafrica.afm2026.R
import com.fameafrica.afm2026.ui.theme.Dimensions
import com.fameafrica.afm2026.ui.theme.FameColors

/**
 * FAME Africa™ Bottom Navigation Bar
 * Active tab: Pitch Green icon + Gold underline
 * Inactive: Soft parchment
 */
@Composable
fun FameBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.bottomNavHeight)
            .background(FameColors.StadiumBlack),
        containerColor = FameColors.StadiumBlack,
        tonalElevation = 0.dp
    ) {
        Screen.mainTabs.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) FameColors.PitchGreen else FameColors.MutedParchment,
                                modifier = Modifier.size(Dimensions.bottomNavIconSize)
                            )

                            // Gold underline for active tab
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(2.dp)
                                        .background(
                                            color = FameColors.ChampionsGold,
                                            shape = RoundedCornerShape(1.dp)
                                        )
                                        .padding(top = 2.dp)
                                )
                            }
                        }
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) FameColors.WarmIvory else FameColors.MutedParchment
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FameColors.PitchGreen,
                    selectedTextColor = FameColors.WarmIvory,
                    unselectedIconColor = FameColors.MutedParchment,
                    unselectedTextColor = FameColors.MutedParchment,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

/**
 * Top App Bar with reputation-based gold shimmer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FameTopAppBar(
    title: String,
    reputationLevel: String = "Local",
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (reputationLevel) {
        "African Legend" -> FameColors.AfricanLegendEmerald
        "Continental" -> FameColors.ContinentalGold
        "National" -> FameColors.NationalSilver
        "Local" -> FameColors.LocalBronze
        else -> FameColors.PitchGreen
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = FameColors.WarmIvory
            )
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = FameColors.WarmIvory
                )
            }
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        // Unread count badge
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(FameColors.AfroSunOrange, CircleShape)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = FameColors.WarmIvory
                    )
                }
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = FameColors.WarmIvory
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = FameColors.StadiumBlack,
            titleContentColor = FameColors.WarmIvory,
            actionIconContentColor = FameColors.WarmIvory,
            navigationIconContentColor = FameColors.WarmIvory
        ),
        modifier = modifier
    )
}