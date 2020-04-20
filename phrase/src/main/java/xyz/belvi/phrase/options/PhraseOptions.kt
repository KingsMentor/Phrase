package xyz.belvi.phrase.options

import xyz.belvi.phrase.PhraseImpl
import xyz.belvi.phrase.PhraseOptionsUseCase
import xyz.belvi.phrase.behaviour.Behaviour
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference

data class PhraseOptions internal constructor(
    val behaviours: List<Behaviour>,
    val sourcePreferredTranslation: SourceTranslationPreference,
    val switchAnim: Int
) {
    companion object {
        fun options(): PhraseOptionsUseCase {
            return PhraseImpl.Companion.OptionsBuilder()
        }
    }
}