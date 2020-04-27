package xyz.belvi.phrase

import android.widget.TextView
import androidx.annotation.AnimRes
import xyz.belvi.phrase.behaviour.Behaviour
import xyz.belvi.phrase.helpers.PhraseSpannableStringBuilder
import xyz.belvi.phrase.options.DetectedLanguage
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

internal interface PhraseUseCase {
    fun bindTextView(textView: TextView, options: PhraseOptions? = null)
    fun detect(text: String, options: PhraseOptions? = null): DetectedLanguage
    fun translate(text: String, options: PhraseOptions? = null): String
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
    fun build(): PhraseOptions
}

interface PhraseBuilderUseCase {
    fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase
    fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase
    fun setUp(): Phrase
}