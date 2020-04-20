package xyz.belvi.phrase.translateMedium

internal data class SourceTranslationOption(
    val source: String,
    val detect: TranslationMedium,
    val translate: TranslationMedium
)