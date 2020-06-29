package xyz.belvi.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.widget.TextView
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
import xyz.belvi.phrase.options.SourceTranslationRule
import xyz.belvi.phrase.options.SourceTranslationPreference
import xyz.belvi.phrase.translateMedium.TranslationMedium

class PhraseImpl internal constructor() : PhraseUseCase {

    /**
     * default phraseOptions used by Phrase instance
     */
    internal var phraseOptions: PhraseOptions? = null

    /**
     * default translationMediums used by Phrase instance
     */
    internal lateinit var translationMediums: List<TranslationMedium>

    /**
     *
     * implementation for binding textView to Phrase
     * */
    override fun bindTextView(
        textView: TextView,
        sourceLanguage: String?,
        options: PhraseOptions?,
        phraseTranslateListener: PhraseTranslateListener?
    ) {
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        textView.addTextChangedListener(
            PhraseTextWatcher(
                options,
                sourceLanguage,
                phraseTranslateListener
            )
        )
    }

    override suspend fun detect(text: String, options: PhraseOptions?): PhraseDetected? {
        // set phraseOption to defaultOption if no options was provided
        val phraseOption = (options ?: this.phraseOptions) ?: return null
        // only continue with detection if BEHAVIOR_IGNORE_DETECTION is not set
        if (phraseOption.behavioursOptions.behaviours.ignoreDetection())
            return null
        // use preferredDetection if set in phraseOption other use the first engine in translationMediums
        val detectionMedium = phraseOption.preferredDetection.firstOrNull() ?: run {
            translationMediums.first()
        }
        // run detection with the medium found
        var detected = detectionMedium.detect(text, phraseOption.targetLanguageCode)

        /*
         *  if detectionMedium fails to detects language, fall back to the rest of the mediums available in preferredDetection and translationMediums in order at which it appears in the list
         *  until a result is found or return null if no result was found.
         */

        val translationIterator =
            (phraseOption.preferredDetection.plus(translationMediums)).iterator()
        while (translationIterator.hasNext() && detected == null) {
            val next = translationIterator.next()
            if (next != detectionMedium) {
                detected = next.detect(text, phraseOption.targetLanguageCode)
            }
        }
        return detected
    }

    override suspend fun translate(text: String, options: PhraseOptions?): PhraseTranslation? {
        // set phraseOption to defaultOption if no options was provided
        val phraseOption =
            (options ?: this.phraseOptions) ?: return null

        // detect the source language of @text
        val detected = detect(text, options)

        // return null if source language wasn't detected and BEHAVIOR_IGNORE_DETECTION is not set
        if (detected == null && !phraseOption.behavioursOptions.behaviours.ignoreDetection())
            return null

        // if the detected source is in excluded list or is same with translation target language, we do not want to run translation.
        if ((detected?.languageCode ?: "").equals(
                phraseOption.targetLanguageCode,
                true
            ) || phraseOption.excludeSources.indexOfFirst {
                it.equals(
                    (detected?.languageCode ?: ""), true
                )
            } > 0
        )
            return null

        /*
         * check if  there is a rule defined for the detected sourceLanguage
         * if a rule was found, return translateMedium defined for the rule.
         * if a rule wasn't found, check if BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY is set.
         * if BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY is set, no translation will be processed.
         * if BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY is not set, return default translation medium
         **/

        var translationMediums: List<TranslationMedium>? = if (detected != null) {
            phraseOption.sourcePreferredTranslation.sourceTranslateRule.filter {
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
                    }?.let {
                        if (it.translate.isEmpty()) translationMediums else it.translate
                    } ?: sourceOptions.find { it.targetLanguageCode.contains("*") }
                        ?.let { it.translate }
                    ?: if (phraseOption.behavioursOptions.behaviours.translatePreferredSourceOnly()) null else translationMediums
                }
        } else translationMediums

        /*
         * if translateMedium is null after the previous check, we want to check of there's preferredSource defined in options.
         * if BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY is set and the source language is not in preferredSource, no translationMedium is defined.
         */

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

        // run translation with the translationMediums found. fallback implementation is based on this list.
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
        }
    }

    override fun updateOptions(options: PhraseOptions) {
        this.phraseOptions = options
    }

    override fun setTranslationMediums(translationMediums: List<TranslationMedium>) {
        this.translationMediums = translationMediums
    }

    class OptionsBuilder {
        var behaviour = BehaviourOptions()
        var sourcesToExclude: List<String> = emptyList()
        var preferredSources: List<String> = emptyList()
        var sourceTranslation = listOf<SourceTranslationRule>()
        var preferredDetectionMedium = listOf<TranslationMedium>()
        var targeting: String = Locale.getDefault().language
        var actionLabel: ((detected: PhraseDetected?) -> String) = { "" }
        var resultActionLabel: ((translation: PhraseTranslation) -> String) = { "" }

        internal fun build(): PhraseOptions {
            return PhraseOptions(
                behaviour,
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
        @ColorInt
        var signatureColor: Int = Color.BLACK
        var signatureTypeface: Typeface? = null
        var flags = setOf<@BehaviorFlags Int>()

        internal fun build(): BehaviourOptions {
            return BehaviourOptions(
                Behaviour(flags),
                signatureTypeface,
                signatureColor
            )
        }
    }
}
