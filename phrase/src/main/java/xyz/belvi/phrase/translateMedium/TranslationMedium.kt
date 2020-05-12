package xyz.belvi.phrase.translateMedium

import xyz.belvi.phrase.options.PhraseDetected

abstract class TranslationMedium {

    abstract fun detect(text: String): PhraseDetected
    abstract fun translate(text: String, targeting: String): String
    abstract fun name(): String
}
