package com.fameafrica.afm2026.data.database.entities

enum class PlayerPosition(val value: String) {
    GK("GK"),
    CB("CB"), LB("LB"), RB("RB"), SW("SW"), LWB("LWB"), RWB("RWB"),
    CDM("CDM"), CM("CM"), CAM("CAM"), LM("LM"), RM("RM"),
    LW("LW"), RW("RW"), ST("ST"), CF("CF")
}

enum class PlayerPersonality(val value: String) {
    PROFESSIONAL("PROFESSIONAL"),
    AGGRESSIVE("AGGRESSIVE"),
    LOYAL("LOYAL"),
    MEDIA_FRIENDLY("MEDIA_FRIENDLY"),
    MEDIA_HOSTILE("MEDIA_HOSTILE"),
    NATURAL_LEADER("NATURAL_LEADER"),
    AMBITIOUS("AMBITIOUS"),
    TEMPERAMENTAL("TEMPERAMENTAL"),
    TEAM_PLAYER("TEAM_PLAYER"),
    INDIVIDUALIST("INDIVIDUALIST")
}

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
    LIBERO("LIBERO")
}

enum class InjuryStatus(val value: String) {
    HEALTHY("HEALTHY"),
    MINOR_INJURY("MINOR_INJURY"),
    MAJOR_INJURY("MAJOR_INJURY"),
    RECOVERING("RECOVERING")
}

enum class TransferStatus(val value: String) {
    NOT_LISTED("NOT_LISTED"),
    AVAILABLE("AVAILABLE"),
    LOAN_LISTED("LOAN_LISTED"),
    PENDING("Pending"),
    NEGOTIATING("Negotiating"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class WorkRate(val value: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    VERY_HIGH("VERY_HIGH")
}

enum class TransferType(val value: String) {
    BUY("Buy"),
    LOAN("Loan"),
    FREE("Free")
}