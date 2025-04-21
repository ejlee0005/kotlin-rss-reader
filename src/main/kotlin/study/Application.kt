import study.RSSModel
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

val blogWoowahan = "https://techblog.woowahan.com/feed"
val blogKakao = "https://tech.kakao.com/blog/feed"
val blogKurly = "https://helloworld.kurly.com/feed"

val rssList = mutableListOf<RSSModel>()

fun main() {
    try {
        loadData()
        val result = inputKeyWord()
        showResult(result)
    } catch (e: IllegalArgumentException) {
        println("[ERROR] ${e.message}")
    } catch (e: IllegalStateException) {
        println("[ERROR] ${e.message}")
    }
}

fun loadData() {
    loadRSSData(blogWoowahan)
    loadRSSData(blogKakao)
    loadRSSData(blogKurly)
}

fun loadRSSData(url: String) {
    val factory = DocumentBuilderFactory.newInstance()
    val stream = factory.newDocumentBuilder().parse(URL(url).openStream())
    val items = stream.getElementsByTagName("item")
    val size = items.length

    for (i in 0 until size) {
        val title = stream.getElementsByTagName("title").item(i).textContent
        val link = stream.getElementsByTagName("link").item(i).textContent
        val pubDate = parsePubData(stream.getElementsByTagName("pubDate").item(i).textContent)

        rssList.add(RSSModel(title, link, pubDate))
    }
}

fun parsePubData(pubDateStr: String): Date {
    val parseDateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    val parseDate = parseDateFormat.parse(pubDateStr)
    return parseDate
}

fun inputKeyWord(): List<RSSModel> {
    println("검색어를 입력하세요 (없으면 전체 출력):")
    val input = readLine()
    return if (input == null) {
        searchAll()
    } else if (input.isEmpty()) {
        searchAll()
    } else {
        search(input)
    }
}

fun searchAll(): List<RSSModel> {
    return rssList.sortedBy { it.pubDate }
}

fun search(input: String): List<RSSModel> {
    return rssList.filter { it.title.contains(input) }.sortedByDescending { it.pubDate }
}

fun showResult(list: List<RSSModel>) {
    list.forEachIndexed { idx, model ->
        println("[$idx] ${model.title} (${parsePubDataFormat(model.pubDate)}) - ${model.link}")
    }
}

fun parsePubDataFormat(date: Date): String {
    val formatDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val formatDateText = formatDateFormat.format(date)
    return formatDateText
}
