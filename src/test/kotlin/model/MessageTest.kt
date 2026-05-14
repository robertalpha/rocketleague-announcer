package nl.vanalphenict.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MessageTest {

    @Test
    fun testTag() {
        "Je Moeder".toTag() shouldBe "JM"
        "JeMoeder".toTag() shouldBe "JM"
        "JeMoeder!".toTag() shouldBe "JM!"
        "JEMOEDER".toTag() shouldBe "JEM"
    }
}
