package study

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * 먼저 역으로 클라이언트 입장에서 호출해야할 코드를 작성한 다음에
 * DSL을 구현을 하는 것을 추천함
 */
class DSLTest {
    @ValueSource(strings = ["홍길동", "김철수"]) // table driven test
    @ParameterizedTest
    fun name(name: String) {
        val person =
            introduce {
                name(name) // 1) this.가 생략 2) 실제 name 함수가 있거나
            }
        person.name shouldBe "홍길동" // kotest. 중위 표시?
    }

    @Test
    fun skillsTest() {
        val person =
            introduce {
                skills {
                    soft("A passion for problem solving")
                    soft("Good communication skills")
                    hard("Kotlin")
                }
            }
        person.skills?.soft shouldBe listOf("A passion for problem solving", "Good communication skills")
        person.skills?.hard shouldBe listOf("Kotlin")
    }
}

private fun introduce(block: PersonBuilder.() -> Unit): Person {
    return PersonBuilder().apply(block).build() // 인자로 들어온 함수를 호출할 수 있음
}

class PersonBuilder(var name: String = "", var company: String = "", var skill: Skill? = null) {
    fun name(name: String) {
        this.name = name
    }

    fun company(company: String) {
        this.company = company
    }

    fun skills(block: SkillsBuilder.() -> Unit) {
        this.skill = SkillsBuilder().apply(block).build()
    }

    fun build(): Person {
        return Person(name, company, skill)
    }
}

class SkillsBuilder() {
    val softs = mutableListOf<String>()
    val hards = mutableListOf<String>()

    fun soft(soft: String) {
        this.softs.add(soft)
    }

    fun hard(hard: String) {
        this.hards.add(hard)
    }

    fun build(): Skill {
        return Skill(softs.toList(), hards.toList())
    }
}

class Person(val name: String, val company: String, val skills: Skill?)

class Skill(val soft: List<String>, val hard: List<String>)
