import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import study.Channel
import study.Post
import java.time.format.DateTimeFormatter
import kotlin.collections.sortedByDescending
import kotlin.collections.take

fun main() =
    runBlocking {
        val channels =
            listOf(
                Channel("우아한 기술 블로그", "https://techblog.woowahan.com/feed"),
                Channel("카카오 기술 블로그", "https://tech.kakao.com/blog/feed"),
                Channel("컬리 기술 블로그", "https://helloworld.kurly.com/feed"),
            )

        while (true) {
            println("검색어를 입력하세요 (없으면 전체 출력):")
            val keyWord = readln()
            val posts =
                channels.posts().filter { keyWord in it.title }
                    .sortedByDescending { it.pubDate }
                    .take(8)

            val result =
                posts.mapIndexed { idx, post ->
                    "[${idx + 1}] ${post.title} (${post.pubDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}) - ${post.link}\n"
                }.joinToString("")
            println(result)
        }
    }

private suspend fun List<Channel>.posts(): List<Post> {
    return coroutineScope {
        this@posts.map {
            async { it.findPosts() }
        }.awaitAll().flatten()
    }
}
