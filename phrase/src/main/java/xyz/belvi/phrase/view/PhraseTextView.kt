package xyz.belvi.phrase.view

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xyz.belvi.phrase.helpers.PhraseSpannableBuilder
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTextView(context: Context, attrs: AttributeSet) :
    AppCompatTextView(context, attrs) {

    private lateinit var phraseSpannableBuilder: PhraseSpannableBuilder
    fun prepare(
        source: String = text.toString(),
        phraseOptions: PhraseOptions? = null,
        phraseTextViewListener: PhraseTranslateListener? = null
    ) {
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
        phraseSpannableBuilder =
            object : PhraseSpannableBuilder(source, phraseOptions) {
                override fun onPhraseTranslating() {
                    phraseTextViewListener?.onPhraseTranslating()
                }

                override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                    super.onPhraseTranslated(phraseTranslation)
                    text = phraseSpannableBuilder
                    phraseTextViewListener?.onPhraseTranslated(phraseTranslation)
                }
            }
    }

    fun updateSource(sourceText: String) {
        if (::phraseSpannableBuilder.isInitialized)
            phraseSpannableBuilder.updateSource(sourceText)
    }
}
