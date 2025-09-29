package nl.vanalphenict.services

class ThemeService(val themes: List<Theme>) {

    var selectedTheme: Theme = themes.first()

    fun selectTheme(themeId: String) {
        themes.find { it.id == themeId }?.let {
            println("theme selected: $themeId")
            selectedTheme = it
        }
    }

//    fun getThemes() = themes
}

data class Theme(val id: String, val title: String)

