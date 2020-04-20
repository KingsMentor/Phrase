package xyz.belvi.phrase

import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.TranslationMedium

class Phrase internal constructor() {
    internal lateinit var phraseOptions: PhraseOptions
    companion object {
        fun with(translationMedium: TranslationMedium): PhraseBuilderUseCase {
            return PhraseImpl.Companion.Builder(translationMedium)
        }

        fun instance(): Phrase {
            return PhraseImpl.phrase
        }
    }

    fun translate(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}