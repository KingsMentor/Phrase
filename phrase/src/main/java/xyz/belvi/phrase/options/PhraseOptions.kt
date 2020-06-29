package xyz.belvi.phrase.options

import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt
import java.util.Locale
import xyz.belvi.phrase.translateMedium.TranslationMedium

data class PhraseOptions internal constructor(
    var behavioursOptions: BehaviourOptions = BehaviourOptions(),
    var sourcePreferredTranslation: SourceTranslationPreference = SourceTranslationPreference(),
    var preferredDetection: List<TranslationMedium> = emptyList(),
    var excludeSources: List<String> = emptyList(),
    var preferredSources: List<String> = emptyList(),
    var targetLanguageCode: String = Locale.getDefault().language,
    var translateText: ((detected: PhraseDetected?) -> String),
    var translateFrom: ((translation: PhraseTranslation) -> String)
)

data class BehaviourOptions internal constructor(
    val behaviours: Behaviour = Behaviour(),
    val signatureTypeFace: Typeface? = null,
    @ColorInt val signatureColor: Int = Color.BLACK
)

data class SourceTranslationRule(
    val sourceLanguageCode: String,
    val targetLanguageCode: List<String> = emptyList(),
    val translate: List<TranslationMedium> = emptyList()
)

data class SourceTranslationPreference internal constructor(
    internal val sourceTranslateRule: List<SourceTranslationRule> = emptyList()
)
