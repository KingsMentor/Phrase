package xyz.belvi.phrase.translateMedium

abstract class TranslationMedium(creditRes: Int = 0) {

    abstract fun detect(text: String): Any
    abstract fun detectedLanguage(text: String): String
    abstract fun translate(text: String): String
}