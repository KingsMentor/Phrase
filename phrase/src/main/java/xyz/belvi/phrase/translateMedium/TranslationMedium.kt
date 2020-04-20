package xyz.belvi.phrase.translateMedium

abstract class TranslationMedium(creditRes: Int = 0) {

    abstract fun detect()

    abstract fun translate(): String
}