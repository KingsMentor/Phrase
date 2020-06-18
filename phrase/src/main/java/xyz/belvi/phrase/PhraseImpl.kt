package xyz.belvi.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import xyz.belvi.phrase.helpers.PhraseTextWatcher
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.*
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.util.*

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

    override fun detect(text: String, options: PhraseOptions?): PhraseDetected? {
        val phraseOption = options ?: this.phraseOptions
        requireNotNull(phraseOption)
        if (phraseOption.behavioursOptions.behaviours.ignoreDetection())
            return null
        val detectionMedium = phraseOption.preferredDetection ?: run {
            translationMediums.first()
        }
        return detectionMedium.detect(text)
    }

    override fun translate(text: String, options: PhraseOptions?): PhraseTranslation {
        val phraseOption = options ?: this.phraseOptions
        requireNotNull(phraseOption)

        val detected = detect(text, options)

        val translationMediums = if (detected != null) {
            phraseOption.sourcePreferredTranslation.sourceTranslateOption.filter { detected.languageCode != it.sourceLanguageCode }
                .let { sourceOptions ->
                    sourceOptions.find { it.targetLanguageCode.contains(phraseOption.targetLanguageCode) }
                        ?.let {
                            it.translate
                        } ?: sourceOptions.find { it.targetLanguageCode.contains("*") }
                        ?.let { it.translate }
                    ?: if (phraseOption.behavioursOptions.behaviours.translatePreferredSourceOnly()) null else translationMediums

                }

        } else translationMediums

        if (detected?.languageCode == phraseOption.targetLanguageCode || phraseOption.excludeSources.contains(
                detected?.languageCode
            )
        )
            return PhraseTranslation(text, null, null)

        translationMediums?.let {
            var translationMedium = translationMediums?.first()
            var translate = translationMedium.translate(
                text,
                phraseOption.targetLanguageCode
            )

            val translationIterator = translationMediums.iterator()
            while (translate.isNullOrBlank() && translationIterator.hasNext()) {
                val medium = translationIterator.next()
                if (medium == translationMedium)
                    continue
                translate = medium.translate(
                    text,
                    phraseOption.targetLanguageCode
                )
                translationMedium = medium
            }
            return PhraseTranslation(translate, translationMedium.name(), detected)
        }
        return PhraseTranslation(text, null, null)
    }

    override fun updateOptions(options: PhraseOptions) {
        this.phraseOptions = options
    }

    override fun setTranslationMediums(translationMedium: List<TranslationMedium>) {
        this.translationMediums = translationMediums
    }


    class OptionsBuilder {
        private var behaviourOptions = BehaviourOptions()
        var sourcesToExclude: List<String> = emptyList()
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

        fun build(): PhraseOptions {
            return PhraseOptions(
                behaviourOptions,
                SourceTranslationPreference(sourceTranslation),
                preferredDetectionMedium,
                sourcesToExclude,
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

        fun build(): BehaviourOptions {
            return BehaviourOptions(
                Behaviour(flags),
                signatureTypeface,
                signatureColor,
                switchAnim
            )
        }
    }

}
