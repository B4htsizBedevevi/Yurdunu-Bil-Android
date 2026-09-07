package tr.yurdunubil.app

/** Compatibility accessor used by the next-generation dashboard. */
val CurrentFact.body: String
    get() = detail
