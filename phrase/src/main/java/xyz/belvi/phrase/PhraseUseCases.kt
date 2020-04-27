package xyz.belvi.phrase

import android.widget.TextView
import androidx.annotation.AnimRes
import xyz.belvi.phrase.behaviour.Behaviour
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.util.*

internal interface PhraseUseCase {
    fun bindTextView(textView: TextView, options: PhraseOptions? = null)
    fun detect(text: String, options: PhraseOptions? = null): PhraseDetected
    fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation
    fun updateOptions(options: PhraseOptions)

}

interface PhraseSourceTranslationUseCase {
    fun specifyTranslateOption(
        source: String,
        translate: TranslationMedium
    ): PhraseSourceTranslationUseCase

    fun makeOptions(): SourceTranslationPreference
}

interface PhraseOptionsUseCase {
    fun includeBehaviours(vararg behaviour: Behaviour): PhraseOptionsUseCase
    fun preferredDetectionMedium(medium: TranslationMedium): PhraseOptionsUseCase
    fun specifySourceTranslation(preferred: SourceTranslationPreference): PhraseOptionsUseCase
    fun switchAnim(@AnimRes anim: Int): PhraseOptionsUseCase
    fun targeting(languageCode: String = Locale.getDefault().language): PhraseOptionsUseCase
    fun build(
        translateText: String?,
        translateFrom: ((translation: PhraseTranslation) -> String)? = null
    ): PhraseOptions
}

interface PhraseBuilderUseCase {
    fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase
    fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase
    fun setUp()
}