package study

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

class Channel(
    private val title: String,
    private val link: String,
) {
    suspend fun findPosts(): List<Post> =
        withContext(Dispatchers.IO) {
            val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val document = builder.parse(link)
            val channel = document.getElementsByTagName("channel").item(0)

            val items =
                List(channel.childNodes.length) { channel.childNodes.item(it) }
                    .filterIsInstance<Element>()
                    .filter { it.tagName == "item" }

            items.map {
                Post(
                    it.textOf("title"),
                    it.textOf("link"),
                    LocalDateTime.parse(it.textOf("pubDate"), DateTimeFormatter.RFC_1123_DATE_TIME),
                )
            }
        }

    private fun Element.textOf(tagName: String): String {
        return getElementsByTagName(tagName).item(0)?.textContent.orEmpty()
    }
}
