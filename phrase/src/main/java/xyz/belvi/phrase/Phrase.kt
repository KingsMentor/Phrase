package xyz.belvi.phrase

import xyz.belvi.phrase.TranslateMedium.TranslationMedium

class Phrase internal constructor() {

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