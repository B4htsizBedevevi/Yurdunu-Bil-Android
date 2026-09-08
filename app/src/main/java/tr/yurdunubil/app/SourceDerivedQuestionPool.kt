package tr.yurdunubil.app

/** Public pool entry point for source-derived questions. */
object SourceDerivedQuestionPool {
    val all: List<Question> = SourceDerivedIntegration.mergedQuestions(emptyList())
}
