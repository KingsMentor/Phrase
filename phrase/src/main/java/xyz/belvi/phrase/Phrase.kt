package xyz.belvi.phrase

import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.TranslationMedium

class Phrase internal constructor() {

    internal val phraseImpl = PhraseImpl()

    companion object {
        private val phrase = Phrase()
        fun with(translationMedium: TranslationMedium): PhraseBuilderUseCase {
            return PhraseImpl.Companion.Builder(translationMedium, phrase)
        }

        fun instance(): Phrase {
            return phrase
        }
    }

    fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation {
        return phraseImpl.translate(text, options)
    }

    fun detectLanguage(text: String, options: PhraseOptions? = null): PhraseDetected? {
        return phraseImpl.detect(text, options)
    }
}