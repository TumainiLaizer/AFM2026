package com.fameafrica.afm2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fameafrica.afm2026.ui.screen.dashboard.DashboardScreen
import com.fameafrica.afm2026.ui.screen.squad.SquadScreen
import com.fameafrica.afm2026.ui.screen.transfers.TransfersScreen
import com.fameafrica.afm2026.ui.screen.club.ClubScreen
import com.fameafrica.afm2026.ui.screen.world.WorldScreen
import com.fameafrica.afm2026.ui.screen.squad.PlayerDetailScreen
import com.fameafrica.afm2026.ui.screen.tactics.TacticsScreen
import com.fameafrica.afm2026.ui.screen.training.TrainingScreen
import com.fameafrica.afm2026.ui.screen.scout.ScoutScreen
import com.fameafrica.afm2026.ui.screen.negotiation.NegotiationScreen
import com.fameafrica.afm2026.ui.screen.finances.FinancesScreen
import com.fameafrica.afm2026.ui.screen.infrastructure.InfrastructureScreen
import com.fameafrica.afm2026.ui.screen.history.HistoryScreen
import com.fameafrica.afm2026.ui.screen.league.LeagueTableScreen
import com.fameafrica.afm2026.ui.screen.cup.CupDrawScreen
import com.fameafrica.afm2026.ui.screen.match.MatchScreen

@Composable
fun FameNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ============ MAIN TABS ============

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToMatch = { matchId ->
                    navController.navigate(Screen.Match.withArgs(matchId.toString()))
                },
                onNavigateToSquad = {
                    navController.navigate(Screen.Squad.route)
                }
            )
        }

        composable(Screen.Squad.route) {
            SquadScreen(
                onPlayerClick = { playerId ->
                    navController.navigate(Screen.PlayerDetail.withArgs(playerId.toString()))
                },
                onTacticsClick = {
                    navController.navigate(Screen.Tactics.route)
                },
                onTrainingClick = {
                    navController.navigate(Screen.Training.route)
                }
            )
        }

        composable(Screen.Transfers.route) {
            TransfersScreen(
                onScoutClick = {
                    navController.navigate(Screen.Scout.route)
                },
                onNegotiationClick = { transferId ->
                    navController.navigate(Screen.Negotiation.withArgs(transferId.toString()))
                }
            )
        }

        composable(Screen.Club.route) {
            ClubScreen(
                onFinancesClick = {
                    navController.navigate(Screen.Finances.route)
                },
                onInfrastructureClick = {
                    navController.navigate(Screen.Infrastructure.route)
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(Screen.World.route) {
            WorldScreen(
                onLeagueClick = { leagueName ->
                    navController.navigate(Screen.LeagueTable.withArgs(leagueName))
                },
                onCupClick = { cupName ->
                    navController.navigate(Screen.CupDraw.withArgs(cupName))
                }
            )
        }

        // ============ NESTED SCREENS ============

        composable(Screen.PlayerDetail.route) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId")?.toIntOrNull() ?: 0
            PlayerDetailScreen(
                playerId = playerId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Tactics.route) {
            TacticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Training.route) {
            TrainingScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Scout.route) {
            ScoutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Negotiation.route) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId")?.toIntOrNull() ?: 0
            NegotiationScreen(
                transferId = transferId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Finances.route) {
            FinancesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Infrastructure.route) {
            InfrastructureScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LeagueTable.route) { backStackEntry ->
            val leagueName = backStackEntry.arguments?.getString("leagueName") ?: ""
            LeagueTableScreen(
                leagueName = leagueName,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CupDraw.route) { backStackEntry ->
            val cupName = backStackEntry.arguments?.getString("cupName") ?: ""
            CupDrawScreen(
                cupName = cupName,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Match.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId")?.toIntOrNull() ?: 0
            MatchScreen(
                matchId = matchId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}