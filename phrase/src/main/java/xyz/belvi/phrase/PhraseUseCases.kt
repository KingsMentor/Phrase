package xyz.belvi.phrase

import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import xyz.belvi.phrase.options.*
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

internal interface PhraseUseCase {
    fun bindTextView(textView: TextView, options: PhraseOptions? = null)
    fun detect(text: String, options: PhraseOptions? = null): PhraseDetected?
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
    fun excludeSources(code: List<String>): PhraseOptionsUseCase
    fun preferredDetectionMedium(medium: TranslationMedium): PhraseOptionsUseCase
    fun specifySourceTranslation(preferred: SourceTranslationPreference): PhraseOptionsUseCase
    fun behaviourOptions(behaviourOptions: BehaviourOptions): PhraseOptionsUseCase
    fun build(
        translateText: String,
        translateFrom: (translation: PhraseTranslation) -> String
    ): PhraseOptions
}

interface BehaviourOptionsUseCase {
    fun includeBehaviours(@BehaviorInt vararg behaviour: Int): BehaviourOptionsUseCase
    fun switchAnim(@AnimRes switchAnim: Int): BehaviourOptionsUseCase
    fun signatureTypeFace(typeFace: Typeface): BehaviourOptionsUseCase
    fun signatureColor(@ColorInt color: Int): BehaviourOptionsUseCase
    fun build(): BehaviourOptions
}

interface PhraseBuilderUseCase {
    fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase
    fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase
    fun setUp()
}