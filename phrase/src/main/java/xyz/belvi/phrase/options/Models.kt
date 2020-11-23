package xyz.belvi.phrase.options

import androidx.annotation.IntDef
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_HIDE_CREDIT
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_HIDE_TRANSLATE_PROMPT
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_IGNORE_DETECTION
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_REPLACE_SOURCE_TEXT
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY

/**
 * object return when TranlationMedium.detect() is called.
 * @param text : original text whose source language was detected
 * @param languageCode: language code of detected language
 * @param languageName: language name of detected language
 * @param detectionMediumName: medium used in detecting text
 * @param fromCache: there was no new api call made to retrieve this result. It was fetched from cache
 */
data class PhraseDetected(
    val text: String,
    var languageCode: String,
    var languageName: String,
    val detectionMediumName: String?,
    val fromCache: Boolean = false
)

/**
 * used by Phrase to handle text Translation
 * @param translation : translated text
 * @param detectedSource: PhraseDetected object for the original text
 * @param fromCache: there was no new api call made to retrieve this result. It was fetched from cache
 */
data class PhraseTranslation(
    val translation: String,
    val translationMediumName: String?,
    val detectedSource: PhraseDetected?,
    val fromCache: Boolean = false
)

/**
 * for defining behavior and flags for Phraseoptions
 * @param behaviorSet is a set of @BehaviorFlags
 */
class Behaviour(private val behaviorSet: Set<@BehaviorFlags Int> = setOf()) {

    companion object {
        /**
         *  include this flag if translated text should be replaced by source text
         */
        const val BEHAVIOR_REPLACE_SOURCE_TEXT: Int = 1
        /**
         *  include this flag if Phrase should only run translation for languageCode included in PhraseOptions preferredSource and sourceTranslationPreference.
         */
        const val BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY: Int = 2

        /**
         *  include this flag if Phrase should ignore language detection before processing translation.
         *  This is helpful if you already know the source language of the original text (in this case, ensure source language is pass to PhraseSpannableBuilder or
         *  PhraseTextView.prepare(), depending on the Implementation of Phrase you are using.)
         *  @see https://github.com/KingsMentor/Phrase documentation.
         */
        const val BEHAVIOR_IGNORE_DETECTION: Int = 3

        /**
         *  include this flag if Phrase should not include translationMediumName to resultActionLabel
         */
        const val BEHAVIOR_HIDE_CREDIT: Int = 4

        /**
         *  include this flag if Phrase not show actionLabel ot resultActionLabel at all.
         */
        const val BEHAVIOR_HIDE_TRANSLATE_PROMPT: Int = 5
    }

    internal fun replaceSourceText() = behaviorSet.contains(BEHAVIOR_REPLACE_SOURCE_TEXT)

    internal fun ignoreDetection() = behaviorSet.contains(BEHAVIOR_IGNORE_DETECTION)

    internal fun translatePreferredSourceOnly() =
        behaviorSet.contains(BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY)

    internal fun hideSignature() = behaviorSet.contains(BEHAVIOR_HIDE_CREDIT)

    internal fun hideTranslatePrompt() = behaviorSet.contains(BEHAVIOR_HIDE_TRANSLATE_PROMPT)
}

@Target(AnnotationTarget.TYPE)
@IntDef(
    flag = true,
    value = [
        BEHAVIOR_REPLACE_SOURCE_TEXT,
        BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY,
        BEHAVIOR_IGNORE_DETECTION,
        BEHAVIOR_HIDE_CREDIT,
        BEHAVIOR_HIDE_TRANSLATE_PROMPT
    ]
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class BehaviorFlags
