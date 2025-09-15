package nl.vanalphenict.model

enum class Announcement {
    HUMMILIATION,
    RETALIATION,
    PENTA_KILL,
    QUAD_KILL,
    TRIPLE_KILL,
    DOUBLE_KILL,
    FIRST_BLOOD,
    NOTHING;

    fun combine(other: Announcement): Announcement {
        if (this.ordinal < other.ordinal)
            return this
        else
            return other
    }
}