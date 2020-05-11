package xyz.belvi.phrase.view

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xyz.belvi.phrase.helpers.PhraseSpannableStringBuilder
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTextView(context: Context, attrs: AttributeSet) :
    AppCompatTextView(context, attrs) {

    private lateinit var phraseSpannableStringBuilder: PhraseSpannableStringBuilder
    fun prepare(
        source: String = text.toString(),
        phraseOptions: PhraseOptions? = null,
        phraseTextViewListener: PhraseTranslateListener?
    ) {
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
        phraseSpannableStringBuilder =
            object : PhraseSpannableStringBuilder(source, phraseOptions) {
                override fun translating() {
                    super.translating()
                    phraseTextViewListener?.onPhraseTranslating()
                }

                override fun notifyUpdate(phraseTranslation: PhraseTranslation?) {
                    super.notifyUpdate(phraseTranslation)
                    text = phraseSpannableStringBuilder
                    phraseTextViewListener?.onPhraseTranslated(phraseTranslation)
                }
            }
    }

    fun updateSource(sourceText: String) {
        if (::phraseSpannableStringBuilder.isInitialized)
            phraseSpannableStringBuilder.updateSource(sourceText)
    }

}