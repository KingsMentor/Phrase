package xyz.belvi.phrase.translateMedium

abstract class TranslationMedium(creditRes: Int = 0) {

    abstract fun init()
    abstract fun detect(text: String)
    abstract fun translate(text: String): String
}