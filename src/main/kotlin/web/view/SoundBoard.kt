package nl.vanalphenict.web.view

import com.janoz.discord.SampleService
import kotlinx.html.HtmlBlockTag
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.span

fun HtmlBlockTag.soundBoard(sampleService: SampleService) = div {
    sampleService.packs.forEach { pack ->
        h2 { +pack.name }
        pack.samples
            .sortedBy { it.name }
            .forEach { sample ->
                button {
                    attributes["hx-put"] = "/play"
                    attributes["hx-vals"] = "{\"sample\":\"${sample.id}\"}"
                    attributes["hx-swap"] = "none"

                    span { +sample.name }
                }
                +" "
            }
    }
}
