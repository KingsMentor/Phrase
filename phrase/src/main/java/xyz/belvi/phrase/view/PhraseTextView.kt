package xyz.belvi.phrase.view

import android.content.Context
import android.graphics.Color
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xyz.belvi.phrase.helpers.ActionStatus
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
        sourceLanguage: String? = null,
        phraseOptions: PhraseOptions? = null,
        phraseTextViewListener: PhraseTranslateListener? = null
    ) {
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
        phraseSpannableBuilder =
            object : PhraseSpannableBuilder(source, sourceLanguage, phraseOptions) {
                override fun onPhraseTranslating() {
                    phraseTextViewListener?.onPhraseTranslating()
                }

                override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                    phraseTextViewListener?.onPhraseTranslated(phraseTranslation)
                }

                override fun onActionClick(actionStatus: ActionStatus) {
                    phraseTextViewListener?.onActionClick(actionStatus)
                }

                override fun onContentChanged(
                    content: PhraseSpannableBuilder,
                    actionStatus: ActionStatus
                ) {
                    text = content
                }
            }
        text = phraseSpannableBuilder
    }

    fun updateSource(sourceText: String, sourceLanguage: String? = null) {
        if (::phraseSpannableBuilder.isInitialized)
            phraseSpannableBuilder.updateSource(sourceText, sourceLanguage)
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (watcher is PhraseTextWatcher)
            throw Exception("a PhraseTextView doesn't require PhraseTextWatcher")
        else
            super.addTextChangedListener(watcher)
    }
}
