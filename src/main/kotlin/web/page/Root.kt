package nl.vanalphenict.web.page

import io.github.allangomes.kotlinwind.css.kw
import io.ktor.server.html.Placeholder
import io.ktor.server.html.PlaceholderList
import io.ktor.server.html.Template
import io.ktor.server.html.TemplatePlaceholder
import io.ktor.server.html.each
import io.ktor.server.html.insert
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.UL
import kotlinx.html.article
import kotlinx.html.b
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.ul
import nl.vanalphenict.services.ThemeService

class Root {

    class LayoutTemplate(val themeService: ThemeService): Template<HTML> {
        val header = Placeholder<FlowContent>()
        val content = TemplatePlaceholder<ContentTemplate>()
        override fun HTML.apply() {
            head {
                // TODO: replace with build step to optimize css
                script { src = "assets/tailwindcss__browser/dist/index.global.js" }

            }
            body {
                style = kw.inline { flex.col.items_center.justify_center.gap[4]; }
                classes = setOf("bg-gray-900")

                div {

                    h1 {
                        classes = setOf("font-bold","leading-snug"," tracking-tight"," text-blue-600"," mx-auto"," w-full"," text-2xl"," lg:max-w-3xl"," lg:text-5xl")
                        insert(header)
                    }

                    p {
                        classes = setOf("font-bold","leading-snug"," tracking-tight"," text-blue-400"," mx-auto")
                        +"Theme:"
                    }
                    div{
                        attributes["hx-ext"] = "sse"
                        attributes["sse-connect"] = "/sse"
                        attributes["sse-swap"] = "switch_theme"
                        div {
                            renderThemes(themeService.themes, themeService.selectedTheme)
                        }
                    }

                    div {
                        renderActions()
                    }

                }

                val htmx = { e: String -> "assets/htmx.org/dist/$e" }
                script(src = htmx("htmx.js")) {}
                script(src = htmx("ext/json-enc.js")) {}
                script(src = "assets/htmx-ext-sse/dist/sse.js") {}
            }

        }
    }

    class ContentTemplate : Template<FlowContent> {
        val articleTitle = Placeholder<FlowContent>()
        val list = TemplatePlaceholder<ListTemplate>()
        override fun FlowContent.apply() {
            article {
                h2 {
                    insert(articleTitle)
                }
                insert(ListTemplate(), list)
            }
        }
    }

    class ListTemplate : Template<FlowContent> {
        val item = PlaceholderList<UL, FlowContent>()
        override fun FlowContent.apply() {
            if (!item.isEmpty()) {
                ul {
                    each(item) {
                        li {
                            if (it.first) {
                                b {
                                    insert(it)
                                }
                            } else {
                                insert(it)
                            }
                        }
                    }
                }
            }
        }
    }

}