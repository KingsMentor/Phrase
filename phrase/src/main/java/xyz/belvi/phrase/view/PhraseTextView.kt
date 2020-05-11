package xyz.belvi.phrase.view

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import xyz.belvi.phrase.helpers.PhraseSpannableStringBuilder
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTextView(context: Context) : AppCompatTextView(context) {

    private lateinit var phraseSpannableStringBuilder: PhraseSpannableStringBuilder
    private lateinit var phraseTextViewListener: PhraseTranslateListener

    fun prepare(source: String = text.toString(), phraseOptions: PhraseOptions? = null) {
        phraseSpannableStringBuilder =
            object : PhraseSpannableStringBuilder(source, phraseOptions) {
                override fun translating() {
                    super.translating()
                    if (::phraseTextViewListener.isInitialized)
                        phraseTextViewListener.onPhraseTranslating()
                }

                override fun notifyUpdate(phraseTranslation: PhraseTranslation?) {
                    text = phraseSpannableStringBuilder
                    if (::phraseTextViewListener.isInitialized)
                        phraseTextViewListener.onPhraseTranslated(phraseTranslation)
                }
            }
    }

    fun updateSource(sourceText: String) {
        if (::phraseSpannableStringBuilder.isInitialized)
            phraseSpannableStringBuilder.updateSource(sourceText)
    }

    fun setPhraseListener(phraseTextViewListener: PhraseTranslateListener) {
        this.phraseTextViewListener = phraseTextViewListener
    }
}