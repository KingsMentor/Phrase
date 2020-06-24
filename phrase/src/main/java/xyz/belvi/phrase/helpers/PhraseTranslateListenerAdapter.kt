package xyz.belvi.phrase.helpers

import android.text.SpannableStringBuilder
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseTranslateListenerAdapter constructor(
    source: CharSequence
) : SpannableStringBuilder(source), PhraseTranslateListener {
    override fun onPhraseTranslating() {

    }

    override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {

    }

    override fun onActionClick(showingTranslation: Boolean) {

    }

    override fun onContentChanged(content: PhraseSpannableBuilder) {

    }

}
