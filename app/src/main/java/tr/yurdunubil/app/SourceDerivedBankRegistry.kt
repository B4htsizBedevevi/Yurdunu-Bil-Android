package tr.yurdunubil.app

/** Single registry for the new source-derived material. */
object SourceDerivedBankRegistry {
    val questions: List<Question> = SourceDerivedGeographyBank.all.distinctBy { it.id }
    val cards: List<SourceDerivedLibraryContent.StudyCard> = SourceDerivedLibraryContent.cards
}
