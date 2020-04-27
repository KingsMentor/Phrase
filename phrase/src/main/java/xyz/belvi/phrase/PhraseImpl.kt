package xyz.belvi.phrase

import android.widget.TextView
import xyz.belvi.phrase.behaviour.Behaviour
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

        class OptionsBuilder :
            PhraseOptionsUseCase {
            private var switchAnim: Int = 0
            private var targetLanguage: String = ""
            private var behaviours = mutableListOf<Behaviour>()
            private var sourceTranslation: SourceTranslationPreference? = null
            private var preferredDetectionMedium: TranslationMedium? = null

            override fun switchAnim(anim: Int): PhraseOptionsUseCase {
                switchAnim = anim
                return this
            }

            override fun targeting(languageCode: String): PhraseOptionsUseCase {
                targetLanguage = languageCode
                return this
            }

            override fun includeBehaviours(vararg behaviour: Behaviour): PhraseOptionsUseCase {
                behaviour.forEach {
                    behaviours.add(it)
                }
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


            override fun build(
                translateText: String?,
                translateFrom: ((translation: PhraseTranslation) -> String)?
            ): PhraseOptions {
                return PhraseOptions(
                    behaviours,
                    sourceTranslation,
                    preferredDetectionMedium,
                    targetLanguage,
                    translateText,
                    translateFrom,
                    switchAnim
                )
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
        val detectionMedium = phraseOption?.preferredDetection ?: run {
            translationMedium.first()
        }
        val code = detectionMedium.detectedLanguageCode(text)
        return PhraseDetected(
            text,
            code,
            detectionMedium.detectedLanguageName(text),
            detectionMedium
        )
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