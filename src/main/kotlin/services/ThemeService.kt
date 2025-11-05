package nl.vanalphenict.services

import io.github.oshai.kotlinlogging.KotlinLogging

class ThemeService(configs: List<SampleMapper>, val announcementHandler: AnnouncementHandler) {

    private val log = KotlinLogging.logger {}

    var themes: List<Theme> =
        configs.sortedBy { it.name }.mapIndexed { index, mapper -> Theme(index, mapper.name) }

    val mapped = configs.associateBy { config -> themes.find { it.title == config.name }!!.id }

    var selectedTheme: Theme = themes.first()

    fun selectTheme(themeId: Int) {
        mapped[themeId]?.let { it ->
            selectedTheme = themes.find { themeId == it.id }!!
            log.trace { "theme selected: ${selectedTheme.title}" }
            announcementHandler.replaceMapping(it)
        }
    }
}

data class Theme(val id: Int, val title: String)
