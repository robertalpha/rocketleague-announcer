package nl.vanalphenict.page

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
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.ul

class Root {

    class LayoutTemplate: Template<HTML> {
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
                        insert(header)
                    }
                    insert(ContentTemplate(), content)

                }

                script { src = "assets/htmx.org/dist/htmx.js" }
            }

        }
    }

    class ContentTemplate : Template<FlowContent> {
        val articleTitle = Placeholder<FlowContent>()
        val articleText = Placeholder<FlowContent>()
        val list = TemplatePlaceholder<ListTemplate>()
        override fun FlowContent.apply() {
            article {
                h2 {
                    insert(articleTitle)
                }
                p {
                    insert(articleText)
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