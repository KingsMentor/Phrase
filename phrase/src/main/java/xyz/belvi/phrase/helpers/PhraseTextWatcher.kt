package xyz.belvi.phrase.helpers

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTextWatcher(
    phraseOptions: PhraseOptions? = null,
    private var sourceLanguage: String? = null,
    phraseTranslateListener: PhraseTranslateListener? = null
) : TextWatcher {
    private var editable: Editable? = null
    private val phraseSpannableBuilder: PhraseSpannableBuilder

    private fun updateSourceLanguage(sourceLanguage: String) {
        this.sourceLanguage = sourceLanguage
    }

    init {
        phraseSpannableBuilder =
            object : PhraseSpannableBuilder("", sourceLanguage, phraseOptions) {
                override fun onPhraseTranslating() {
                    phraseTranslateListener?.onPhraseTranslating()
                }

                override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                    phraseTranslateListener?.onPhraseTranslated(phraseTranslation)
                }

                override fun onActionClick(actionStatus: ActionStatus) {
                    phraseTranslateListener?.onActionClick(actionStatus)
                }

                override fun onContentChanged(content: PhraseSpannableBuilder) {
                    phraseTranslateListener?.onContentChanged(content)
                    updateEditable(content)
                }
            }
    }

    override fun afterTextChanged(s: Editable?) {
        editable = s
        if (s.toString() == phraseSpannableBuilder.toString() && phraseSpannableBuilder.actionStatus == ActionStatus.SHOWING_SOURCE) {
            phraseSpannableBuilder.updateSource(phraseSpannableBuilder.subSequence(0,phraseSpannableBuilder.length), sourceLanguage)
            return
        }
        if (s.isNullOrBlank()) {
            return
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.isNullOrBlank())
            return
        if (s.toString() != phraseSpannableBuilder.toString())
            phraseSpannableBuilder.updateSource(s.subSequence(0,s.length), sourceLanguage)
    }

    private fun updateEditable(content: SpannableStringBuilder? = null) {

        if (content.toString() == editable.toString()) {
            return
        }
        editable?.apply {
            clear()
            append(content ?: phraseSpannableBuilder)
        }
    }
}
