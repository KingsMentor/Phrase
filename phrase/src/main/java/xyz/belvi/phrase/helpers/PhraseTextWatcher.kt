package xyz.belvi.phrase.helpers

import android.text.Editable
import android.text.TextWatcher
import androidx.core.text.set
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTextWatcher(
    phraseOptions: PhraseOptions? = null,
    phraseTranslateListener: PhraseTranslateListener? = null
) : TextWatcher {
    private var editable: Editable? = null
    private val phraseSpannableStringBuilder: PhraseSpannableStringBuilder

    init {
        phraseSpannableStringBuilder = object : PhraseSpannableStringBuilder("", phraseOptions) {
            override fun translating() {
                super.translating()
                phraseTranslateListener?.onPhraseTranslating()
            }

            override fun notifyUpdate(phraseTranslation: PhraseTranslation?) {
                super.notifyUpdate(phraseTranslation)
                phraseTranslateListener?.onPhraseTranslated(phraseTranslation)
                updateEditable()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        editable = s
        if (s.toString() == phraseSpannableStringBuilder.toString() || s.isNullOrBlank())
            return
        updateEditable()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.isNullOrBlank())
            return
        if (s.toString() != phraseSpannableStringBuilder.toString())
            phraseSpannableStringBuilder.updateSource(s.toString())
    }

    private fun updateEditable() {
        editable?.apply {
            clear()
            append(phraseSpannableStringBuilder)
        }
    }
}
