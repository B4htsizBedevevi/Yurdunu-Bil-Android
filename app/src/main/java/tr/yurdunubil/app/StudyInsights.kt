package tr.yurdunubil.app

import kotlin.math.roundToInt

data class StudyInsight(
    val title: String,
    val detail: String,
    val score: Int,
    val topic: String
)

fun buildStudyInsights(s: AppProgressStore.Snapshot): List<StudyInsight> {
    val topics = (s.topicCorrect.keys + s.topicWrong.keys).distinct()
    return topics.map { topic ->
        val c = s.topicCorrect[topic] ?: 0
        val w = s.topicWrong[topic] ?: 0
        val total = c + w
        val accuracy = if (total == 0) 0 else (c * 100f / total).roundToInt()
        StudyInsight(
            title = topic,
            detail = when {
                accuracy < 50 -> "Öncelikli tekrar: temel kavram + kısa test"
                accuracy < 70 -> "Geliştirilebilir: karışık soru çöz"
                else -> "İyi gidiyor: aralıklı tekrar ile koru"
            },
            score = accuracy,
            topic = topic
        )
    }.sortedBy { it.score }
}

fun recommendedQuestions(s: AppProgressStore.Snapshot, limit: Int = 10): List<Question> {
    val pool = SharedQuestionPool.all
    val weakTopics = buildStudyInsights(s).take(3).map { it.topic }.toSet()
    val wrong = pool.filter { it.id in s.wrongIds }
    val weak = pool.filter { it.topic in weakTopics && it.id !in s.masteredIds }
    val unseen = pool.filter { it.id !in s.seenIds }
    return (wrong.shuffled() + weak.shuffled() + unseen.shuffled() + pool.shuffled())
        .distinctBy { it.id }
        .take(limit)
}
