package com.fameafrica.afm2026.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * FAME Africa™ Official Color System
 * Heritage + Power + Stadium Atmosphere + Continental Unity
 * Modern African football authority, not clichés
 */
object FameColors {

    // ============ PRIMARY BRAND COLORS (DO NOT CHANGE) ============

    /** Pitch Authority Green - Headers, active tabs, league tables */
    val PitchGreen = Color(0xFF0F5A36)

    /** Champions Gold - Trophies, reputation stars, board approval */
    val ChampionsGold = Color(0xFFD4A017)

    /** Midnight Stadium Black - Main background, match engine, premium feel */
    val StadiumBlack = Color(0xFF0B0B0F)


    // ============ SECONDARY CULTURAL PALETTE ============

    /** Afro-Sun Orange - Goals, breaking news, transfer deadlines */
    val AfroSunOrange = Color(0xFFFF7A00)

    /** Kente Passion Red - Cards, board tension, rivalry matches */
    val KenteRed = Color(0xFF9E1B1B)

    /** Baobab Brown - History, heritage, trophy cabinet, youth academy */
    val BaobabBrown = Color(0xFF6B4F2A)


    // ============ TEXT COLORS (Never pure white) ============

    /** Warm Ivory - Primary text */
    val WarmIvory = Color(0xFFF2EAD3)

    /** Muted Parchment - Secondary text */
    val MutedParchment = Color(0xFFC8BFAE)

    /** Disabled Text - 40% opacity of Muted Parchment */
    val DisabledText = Color(0xFF8E8578)


    // ============ MATCH DAY PITCH ============

    /** Match Pitch Green - Brighter than UI green for pitch view */
    val MatchPitch = Color(0xFF1B7A45)


    // ============ REPUTATION PROGRESSION ============

    val LocalBronze = Color(0xFFB87333)
    val NationalSilver = Color(0xFFC0C0C0)
    val ContinentalGold = ChampionsGold
    val AfricanLegendEmerald = Color(0xFF00A86B)


    // ============ SEMANTIC COLORS ============

    val Success = Color(0xFF2E7D32)
    val Warning = AfroSunOrange
    val Error = KenteRed
    val Info = Color(0xFF0288D1)


    // ============ BACKGROUND VARIANTS ============

    val SurfaceDark = Color(0xFF1A1A20)
    val SurfaceMedium = Color(0xFF25252D)
    val SurfaceLight = Color(0xFF303038)

    /** Light theme background (optional) */
    val LightSand = Color(0xFFF4E9D8)
    val LightCard = Color(0xFFFFFFFF)
    val LightBorder = BaobabBrown.copy(alpha = 0.2f)
}