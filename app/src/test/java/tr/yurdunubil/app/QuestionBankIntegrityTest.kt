package tr.yurdunubil.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionBankIntegrityTest {
    @Test
    fun sharedPoolHasEnoughQuestionsForProductionModes() {
        assertTrue(SharedQuestionPool.all.size >= 150)
        assertTrue(SharedQuestionPool.all.distinctBy { it.id }.size == SharedQuestionPool.all.size)
    }

    @Test
    fun everyQuestionIsStructurallyValid() {
        SharedQuestionPool.all.forEach { question ->
            assertTrue("Blank question: ${question.id}", question.text.isNotBlank())
            assertTrue("Too few options: ${question.id}", question.options.size >= 4)
            assertTrue("Blank topic: ${question.id}", question.topic.isNotBlank())
            assertTrue("Bad correct index: ${question.id}", question.correctIndex in question.options.indices)
            assertTrue("Blank explanation: ${question.id}", question.explanation.isNotBlank())
            assertFalse("Duplicate options: ${question.id}", question.options.size != question.options.distinct().size)
        }
    }

    @Test
    fun megaBankIsIncludedInSharedPool() {
        val megaIds = MegaQuestionBank.all.map { it.id }.toSet()
        val sharedIds = SharedQuestionPool.all.map { it.id }.toSet()
        assertTrue(megaIds.isNotEmpty())
        assertTrue(sharedIds.containsAll(megaIds))
    }

    @Test
    fun libraryTopicsHaveQuestionCoverage() {
        val coveredTopics = SharedQuestionPool.all.groupingBy { it.topic }.eachCount()
        GeographyLibraryContent.notes.forEach { note ->
            val accepted = SharedQuestionPool.topicForLibrary(note.title)
            if (accepted.isNotEmpty()) {
                assertTrue("No questions for ${note.title}", accepted.any { coveredTopics[it] ?: 0 > 0 })
            }
        }
    }
}
