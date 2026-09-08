package tr.yurdunubil.app

/** Helpers used by the native app to consume the source-derived pool safely. */
object SourceDerivedIntegration {
    fun mergedQuestions(base: List<Question>): List<Question> =
        (base + SourceDerivedBankRegistry.questions).distinctBy { it.id }

    fun cards(): List<SourceDerivedLibraryContent.StudyCard> = SourceDerivedBankRegistry.cards
}
