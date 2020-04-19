package xyz.belvi.phrase

import xyz.belvi.phrase.TranslateMedium.TranslationMedium

internal class PhraseImpl internal constructor() : PhraseUseCase {

    companion object {
        internal lateinit var phrase: Phrase

        class Builder(private val translationMedium: TranslationMedium) : PhraseBuilderUseCase {
            override fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun setUp(): Phrase {
                phrase = Phrase()
                return phrase
            }
        }
    }

    override fun translate(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}