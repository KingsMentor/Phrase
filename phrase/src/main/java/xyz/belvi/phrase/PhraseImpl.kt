package xyz.belvi.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import java.util.Locale
import xyz.belvi.phrase.helpers.PhraseTextWatcher
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.BehaviorFlags
import xyz.belvi.phrase.options.Behaviour
import xyz.belvi.phrase.options.BehaviourOptions
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.options.SourceTranslationOption
import xyz.belvi.phrase.options.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

class PhraseImpl internal constructor() : PhraseUseCase {

    internal var phraseOptions: PhraseOptions? = null
    internal lateinit var translationMediums: List<TranslationMedium>

    override fun bindTextView(
        textView: TextView,
        options: PhraseOptions?,
        phraseTranslateListener: PhraseTranslateListener?
    ) {
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        textView.addTextChangedListener(
            PhraseTextWatcher(
                options,
                phraseTranslateListener
            )
        )
    }

    override suspend fun detect(text: String, options: PhraseOptions?): PhraseDetected? {
        val phraseOption = (options ?: this.phraseOptions) ?: return null
        if (phraseOption.behavioursOptions.behaviours.ignoreDetection())
            return null
        val detectionMedium = phraseOption.preferredDetection ?: run {
            translationMediums.first()
        }
        return detectionMedium.detect(text, phraseOption.targetLanguageCode)
    }

    override suspend fun translate(text: String, options: PhraseOptions?): PhraseTranslation {
        val phraseOption =
            (options ?: this.phraseOptions) ?: return PhraseTranslation(text, null, null)

        val detected = detect(text, options)

        var translationMediums: List<TranslationMedium>? = if (detected != null) {
            phraseOption.sourcePreferredTranslation.sourceTranslateOption.filter {
                detected.languageCode.equals(
                    it.sourceLanguageCode,
                    true
                )
            }
                .let { sourceOptions ->
                    sourceOptions.find { sourceTranslationOption ->
                        sourceTranslationOption.targetLanguageCode.indexOfFirst {
                            it.equals(
                                phraseOption.targetLanguageCode,
                                true
                            )
                        } >= 0
                    }
                        ?.let {
                            if (it.translate.isEmpty()) translationMediums else it.translate
                        } ?: sourceOptions.find { it.targetLanguageCode.contains("*") }
                        ?.let { it.translate }
                    ?: if (phraseOption.behavioursOptions.behaviours.translatePreferredSourceOnly()) null else translationMediums
                }
        } else translationMediums

        if (translationMediums == null) {
            translationMediums =
                if (phraseOption.behavioursOptions.behaviours.translatePreferredSourceOnly() && phraseOption.preferredSources.indexOfFirst {
                        it.equals(
                            detected?.languageCode,
                            true
                        )
                    } < 0) {
                    null
                } else {
                    this.translationMediums
                }
        }

        if ((detected?.languageCode ?: "").equals(
                phraseOption.targetLanguageCode,
                true
            ) || phraseOption.excludeSources.indexOfFirst {
                it.equals(
                    (detected?.languageCode ?: ""),
                    true
                )
            } > 0
        )
            return PhraseTranslation(text, null, null)

        return translationMediums?.let {
            var translationMedium = translationMediums.first()
            var translate = translationMedium.translate(
                text,
                detected?.languageCode ?: "",
                phraseOption.targetLanguageCode
            )

            val translationIterator = translationMediums.iterator()
            while (translate.isNullOrBlank() && translationIterator.hasNext()) {
                val medium = translationIterator.next()
                if (medium == translationMedium)
                    continue
                translate = medium.translate(
                    text,
                    detected?.languageCode ?: "",
                    phraseOption.targetLanguageCode
                )
                translationMedium = medium
            }
            PhraseTranslation(translate, translationMedium.name(), detected)
        } ?: PhraseTranslation(text, null, null)
    }

    override fun updateOptions(options: PhraseOptions) {
        this.phraseOptions = options
    }

    override fun setTranslationMediums(translationMediums: List<TranslationMedium>) {
        this.translationMediums = translationMediums
    }

    class OptionsBuilder {
        private var behaviourOptions = BehaviourOptions()
        var sourcesToExclude: List<String> = emptyList()
        var preferredSources: List<String> = emptyList()
        var sourceTranslation = listOf<SourceTranslationOption>()
        var preferredDetectionMedium: TranslationMedium? = null
        var targeting: String = Locale.getDefault().language
        var actionLabel: String = ""
        var resultActionLabel: ((translation: PhraseTranslation) -> String) = { "" }

        fun behaviourFlags(behaviourOptions: BehaviourOptionsBuilder.() -> Unit) {
            BehaviourOptionsBuilder().apply(behaviourOptions).run {
                this@OptionsBuilder.behaviourOptions = this.build()
            }
        }

        internal fun build(): PhraseOptions {
            return PhraseOptions(
                behaviourOptions,
                SourceTranslationPreference(sourceTranslation),
                preferredDetectionMedium,
                sourcesToExclude,
                preferredSources,
                targeting,
                actionLabel,
                resultActionLabel
            )
        }
    }

    class BehaviourOptionsBuilder {
        @AnimRes
        var switchAnim: Int = 0

        @ColorInt
        var signatureColor: Int = 0
        var signatureTypeface: Typeface? = null
        var flags = setOf<@BehaviorFlags Int>()

        internal fun build(): BehaviourOptions {
            return BehaviourOptions(
                Behaviour(flags),
                signatureTypeface,
                signatureColor,
                switchAnim
            )
        }
    }
}
