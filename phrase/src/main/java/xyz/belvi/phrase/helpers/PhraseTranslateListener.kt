package xyz.belvi.phrase.helpers

import xyz.belvi.phrase.options.PhraseTranslation

/**
 * listener used PhraseStringBuilder to provide callback for click events and translation
 */
interface PhraseTranslateListener {
    /**
     * call when text translation is about to start
     */
    fun onPhraseTranslating()

    /**
     * fired when text has been translated
     * @param phraseTranslation contains more information about the translation
     */
    fun onPhraseTranslated(phraseTranslation: PhraseTranslation?)

    /**
     * fired when translation prompt is clicked
     * @param actionStatus is the actionStatus in PhraseSpannableBuilder before click
     */
    fun onActionClick(actionStatus: ActionStatus)

    /**
     * fired when PhraseSpannableBuilder content has changed
     * @param content is the updated content
     */
    fun onContentChanged(content: PhraseSpannableBuilder,actionStatus: ActionStatus)
}
