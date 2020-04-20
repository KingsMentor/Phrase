package xyz.belvi.phrase.translateMedium

import xyz.belvi.phrase.PhraseImpl
import xyz.belvi.phrase.PhraseSourceTranslationUseCase


data class SourceTranslationPreference internal constructor(
    internal val sourceTranslateOption: List<SourceTranslationOption>
) {
    companion object {
        fun options(): PhraseSourceTranslationUseCase {
            return PhraseImpl.Companion.SourceOptionsBuilder()
        }
    }
}