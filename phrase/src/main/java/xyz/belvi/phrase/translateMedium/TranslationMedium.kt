package xyz.belvi.phrase.translateMedium

import androidx.annotation.DrawableRes
import java.util.*


abstract class TranslationMedium(
    @DrawableRes protected open val creditRes: Int = 0
) {

    abstract fun <T> detect(text: String): T
    abstract fun detectedLanguageCode(text: String): String
    abstract fun detectedLanguageName(text: String): String
    abstract fun translate(text: String, target: String): String
    abstract fun name(): String

}