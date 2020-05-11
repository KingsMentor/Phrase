package xyz.belvi.phrase.helpers

import android.text.Editable
import android.text.TextWatcher
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTextWatcher(
    phraseTranslateListener: PhraseTranslateListener?,
    phraseOptions: PhraseOptions?
) : TextWatcher {
    private var phraseSpannableStringBuilder: PhraseSpannableStringBuilder =
        object : PhraseSpannableStringBuilder("", phraseOptions) {
            override fun translating() {
                phraseTranslateListener?.onPhraseTranslating()
            }

            override fun notifyUpdate(phraseTranslation: PhraseTranslation?) {
                phraseTranslateListener?.onPhraseTranslated(phraseTranslation)
            }
        }

    override fun afterTextChanged(s: Editable?) {
        s?.clear()
        s?.append(phraseSpannableStringBuilder)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString() != phraseSpannableStringBuilder.toString())
            phraseSpannableStringBuilder.updateSource(s.toString())
    }

}