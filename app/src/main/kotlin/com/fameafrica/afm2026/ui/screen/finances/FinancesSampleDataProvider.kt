package com.fameafrica.afm2026.ui.screen.finances

object FinancesSampleDataProvider {

    val sampleSponsors = listOf(
        SponsorUiModel(id = 1, name = "AfroMobile", type = "Main Sponsor", annualValue = 50_000_000, yearsRemaining = 3),
        SponsorUiModel(id = 2, name = "Baobab Energy", type = "Kit Sponsor", annualValue = 25_000_000, yearsRemaining = 5),
        SponsorUiModel(id = 3, name = "Kente Weavers", type = "Sleeve Sponsor", annualValue = 10_000_000, yearsRemaining = 2),
        SponsorUiModel(id = 4, name = "Savannah Juices", type = "Official Partner", annualValue = 5_000_000, yearsRemaining = 1)
    )

    val sampleProfitLossHistory = listOf(
        ProfitLossEntry(label = "2020", amount = -5_000_000),
        ProfitLossEntry(label = "2021", amount = 1_200_000),
        ProfitLossEntry(label = "2022", amount = 8_500_000),
        ProfitLossEntry(label = "2023", amount = -2_100_000),
        ProfitLossEntry(label = "2024", amount = 15_000_000)
    )

    val sampleFinancialSummary = FinancialSummaryUiModel(
        revenue = 250_000_000,
        expenses = 235_000_000,
        profitLoss = 15_000_000,
        bankBalance = 85_000_000,
        isProfitable = true
    )

    val sampleUiStateRichProfitable = FinancesUiState(
        isLoading = false,
        financialSummary = sampleFinancialSummary,
        budget = 120_000_000,
        bankBalance = 85_000_000,
        wageBill = 150_000_000,
        financialTier = "Rich",
        financialHealth = "Healthy",
        isProfitable = true,
        revenueBreakdown = mapOf(
            "Sponsorship" to 85_000_000,
            "Broadcasting" to 70_000_000,
            "Matchday" to 40_000_000,
            "Merchandise" to 25_000_000,
            "Prize Money" to 30_000_000
        ),
        expenseBreakdown = mapOf(
            "Player Wages" to 150_000_000,
            "Staff Wages" to 20_000_000,
            "Transfer Spending" to 50_000_000,
            "Infrastructure" to 10_000_000,
            "Operational" to 5_000_000
        ),
        profitLossHistory = sampleProfitLossHistory,
        sponsors = sampleSponsors,
        leagueAverageRevenue = 150_000_000,
        leagueHighestRevenue = 400_000_000
    )

    val sampleGameContext = GameContextState(season = "2024/25")
}
