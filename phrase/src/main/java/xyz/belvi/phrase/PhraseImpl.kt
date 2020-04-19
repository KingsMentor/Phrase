package xyz.belvi.phrase

import xyz.belvi.phrase.TranslateMedium.GoogleTranslate
import xyz.belvi.phrase.TranslateMedium.TranslationMedium

class PhraseImpl {

    companion object {
        class Builder(private val translationMedium: TranslationMedium) : PhraseBuilderUseCase {

            override fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase {

            }

            override fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase {

            }

            override fun setUp(medium: TranslationMedium): PhraseUseCase {

            }

        }
    }

}