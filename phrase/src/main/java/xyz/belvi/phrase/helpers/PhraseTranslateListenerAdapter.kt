package xyz.belvi.phrase.helpers

import android.text.SpannableStringBuilder
import xyz.belvi.phrase.options.PhraseTranslation

/**
 * this is an adapter for PhraseTranslateListener and provide based extension for SpannableStringBuilder which is used by PhraseSpannableBuilder
 */
open class PhraseTranslateListenerAdapter constructor(
    source: CharSequence
) : SpannableStringBuilder(source), PhraseTranslateListener {
    override fun onPhraseTranslating() {

    }

    override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {

    }

    override fun onActionClick(actionStatus: ActionStatus) {

    }

    override fun onContentChanged(content: PhraseSpannableBuilder, actionStatus: ActionStatus) {

    }

}
