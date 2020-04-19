package xyz.belvi.phrase

import xyz.belvi.phrase.TranslateMedium.TranslationMedium

interface PhraseUseCase {

    fun with(medium: TranslationMedium): PhraseBuilderUseCase
}

interface PhraseBuilderUseCase {
    fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase
    fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase
    fun setUp(medium: TranslationMedium): PhraseUseCase
}