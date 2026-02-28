package com.fameafrica.afm2026.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * FAME Africa™ Shape System
 * Clean, modern, slightly rounded for approachability
 */
val FameShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Individual shapes for specific components
object ComponentShapes {
    val card = RoundedCornerShape(12.dp)
    val button = RoundedCornerShape(8.dp)
    val chip = RoundedCornerShape(16.dp) // Fully rounded for chips
    val bottomNavItem = RoundedCornerShape(0.dp) // No rounding for bottom nav
    val playerCard = RoundedCornerShape(16.dp)
    val matchCard = RoundedCornerShape(12.dp)
    val badge = RoundedCornerShape(50.dp) // Fully rounded
    val pitch = RoundedCornerShape(0.dp) // No rounding for pitch
}// Shapes.kt
