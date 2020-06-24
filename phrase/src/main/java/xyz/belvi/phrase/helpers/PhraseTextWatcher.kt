package xyz.belvi.phrase.helpers

import android.text.Editable
import android.text.TextWatcher
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTextWatcher(
    phraseOptions: PhraseOptions? = null,
    phraseTranslateListener: PhraseTranslateListener? = null
) : TextWatcher {
    private var editable: Editable? = null
    private val phraseSpannableBuilder: PhraseSpannableBuilder

    init {
        phraseSpannableBuilder = object : PhraseSpannableBuilder("", phraseOptions) {
            override fun onPhraseTranslating() {
                phraseTranslateListener?.onPhraseTranslating()
            }

            override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                phraseTranslateListener?.onPhraseTranslated(phraseTranslation)
                updateEditable()
            }

            override fun onActionClick(showingTranslation: Boolean) {
                phraseTranslateListener?.onActionClick(showingTranslation)
            }

            override fun onContentChanged(content: PhraseSpannableBuilder) {
                updateEditable()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        editable = s
        if (s.toString() == phraseSpannableBuilder.toString() || s.isNullOrBlank())
            return
        updateEditable()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.isNullOrBlank())
            return
        if (s.toString() != phraseSpannableBuilder.toString())
            phraseSpannableBuilder.updateSource(s)
    }

    private fun updateEditable() {
        editable?.apply {
            clear()
            append(phraseSpannableBuilder)
        }
    }
}
