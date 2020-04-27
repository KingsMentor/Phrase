package xyz.belvi.phrase.options

import com.google.cloud.translate.Detection
import xyz.belvi.phrase.PhraseImpl
import xyz.belvi.phrase.PhraseOptionsUseCase
import xyz.belvi.phrase.behaviour.Behaviour
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

data class PhraseOptions internal constructor(
    val behaviours: List<Behaviour>,
    val sourcePreferredTranslation: SourceTranslationPreference?,
    val preferredDetection: TranslationMedium?,
    val targetLanguageCode: String,
    val translateText: String?,
    val translateFrom: ((translation: PhraseTranslation) -> String)?,
    val switchAnim: Int
) {
    companion object {
        fun options(): PhraseOptionsUseCase {
            return PhraseImpl.Companion.OptionsBuilder()
        }
    }
}