package nl.vanalphenict.utility

import java.awt.Color

class ColorUtils {
    companion object {
        fun Color.toCssGradient() =
            "linear-gradient(0deg,${this.darker().toHexString()} 0%,${this.brighter().toHexString()} 100%)"

        fun Color.toHexString() = String.format("#%02x%02x%02x", red, green, blue)

        fun String.hexToColor(): Color {
            if (this.isBlank() || this.length < 6) return GREY
            return try {
                Color(
                    this.substring(0, 2).toInt(16),
                    this.substring(2, 4).toInt(16),
                    this.substring(4, 6).toInt(16),
                )
            } catch (_: Exception) {
                GREY
            }
        }

        val ORANGE = Color(194, 100, 24)
        val BLUE = Color(24, 115, 255)
        val GREY = Color(128, 128, 128)
        val DARK_GREY = Color(229, 229, 229)
    }
}
