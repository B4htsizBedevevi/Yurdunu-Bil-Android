package tr.yurdunubil.app

import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionBankValidationTest {
    @Test
    fun questionBankHasUniqueValidQuestions() {
        val all = SharedQuestionPool.all
        assertTrue("Question bank should not be empty", all.isNotEmpty())
        assertTrue("Question IDs must be unique", all.map { it.id }.distinct().size == all.size)
        all.forEach { q ->
            assertTrue("${q.id}: options", q.options.size >= 4)
            assertTrue("${q.id}: correctIndex", q.correctIndex in q.options.indices)
            assertTrue("${q.id}: text", q.text.isNotBlank())
            assertTrue("${q.id}: explanation", q.explanation.isNotBlank())
        }
    }

    @Test
    fun everyMainTopicHasQuestions() {
        val covered = SharedQuestionPool.all.map { it.topic }.toSet()
        GeographyData.topics.forEach { topic ->
            val expected = SharedQuestionPool.topicForLibrary(topic.title)
            assertTrue("${topic.title} should map to at least one bank topic", expected.any { it in covered })
        }
    }

    @Test
    fun pickerNeverReturnsMoreThanRequested() {
        val mode = SharedGameModes.master
        val picked = SharedQuestionPool.pick(mode, seed = 42L)
        assertTrue(picked.size <= mode.questions)
        assertTrue(picked.map { it.id }.distinct().size == picked.size)
    }
}
