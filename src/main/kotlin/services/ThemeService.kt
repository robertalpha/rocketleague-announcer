package nl.vanalphenict.services

class ThemeService(configs: List<SampleMapper>, val announcementHandler: AnnouncementHandler) {

    var themes: List<Theme> = configs.sortedBy { it.name }.mapIndexed{ index, mapper -> Theme(index,mapper.name) }

    val mapped = configs.associateBy { config ->  themes.find {
        it.title == config.name
    }!!.id}

    var selectedTheme: Theme = themes.first()

    fun selectTheme(themeId: Int) {
        mapped[themeId]?.let { it ->
            selectedTheme = themes.find { themeId==it.id }!!
            println("theme selected: ${selectedTheme.title}")
            announcementHandler.replaceMapping(it)
        }
    }
}

data class Theme(val id: Int, val title: String)

