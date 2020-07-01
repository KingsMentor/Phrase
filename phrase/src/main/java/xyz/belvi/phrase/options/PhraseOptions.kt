package xyz.belvi.phrase.options

import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt
import java.util.Locale
import xyz.belvi.phrase.translateMedium.TranslationMedium

/**
 * declare preference and translation options. See this as the config object for Phrase.
 */
data class PhraseOptions internal constructor(
    /**
     * setting behaviourOptions for Phrase
     * @see BehaviourOptions
     */
    var behavioursOptions: BehaviourOptions = BehaviourOptions(),
    /**
     * define translation preference. You can use this to assign certain TranslationMedium for translating text from a specific source to a specific target languae
     */
    var sourcePreferredTranslation: SourceTranslationPreference = SourceTranslationPreference(),
    /**
     * define mediums to use for language detection based on preference. Phrase uses mediums mentioned in the list as a fallback.
     * If this list is empty, Phrase uses list provided in translationMedium for running language detection instead
     */
    var preferredDetection: List<TranslationMedium> = emptyList(),
    /**
     * languageSource included in this list will be excluded from  translation
     */
    var excludeSources: List<String> = emptyList(),
    /**
     * languageSource include in this list will only be translated when BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY flag is set
     */
    var preferredSources: List<String> = emptyList(),
    /**
     * target language code of the user. This is the language all text appearing in other language will be translated to
     */
    var targetLanguageCode: String = Locale.getDefault().language,
    /**
     * the text to be displayed to the user that prompt for text translation
     * Phrase appends this text depending on whether BEHAVIOR_HIDE_TRANSLATE_PROMPT is set or not.
     * @see PhraseDetected
     */
    var translateText: ((detected: PhraseDetected?) -> String),
    /**
     * the text to be displayed to the user that confirms that the original text has been translated.
     * Phrase appends this text depending on whether BEHAVIOR_HIDE_TRANSLATE_PROMPT is set or not.
     * Phrase appends translationMedium.name() to this text depending on whether BEHAVIOR_HIDE_CREDIT is set or not.
     * @see PhraseTranslation
     */
    var translateFrom: ((translation: PhraseTranslation) -> String)
)

/**
 * define behaviourOptions for phrase
 */
data class BehaviourOptions internal constructor(
    /**
     * @see Behaviour
     */
    val behaviours: Behaviour = Behaviour(),
    /**
     * set typeface for translationMedium name shown to user after translation. This is to give credit to the medium used for translation.
     * use BEHAVIOR_HIDE_CREDIT to hide this credit.
     * use BEHAVIOR_HIDE_TRANSLATE_PROMPT to hide prompt entire
     */
    val signatureTypeFace: Typeface? = null,
    /**
     * set color for translationMedium name shown to user after translation. This is to give credit to the medium used for translation.
     * use BEHAVIOR_HIDE_CREDIT to hide this credit.
     * use BEHAVIOR_HIDE_TRANSLATE_PROMPT to hide prompt entire
     */
    @ColorInt val signatureColor: Int = Color.BLACK
)

/**
 * define specific rule for SourceTranslationPreference
 */
data class SourceTranslationRule(
    /**
     * define source language that this rule should apply to
     */
    val sourceLanguageCode: String,
    /**
     * define target language that this rule should apply to in regards to sourceLanguageCode
     * This should just include * to refer to all target language
     */
    val targetLanguageCode: List<String> = emptyList(),
    /**
     * define TranslationMedium to be used when this rule is meant. Phrase uses default translateMedium is this is not provided or left empty.
     * Defines TranslationMedium in order of fallback preference.
     */
    val translate: List<TranslationMedium> = emptyList()
)

data class SourceTranslationPreference internal constructor(
    /**
     * set of rules to be applied before processing translation.
     * Rules are processed from top to bottom. This means if more than one rule applies to same source, the first defined rule will be used if it meets the checking criteria
     */
    internal val sourceTranslateRule: List<SourceTranslationRule> = emptyList()
)
