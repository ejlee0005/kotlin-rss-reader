import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import study.Channel
import study.Post
import java.time.Duration
import java.time.format.DateTimeFormatter
import kotlin.collections.take

private val channels =
    listOf(
        Channel("우아한 기술 블로그", "https://techblog.woowahan.com/feed"),
        Channel("카카오 기술 블로그", "https://tech.kakao.com/blog/feed"),
        Channel("컬리 기술 블로그", "https://helloworld.kurly.com/feed"),
    )

private var prevPosts: List<Post> = emptyList()
private var currentPosts: List<Post> = emptyList()

fun main() {
    runBlocking {
        launch(Dispatchers.IO) {
            while (isActive) {
                delay(Duration.ofMinutes(10).toMillis())
                updatePosts()
            }
        }

        launch {
            updatePosts()
            while (isActive) {
                println("검색어를 입력하세요 (없으면 전체 출력):")
                val keyword = readln()
                val filtered =
                    currentPosts.filter { keyword in it.title }
                        .sortedByDescending { it.pubDate }
                        .take(10)
                val result =
                    filtered.mapIndexed { idx, post ->
                        "[${idx + 1}] ${post.title} (${post.pubDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}) - ${post.link}\n"
                    }.joinToString("")
                println(result)
            }
        }
    }
}

private suspend fun updatePosts() {
    currentPosts = channels.posts()
    if (prevPosts.isNotEmpty()) {
        val result =
            currentPosts.filterNot { current ->
                prevPosts.any { prev -> current.title != prev.title }
            }.map { post ->
                "[NEW] ${post.title} (${post.pubDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}) - ${post.link}\n"
            }
        if (result.isNotEmpty()) println(result)
    }
    prevPosts = currentPosts
}

private suspend fun List<Channel>.posts(): List<Post> {
    return coroutineScope {
        this@posts.map {
            async { it.findPosts() }
        }.awaitAll().flatten()
    }
}
