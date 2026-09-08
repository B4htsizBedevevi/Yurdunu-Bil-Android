package tr.yurdunubil.app

/** Single registry for the source-derived learning material kept for compatibility. */
object SourceDerivedBankRegistry {
    val questions: List<Question> = UnifiedQuestionBank.all.distinctBy { it.id }
    val cards: List<SourceDerivedLibraryContent.StudyCard> = SourceDerivedLibraryContent.cards
}
