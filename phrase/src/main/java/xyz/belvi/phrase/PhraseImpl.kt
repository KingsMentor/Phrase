package xyz.belvi.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.ColorInt
import xyz.belvi.phrase.helpers.PhraseTextWatcher
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.*
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.util.*

class PhraseImpl internal constructor() : PhraseUseCase {

    internal var phraseOptions: PhraseOptions? = null
    internal lateinit var translationMedium: List<TranslationMedium>


    class OptionsBuilder {
        private var behaviourOptions = BehaviourOptions()
        var sourcesToExclude: List<String> = emptyList()
        var sourceTranslation = listOf<SourceTranslationOption>()
        var preferredDetectionMedium: TranslationMedium? = null
        var targetting: String = Locale.getDefault().language
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
                targetting,
                actionLabel,
                resultActionLabel
            )
        }
    }

    class BehaviourOptionsBuilder {
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

    override fun bindTextView(
        textView: TextView,
        options: PhraseOptions?,
        phraseTranslateListener: PhraseTranslateListener?
    ) {
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        textView.addTextChangedListener(
            PhraseTextWatcher(
                options ?: phraseOptions,
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
            translationMedium.first()
        }
        return detectionMedium.detect(text)
    }

    override fun translate(text: String, options: PhraseOptions?): PhraseTranslation {
        val phraseOption = options ?: this.phraseOptions
        requireNotNull(phraseOption)

        val detected = detect(text, options)

        val translationMedium = if (detected != null) {
            if (phraseOption.behavioursOptions.behaviours.translatePreferredSourceOnly()) {
                phraseOption.sourcePreferredTranslation.sourceTranslateOption.find {
                    detected.languageCode == it.sourceLanguageCode
                }?.let {
                    it.translate
                }
            } else {
                phraseOption.sourcePreferredTranslation.sourceTranslateOption.find {
                    detected.languageCode == it.sourceLanguageCode
                }?.let {
                    it.translate
                } ?: translationMedium.first()
            }
        } else translationMedium.first()

        return PhraseTranslation(
            translationMedium?.translate(
                text,
                phraseOption.targetLanguageCode
            ) ?: text, translationMedium?.name(), detected
        )
    }

    override fun updateOptions(options: PhraseOptions) {
        this.phraseOptions = options
    }
}
