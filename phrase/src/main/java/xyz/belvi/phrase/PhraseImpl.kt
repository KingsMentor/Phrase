package xyz.belvi.phrase

import android.graphics.Typeface
import android.widget.TextView
import xyz.belvi.phrase.options.Behaviour
import xyz.belvi.phrase.helpers.PhraseTextWatcher
import xyz.belvi.phrase.options.*
import xyz.belvi.phrase.translateMedium.SourceTranslationOption
import xyz.belvi.phrase.translateMedium.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

internal class PhraseImpl internal constructor() : PhraseUseCase {

    internal var phraseOptions: PhraseOptions? = null
    internal lateinit var translationMedium: List<TranslationMedium>

    companion object {
        class Builder(medium: TranslationMedium, val phrase: Phrase) : PhraseBuilderUseCase {
            private var translationMedium = mutableListOf(medium)
            override fun options(phraseOptions: PhraseOptions): PhraseBuilderUseCase {
                phrase.phraseImpl.phraseOptions = phraseOptions
                return this
            }

            override fun includeFallback(medium: TranslationMedium): PhraseBuilderUseCase {
                translationMedium.find { it == medium }?.let {
                    translationMedium.remove(medium)
                }
                translationMedium.add(medium)
                return this
            }

            override fun setUp() {
                phrase.phraseImpl.translationMedium = translationMedium
            }

        }

        class OptionsBuilder(private val targetLanguageCode: String) :
            PhraseOptionsUseCase {
            private var behaviourOptions: BehaviourOptions? = null
            private var sourcesToExclude: List<String> = emptyList()
            private var sourceTranslation: SourceTranslationPreference? = null
            private var preferredDetectionMedium: TranslationMedium? = null

            override fun excludeSource(code: List<String>): PhraseOptionsUseCase {
                this.sourcesToExclude = code
                return this
            }


            override fun preferredDetectionMedium(medium: TranslationMedium): PhraseOptionsUseCase {
                preferredDetectionMedium = medium
                return this
            }

            override fun specifySourceTranslation(preferred: SourceTranslationPreference): PhraseOptionsUseCase {
                sourceTranslation = preferred
                return this
            }

            override fun behaviourOptions(behaviourOptions: BehaviourOptions): PhraseOptionsUseCase {
                this.behaviourOptions = behaviourOptions
                return this
            }


            override fun build(
                translateText: String,
                translateFrom: ((translation: PhraseTranslation) -> String)
            ): PhraseOptions {
                return PhraseOptions(
                    behaviourOptions,
                    sourceTranslation,
                    preferredDetectionMedium,
                    sourcesToExclude,
                    targetLanguageCode,
                    translateText,
                    translateFrom
                )
            }
        }


        class BehaviourOptionsBuilder :
            BehaviourOptionsUseCase {
            private var switchAnim: Int = 0
            private var signatureColor: Int = 0
            private var signatureTypeface: Typeface? = null
            private var behaviours = mutableListOf<Behaviour>()

            override fun includeBehaviours(vararg behaviour: Behaviour): BehaviourOptionsUseCase {
                behaviour.forEach {
                    behaviours.add(it)
                }
                return this
            }

            override fun switchAnim(switchAnim: Int): BehaviourOptionsUseCase {
                this.switchAnim = switchAnim
                return this
            }

            override fun signatureTypeFace(typeFace: Typeface): BehaviourOptionsUseCase {
                this.signatureTypeface = typeFace
                return this
            }

            override fun signatureColor(color: Int): BehaviourOptionsUseCase {
                signatureColor = color
                return this
            }

            override fun build(): BehaviourOptions {
                return BehaviourOptions(behaviours, signatureTypeface, signatureColor, switchAnim)
            }


        }

        class SourceOptionsBuilder :
            PhraseSourceTranslationUseCase {
            private var sourceTranslationOptions = mutableListOf<SourceTranslationOption>()
            override fun specifyTranslateOption(
                source: String,
                translate: TranslationMedium
            ): PhraseSourceTranslationUseCase {
                val index = sourceTranslationOptions.indexOfFirst { it.source == source }
                if (index >= 0)
                    sourceTranslationOptions[index] =
                        SourceTranslationOption(source, translate)
                else
                    sourceTranslationOptions.add(SourceTranslationOption(source, translate))

                return this
            }

            override fun makeOptions(): SourceTranslationPreference {
                return SourceTranslationPreference(sourceTranslationOptions)
            }

        }
    }

    override fun bindTextView(textView: TextView, options: PhraseOptions?) {
        textView.addTextChangedListener(PhraseTextWatcher(options ?: phraseOptions))
    }

    override fun detect(text: String, options: PhraseOptions?): PhraseDetected {
        val phraseOption = options ?: this.phraseOptions
        requireNotNull(phraseOption)
        val detectionMedium = phraseOption.preferredDetection ?: run {
            translationMedium.first()
        }
        return detectionMedium.detect(text)
    }

    override fun translate(text: String, options: PhraseOptions?): PhraseTranslation {
        val phraseOption = options ?: this.phraseOptions
        requireNotNull(phraseOption)
        val detected = detect(text, options)
        val translationMedium =
            phraseOption.sourcePreferredTranslation?.sourceTranslateOption?.find {
                detected.code == it.source
            }?.let {
                it.translate
            } ?: translationMedium.first()

        return PhraseTranslation(
            translationMedium.translate(
                text,
                phraseOption.targetLanguageCode
            ), detected, translationMedium
        )
    }

    override fun updateOptions(options: PhraseOptions) {
        this.phraseOptions = options
    }

}