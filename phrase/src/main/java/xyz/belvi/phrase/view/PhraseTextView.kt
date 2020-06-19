package xyz.belvi.phrase.view

import android.content.Context
import android.graphics.Color
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xyz.belvi.phrase.helpers.PhraseSpannableBuilder
import xyz.belvi.phrase.helpers.PhraseTextWatcher
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import java.lang.Exception

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
                    phraseTextViewListener?.onPhraseTranslated(phraseTranslation)
                }

                override fun onActionClick(showingTranslation: Boolean) {
                    phraseTextViewListener?.onActionClick(showingTranslation)
                }

                override fun onContentChanged(content: PhraseSpannableBuilder) {
                    text = content
                }
            }
        text = phraseSpannableBuilder
    }

    fun updateSource(sourceText: String) {
        if (::phraseSpannableBuilder.isInitialized)
            phraseSpannableBuilder.updateSource(sourceText)
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (watcher is PhraseTextWatcher)
            throw Exception("a PhraseTextView doesn't require PhraseTextWatcher")
        else
            super.addTextChangedListener(watcher)
    }
}
