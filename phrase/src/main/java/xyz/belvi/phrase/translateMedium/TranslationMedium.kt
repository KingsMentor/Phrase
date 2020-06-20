package xyz.belvi.phrase.translateMedium

import xyz.belvi.phrase.options.PhraseDetected

abstract class TranslationMedium {

    val cacheDetected = HashMap<String, PhraseDetected>()
    val cacheTranslation = HashMap<String, String>()

    abstract suspend fun detect(text: String, targeting: String = ""): PhraseDetected?
    abstract suspend fun translate(text: String, sourceLanguage: String, targeting: String): String
    abstract fun name(): String
}
