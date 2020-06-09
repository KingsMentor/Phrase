package xyz.belvi.phrase

import android.widget.TextView
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.TranslationMedium


fun phrase(phraseBuilder: Phrase.Builder.() -> Unit): Phrase =
    Phrase.Builder().apply(phraseBuilder).build()


class Phrase internal constructor() {


    internal val phraseImpl = PhraseImpl()

    companion object {
        val phrase = Phrase()
        fun instance(): Phrase {
            return phrase
        }
    }

    fun bindTextView(
        textView: TextView,
        options: PhraseOptions? = null,
        phraseTranslateListener: PhraseTranslateListener? = null
    ) {
        return phraseImpl.bindTextView(textView, options, phraseTranslateListener)
    }

    fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation {
        return phraseImpl.translate(text, options)
    }

    fun detectLanguage(text: String, options: PhraseOptions? = null): PhraseDetected? {
        return phraseImpl.detect(text, options)
    }


    class Builder {
        var mediums = listOf<TranslationMedium>()

        fun options(phraseOptions: PhraseImpl.OptionsBuilder.() -> Unit) {
            PhraseImpl.OptionsBuilder().apply(phraseOptions).run {
                phrase.phraseImpl.phraseOptions = this.build()
            }
        }

        fun build(): Phrase {
            phrase.phraseImpl.translationMedium = mediums
            return phrase
        }
    }


}
