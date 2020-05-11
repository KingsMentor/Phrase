package xyz.belvi.phrase.options

import xyz.belvi.phrase.PhraseImpl
import xyz.belvi.phrase.PhraseOptionsUseCase
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.util.*

data class PhraseOptions internal constructor(
    val behavioursOptions: BehaviourOptions = BehaviourOptions(),
    val sourcePreferredTranslation: SourceTranslationPreference = SourceTranslationPreference(),
    val preferredDetection: TranslationMedium?,
    val excludeSources: List<String> = emptyList(),
    val targetLanguageCode: String = Locale.getDefault().language,
    val translateText: String,
    val translateFrom: ((translation: PhraseTranslation) -> String)
) {
    companion object {
        fun options(targetLanguageCode: String = Locale.getDefault().language): PhraseOptionsUseCase {
            return PhraseImpl.Companion.OptionsBuilder(targetLanguageCode)
        }
    }
}