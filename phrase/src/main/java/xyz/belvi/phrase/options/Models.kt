package xyz.belvi.phrase.options

import androidx.annotation.IntDef
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_HIDE_CREDIT_SIGNATURE
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_HIDE_TRANSLATE_PROMPT
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_REPLACE_SOURCE_TEXT
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_SKIP_DETECTION
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_TRANSLATE_IGNORING_DETECTION
import xyz.belvi.phrase.options.Behaviour.Companion.BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY
import xyz.belvi.phrase.translateMedium.TranslationMedium

data class PhraseDetected(
    val text: String,
    val code: String,
    val name: String,
    val detectMedium: TranslationMedium
)

data class PhraseTranslation(
    val translation: String,
    val source: PhraseDetected?,
    val translationMedium: TranslationMedium?
)

class Behaviour(private val behaviorSet: Set<@BehaviorFlags Int> = setOf()) {


    companion object {
        const val BEHAVIOR_REPLACE_SOURCE_TEXT: Int = 1

        const val BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY: Int = 2

        const val BEHAVIOR_SKIP_DETECTION: Int = 3

        const val BEHAVIOR_HIDE_CREDIT_SIGNATURE: Int = 4

        const val BEHAVIOR_HIDE_TRANSLATE_PROMPT: Int = 5

        const val BEHAVIOR_TRANSLATE_IGNORING_DETECTION: Int = 6
    }


    internal fun replaceSourceText() = behaviorSet.contains(BEHAVIOR_REPLACE_SOURCE_TEXT)

    internal fun skipDetection() = behaviorSet.contains(BEHAVIOR_SKIP_DETECTION)

    internal fun translatePreferredSourceOnly() =
        behaviorSet.contains(BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY)

    internal fun hideSignature() = behaviorSet.contains(BEHAVIOR_HIDE_CREDIT_SIGNATURE)

    internal fun hideTranslatePrompt() = behaviorSet.contains(BEHAVIOR_HIDE_TRANSLATE_PROMPT)

    internal fun ignoreSkipDetection() = behaviorSet.contains(BEHAVIOR_TRANSLATE_IGNORING_DETECTION)
}


@Target(AnnotationTarget.TYPE)
@IntDef(
    flag = true,
    value = [
        BEHAVIOR_REPLACE_SOURCE_TEXT,
        BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY,
        BEHAVIOR_SKIP_DETECTION,
        BEHAVIOR_HIDE_CREDIT_SIGNATURE,
        BEHAVIOR_HIDE_TRANSLATE_PROMPT,
        BEHAVIOR_TRANSLATE_IGNORING_DETECTION
    ]
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class BehaviorFlags