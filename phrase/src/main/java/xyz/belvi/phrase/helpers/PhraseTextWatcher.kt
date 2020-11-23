package xyz.belvi.phrase.helpers

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

/**
 * this is a custom textWatcher used by Phrase. PhraseTextWatcher bring Phrase capability to any textView that adds it as a text watcher.
 * @param sourceLanguage is the language code of the original text. If this is set, language detection is skipped
 * @param phraseOptions to be used for this builder. If none is provided, the default phraseOptions is used.
 * @param phraseTranslateListener is a callback to get Phrase update for this implementation.
 * @see Phrase#defaultOption for default phraseOptions used.
 */
open class PhraseTextWatcher(
    phraseOptions: PhraseOptions? = null,
    private var sourceLanguage: String? = null,
    phraseTranslateListener: PhraseTranslateListener? = null
) : TextWatcher {
    private var editable: Editable? = null
    private val phraseSpannableBuilder: PhraseSpannableBuilder

    /**
     * @param sourceLanguage is the sourceLanguage of the original text. This should remain null if you want Phrase to run language detection for the new text.
     */
    fun updateSourceLanguage(sourceLanguage: String) {
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

                override fun onContentChanged(
                    content: PhraseSpannableBuilder,
                    actionStatus: ActionStatus
                ) {
                    phraseTranslateListener?.onContentChanged(content,actionStatus)
                    updateEditable(content)
                }
            }
    }

    override fun afterTextChanged(s: Editable?) {
        editable = s
        // only update source when there's a relevant text change
        if (s.toString() == phraseSpannableBuilder.toString() && phraseSpannableBuilder.actionStatus == ActionStatus.SHOWING_SOURCE) {
            phraseSpannableBuilder.updateSource(
                phraseSpannableBuilder.subSequence(
                    0,
                    phraseSpannableBuilder.length
                ), sourceLanguage
            )
            return
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // we do not want to update source for empty string.
        if (s.isNullOrBlank())
            return
        // only update source when changes is not same with current content of phraseSpannableBuilder
        if (s.toString() != phraseSpannableBuilder.toString())
            phraseSpannableBuilder.updateSource(s.subSequence(0, s.length), sourceLanguage)
    }

    /**
     * update textView with changes identified by Phrase via editable
     */
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
