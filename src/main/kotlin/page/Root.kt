package nl.vanalphenict.page

import com.sun.org.apache.bcel.internal.generic.Select
import io.github.allangomes.kotlinwind.css.kw
import io.ktor.server.html.Placeholder
import io.ktor.server.html.PlaceholderList
import io.ktor.server.html.Template
import io.ktor.server.html.TemplatePlaceholder
import io.ktor.server.html.each
import io.ktor.server.html.insert
import java.awt.Color.blue
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.UL
import kotlinx.html.article
import kotlinx.html.b
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.role
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


                div {

                    h1 {
                        classes = setOf("font-bold","leading-snug"," tracking-tight"," text-blue-600"," mx-auto"," w-full"," text-2xl"," lg:max-w-3xl"," lg:text-5xl")

                        insert(header)
                    }

                    p {
                        +"Select theme:"
                    }
                    div {
                        renderThemes(themeService.themes,themeService.selectedTheme.id)
                    }



                }

                script { src = "assets/htmx.org/dist/htmx.js" }
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