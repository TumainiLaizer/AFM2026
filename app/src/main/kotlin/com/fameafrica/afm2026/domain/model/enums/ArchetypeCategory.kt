package com.fameafrica.afm2026.domain.model.enums

enum class PlayerArchetype(val value: String) {
    DYNAMIC_FORWARD("DYNAMIC_FORWARD"),
    STRATEGIC_MIDFIELDER("STRATEGIC_MIDFIELDER"),
    RESILIENT_DEFENDER("RESILIENT_DEFENDER"),
    INSPIRATIONAL_CAPTAIN("INSPIRATIONAL_CAPTAIN"),
    COMPLETE_FORWARD("COMPLETE_FORWARD"),
    BOX_TO_BOX("BOX_TO_BOX"),
    SWEEPING_KEEPER("SWEEPING_KEEPER"),
    TRADITIONAL_STRIKER("TRADITIONAL_STRIKER"),
    WING_WIZARD("WING_WIZARD"),
    ANCHOR_MAN("ANCHOR_MAN"),
    REGISTA("REGISTA"),
    LIBERO("LIBERO");

    companion object {
        fun fromString(value: String): PlayerArchetype? {
            return values().find { it.value == value }
        }
    }
}

enum class PlayerTrait(val value: String) {
    BOLD("BOLD"),
    CONFIDENT("CONFIDENT"),
    CREATIVE("CREATIVE"),
    FORTHRIGHT("FORTHRIGHT"),
    ADAPTABLE("ADAPTABLE"),
    TEAM_ORIENTED("TEAM_ORIENTED"),
    DISCIPLINED("DISCIPLINED"),
    RESILIENT("RESILIENT"),
    COMPOSED("COMPOSED"),
    EMOTIONALLY_STABLE("EMOTIONALLY_STABLE"),
    SOCIALLY_WARM("SOCIALLY_WARM"),
    VERSATILE("VERSATILE"),
    INTELLIGENT("INTELLIGENT"),
    ENERGETIC("ENERGETIC"),
    DETERMINED("DETERMINED"),
    BRAVE("BRAVE"),
    DECISIVE("DECISIVE");

    companion object {
        fun fromString(value: String): PlayerTrait? {
            return values().find { it.value == value }
        }
    }
}// ArchetypeCategory.kt
