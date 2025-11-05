package nl.vanalphenict.utility

import java.awt.Color

class ColorUtils {
    companion object {
        fun Color.toHexString() = String.format("#%02x%02x%02x", red, green, blue)
    }
}
