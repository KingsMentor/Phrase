package xyz.belvi.phrase.options

import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import java.util.Locale
import xyz.belvi.phrase.translateMedium.TranslationMedium

data class PhraseOptions internal constructor(
    val behavioursOptions: BehaviourOptions = BehaviourOptions(),
    val sourcePreferredTranslation: SourceTranslationPreference = SourceTranslationPreference(),
    val preferredDetection: TranslationMedium?,
    val excludeSources: List<String> = emptyList(),
    val preferredSources: List<String> = emptyList(),
    val targetLanguageCode: String = Locale.getDefault().language,
    val translateText: ((detected: PhraseDetected?) -> String),
    val translateFrom: ((translation: PhraseTranslation) -> String)
)

data class BehaviourOptions internal constructor(
    val behaviours: Behaviour = Behaviour(),
    val signatureTypeFace: Typeface? = null,
    @ColorInt val signatureColor: Int = Color.BLACK
)

data class SourceTranslationOption(
    val sourceLanguageCode: String,
    val targetLanguageCode: List<String> = emptyList(),
    val translate: List<TranslationMedium> = emptyList()
)

data class SourceTranslationPreference internal constructor(
    internal val sourceTranslateOption: List<SourceTranslationOption> = emptyList()
)
