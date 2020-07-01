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
     * @param textView#movementMethod is set to allow click on translate link
     * @param textView#highlightColor is set to transparent to remove background color when link is clicked
     *
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
        var detected = detectionMedium.detect(text, phraseOption.targetLanguageCode.first())

        /*
         *  if detectionMedium fails to detects language, fall back to the rest of the mediums available in preferredDetection and translationMediums in order at which it appears in the list
         *  until a result is found or return null if no result was found.
         */

        val translationIterator =
            (phraseOption.preferredDetection.plus(translationMediums)).iterator()
        while (translationIterator.hasNext() && detected == null) {
            val next = translationIterator.next()
            if (next != detectionMedium) {
                detected = next.detect(text, phraseOption.targetLanguageCode.first())
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
        if ((phraseOption.targetLanguageCode.indexOfFirst {
                it.toLowerCase() == (detected?.languageCode ?: "").toLowerCase()
            } >= 0) || phraseOption.excludeSources.indexOfFirst {
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
                    sourceOptions.find {
                        it.targetLanguageCode.intersect(phraseOption.targetLanguageCode)
                            .isNotEmpty()
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
            val inCache = translationMedium.isTranslationInCached(
                text,
                detected?.languageCode ?: "",
                phraseOption.targetLanguageCode.first()
            )
            var translate = translationMedium.translate(
                text,
                detected?.languageCode ?: "",
                phraseOption.targetLanguageCode.first()
            )

            val translationIterator = translationMediums.iterator()
            while (translate.isNullOrBlank() && translationIterator.hasNext()) {
                val medium = translationIterator.next()
                if (medium == translationMedium)
                    continue
                translate = medium.translate(
                    text,
                    detected?.languageCode ?: "",
                    phraseOption.targetLanguageCode.first()
                )
                translationMedium = medium
            }
            PhraseTranslation(translate, translationMedium.name(), detected, inCache)
        }
    }

    override fun updateOptions(options: PhraseOptions) {
        this.phraseOptions = options
    }

    /**
     * implementation for changing default translateMediums for Phrase
     */
    override fun setTranslationMediums(translationMediums: List<TranslationMedium>) {
        this.translationMediums = translationMediums
    }

    class OptionsBuilder {
        /**
         * define Phrase behaviours
         */
        var behaviour = BehaviourOptions()

        /**
         * define sources to excluded. text detected to be in a language-code added to this list will not be translated
         */
        var sourcesToExclude: List<String> = emptyList()

        /**
         * define sources to be translated.
         * @see behaviour#flags on how to set BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY. this flag ensure only language sources in this list is translated
         */
        var preferredSources: List<String> = emptyList()

        /**
         * define rules for translating from one source to multiple targets.
         * With @param sourceTranslation you can define that translation from en to listOf('fr','es') should be processed with DEEPL while
         * translation from en to listOf('de','dk') should be processed with GoogleTranslate
         * this also works with flag: BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY. is this flag is set, only source language meeting this rule or defined in preferredSources will be translated
         */
        var sourceTranslation = listOf<SourceTranslationRule>()

        /**
         * define mediums for processing language detection.
         * If this is not defined, @param translateMedium will be used instead. This list works with a fallback mechanism.
         * If the first medium in this list fails to detect the source language, it falls back to the second item on the list, until the list is exhausted.
         */
        var preferredDetectionMedium = listOf<TranslationMedium>()

        /**
         * set a target language to translate text to.
         */
        var targeting = listOf(Locale.getDefault().language)

        /**
         * define a label that prompt a user to translate a text
         * detected contains information about the language detected and the medium used.
         */
        var actionLabel: ((detected: PhraseDetected?) -> String) = { "" }

        /**
         * define a label that user will see after translation.
         * use translation to get details regarding the translated text
         */
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
        /**
         * set color for translationMedium name shown to user after translation. This is to give credit to the medium used for translation.
         * use BEHAVIOR_HIDE_CREDIT to hide this credit.
         * use BEHAVIOR_HIDE_TRANSLATE_PROMPT to hide prompt entire
         */
        @ColorInt
        var signatureColor: Int = Color.BLACK

        /**
         * set typeface for translationMedium name shown to user after translation. This is to give credit to the medium used for translation.
         * use BEHAVIOR_HIDE_CREDIT to hide this credit.
         * use BEHAVIOR_HIDE_TRANSLATE_PROMPT to hide prompt entire
         */
        var signatureTypeface: Typeface? = null

        /**
         * set list of flags to handle how Phrase use PhraseOptions
         * @see Behaviour
         */
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
