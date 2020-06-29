package xyz.belvi.phrase

import android.widget.TextView
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.TranslationMedium

/**
 * expose callable interface available via Phrase Instance
 */
internal interface PhraseUseCase {
    fun bindTextView(
        textView: TextView,
        sourceLanguage: String? = null,
        options: PhraseOptions? = null,
        phraseTranslateListener: PhraseTranslateListener? = null
    )

    suspend fun detect(text: String, options: PhraseOptions? = null): PhraseDetected?
    suspend fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation?
    fun updateOptions(options: PhraseOptions)
    fun setTranslationMediums(translationMediums: List<TranslationMedium>)
}
