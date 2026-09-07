package tr.yurdunubil.app

import org.junit.Assert.assertEquals
import org.junit.Test

class QuestionEngineTest {
    @Test fun resultCountsCorrectWrongAndBlank() {
        val q = SampleQuestions.all.take(3)
        val engine = QuizEngine(q)
        engine.answer(q[0].correctIndex)
        engine.answer((q[1].correctIndex + 1) % q[1].options.size)
        engine.answer(null)
        val result = engine.result()
        assertEquals(1, result.correct)
        assertEquals(1, result.wrong)
        assertEquals(1, result.blank)
        assertEquals(10, result.xp)
    }

    @Test fun poolIdsAreUnique() {
        val ids = SharedQuestionPool.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test fun questionsHaveValidOptions() {
        SharedQuestionPool.all.forEach { q ->
            assertEquals(5, q.options.size)
            assert(q.correctIndex in q.options.indices)
            assert(q.text.isNotBlank())
            assert(q.explanation.isNotBlank())
        }
    }
}
