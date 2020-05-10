package xyz.belvi.phrase.translateMedium

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import xyz.belvi.phrase.options.PhraseDetected
import java.util.*


abstract class TranslationMedium() {

    abstract fun detect(text: String): PhraseDetected
    abstract fun translate(text: String, targeting: String): String
    abstract fun name(): String

}