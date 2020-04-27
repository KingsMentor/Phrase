package xyz.belvi.phrase.translateMedium

import androidx.annotation.DrawableRes
import xyz.belvi.phrase.options.PhraseDetected
import java.util.*


abstract class TranslationMedium(@DrawableRes protected open val creditRes: Int = 0) {

    abstract fun detect(text: String): PhraseDetected
    abstract fun translate(text: String, targeting: String): String
    abstract fun name(): String

}