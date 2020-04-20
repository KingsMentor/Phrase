package xyz.belvi.phrase.translateMedium

abstract class TranslationMedium<D>(creditRes: Int = 0) {

    abstract fun detect(text: String): D
    abstract fun detectedLanguage(text: String): String
    abstract fun translate(text: String): String
}