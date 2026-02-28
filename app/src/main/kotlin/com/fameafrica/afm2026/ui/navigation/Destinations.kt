package com.fameafrica.afm2026.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * FAME Africa™ Navigation Destinations
 * 5 main tabs + nested navigation
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null
) {

    // ============ MAIN TABS ============

    object Dashboard : Screen(
        route = "dashboard",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Squad : Screen(
        route = "squad",
        title = "Squad",
        icon = Icons.Default.Group
    )

    object Transfers : Screen(
        route = "transfers",
        title = "Transfers",
        icon = Icons.Default.SwapHoriz
    )

    object Club : Screen(
        route = "club",
        title = "Club",
        icon = Icons.Default.Business
    )

    object World : Screen(
        route = "world",
        title = "World",
        icon = Icons.Default.Public
    )

    // ============ NESTED SCREENS ============

    // Squad nested screens
    object PlayerDetail : Screen(
        route = "squad/player/{playerId}",
        title = "Player",
        icon = Icons.Default.Person
    )

    object Tactics : Screen(
        route = "tactics",
        title = "Tactics",
        icon = Icons.Default.SportsSoccer
    )

    object Training : Screen(
        route = "training",
        title = "Training",
        icon = Icons.Default.FitnessCenter
    )

    // Transfers nested screens
    object Scout : Screen(
        route = "scout",
        title = "Scout",
        icon = Icons.Default.Visibility
    )

    object Negotiation : Screen(
        route = "negotiation/{transferId}",
        title = "Negotiation",
        icon = Icons.Default.AttachMoney
    )

    // Club nested screens
    object Finances : Screen(
        route = "finances",
        title = "Finances",
        icon = Icons.Default.TrendingUp
    )

    object Infrastructure : Screen(
        route = "infrastructure",
        title = "Infrastructure",
        icon = Icons.Default.Build
    )

    object History : Screen(
        route = "history",
        title = "History",
        icon = Icons.Default.History
    )

    // World nested screens
    object LeagueTable : Screen(
        route = "league/{leagueName}",
        title = "League",
        icon = Icons.Default.FormatListNumbered
    )

    object CupDraw : Screen(
        route = "cup/{cupName}",
        title = "Cup",
        icon = Icons.Default.EmojiEvents
    )

    object Match : Screen(
        route = "match/{matchId}",
        title = "Match",
        icon = Icons.Default.SportsScore
    )

    // ============ UTILITY ============

    fun withArgs(vararg args: String): String {
        return buildString {
            var currentRoute = route
            args.forEach { arg ->
                currentRoute = currentRoute.replaceFirst("\\{[^}]+}".toRegex(), arg)
            }
            append(currentRoute)
        }
    }

    companion object {
        val mainTabs = listOf(Dashboard, Squad, Transfers, Club, World)
    }
}
