package xyz.belvi.phrase

import xyz.belvi.phrase.TranslateMedium.TranslationMedium

internal interface PhraseUseCase {
    fun translate(): String
}

interface PhraseBuilderUseCase {
    fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase
    fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase
    fun setUp(): Phrase
}