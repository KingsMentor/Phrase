package xyz.belvi.phrase

import androidx.annotation.AnimRes
import xyz.belvi.phrase.behaviour.Behaviour
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

internal interface PhraseUseCase {
    fun translate(): String

}

interface PhraseSourceTranslationUseCase {
    fun specifyTranslateOption(
        source: String,
        detect: TranslationMedium,
        translate: TranslationMedium
    ): PhraseSourceTranslationUseCase

    fun makeOptions(): SourceTranslationPreference
}

interface PhraseOptionsUseCase {
    fun switchAnim(@AnimRes anim: Int): PhraseOptionsUseCase
    fun includeBehaviours(vararg behaviour: Behaviour): PhraseOptionsUseCase
    fun specifySourceTranslation(preferred: SourceTranslationPreference): PhraseOptionsUseCase
    fun build(): PhraseOptions
}

interface PhraseBuilderUseCase {
    fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase
    fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase
    fun setUp(): Phrase
}