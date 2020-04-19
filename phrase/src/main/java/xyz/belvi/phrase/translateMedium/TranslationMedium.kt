package xyz.belvi.phrase.translateMedium

abstract class TranslationMedium {

    abstract fun detect()

    abstract fun translate(): String
}