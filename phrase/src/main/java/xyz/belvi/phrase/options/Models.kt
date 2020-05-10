package xyz.belvi.phrase.options

import xyz.belvi.phrase.translateMedium.TranslationMedium

data class PhraseDetected(
    val text: String, val code: String, val name: String,
    val detectMedium: TranslationMedium
)

data class PhraseTranslation(
    val translation: String,
    val source: PhraseDetected,
    val translationMedium: TranslationMedium
)

object Behaviour {
    object AUTO_DETECT
    object AUTO_TRANSLATE
    object REPLACE_SOURCE_TEXT
    object HIDE_CREDIT_MEDIUM
}