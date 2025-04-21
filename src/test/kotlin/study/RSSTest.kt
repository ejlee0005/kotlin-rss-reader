package study

import org.junit.jupiter.api.Test
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class RSSTest {
    @Test
    fun rssTest() {
        val blogWoowahan = "https://techblog.woowahan.com/feed"
        val blogKakao = "https://tech.kakao.com/blog/feed"
        val blogKurly = "https://helloworld.kurly.com/feed"

        printRSSResult(blogWoowahan)
        printRSSResult(blogKakao)
        printRSSResult(blogKurly)
    }

    fun printRSSResult(url: String) {
        val factory = DocumentBuilderFactory.newInstance()
        val stream = factory.newDocumentBuilder().parse(URL(url).openStream())
        val items = stream.getElementsByTagName("item")
        val size = items.length

        println(">>>> url: $url")
        for (i in 0 until size) {
            val title = stream.getElementsByTagName("title").item(i).textContent
            val link = stream.getElementsByTagName("link").item(i).textContent
            val pubDate = stream.getElementsByTagName("pubDate").item(i).textContent
            println("title: $title, link: $link, pubDate: $pubDate")
        }
    }
}
