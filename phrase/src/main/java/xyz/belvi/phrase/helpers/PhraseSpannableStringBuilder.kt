package xyz.belvi.phrase.helpers

import android.text.SpannableStringBuilder
import xyz.belvi.phrase.translateMedium.TranslationMedium

abstract class PhraseSpannableStringBuilder : SpannableStringBuilder() {

    abstract fun showTranslation()
    abstract fun hideTranslation()

    fun buildTranslatedText(source: String, result: String, medium: TranslationMedium) {

    }

}