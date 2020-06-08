package xyz.belvi.phrase

import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.*
import xyz.belvi.phrase.translateMedium.TranslationMedium


internal interface PhraseUseCase {
    fun bindTextView(
        textView: TextView,
        options: PhraseOptions? = null,
        phraseTranslateListener: PhraseTranslateListener? = null
    )

    fun detect(text: String, options: PhraseOptions? = null): PhraseDetected?
    fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation
    fun updateOptions(options: PhraseOptions)
}
