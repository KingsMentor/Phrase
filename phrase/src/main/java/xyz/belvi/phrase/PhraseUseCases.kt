package xyz.belvi.phrase

import xyz.belvi.phrase.translateMedium.TranslationMedium

internal interface PhraseUseCase {
    fun translate(): String
}

interface PhraseBuilderUseCase {
    fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase
    fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase
    fun setUp(): Phrase
}