package xyz.belvi.phrase.translateMedium

import java.util.*


abstract class TranslationMedium(
    protected open val targetedLanguage: String = Locale.getDefault().language,
    protected open val creditRes: Int = 0
) {

    abstract fun <T> detect(text: String): T
    abstract fun detectedLanguage(text: String): String
    abstract fun detectedLanguageName(text: String): String
    abstract fun translate(text: String): String
}