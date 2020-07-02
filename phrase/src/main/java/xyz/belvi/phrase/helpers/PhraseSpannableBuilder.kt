package xyz.belvi.phrase.helpers

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.text.style.TypefaceSpan
import android.view.View
import kotlinx.coroutines.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.Languages

/**
 * this is the action status for PhraseSpannableBuilder
 * @param SHOWING_SOURCE for when PhraseSpannableBuilder contains just the source
 * @param SHOWING_WITH_TRANSLATE_ACTION for when PhraseSpannableBuilder contains source text and a prompt to translate
 * @param SHOWING_TRANSLATED for when PhraseSpannableBuilder contains source and translated text, witch information about translated medium (if behavior flag permits)
 */
enum class ActionStatus {
    SHOWING_SOURCE,
    SHOWING_WITH_TRANSLATE_ACTION,
    SHOWING_TRANSLATED
}

/***
 * handles spannableString for Phrase
 * @param source is the original text
 * @param sourceLanguage is the language code of the original text. If this is set, language detection is skipped
 * @param phraseOptions to be used for this builder. If none is provided, the default phraseOptions is used.
 * @see Phrase#defaultOption for default phraseOptions used.
 */
abstract class PhraseSpannableBuilder constructor(
    protected var source: CharSequence,
    protected var sourceLanguage: String? = null,
    protected var phraseOptions: PhraseOptions? = null
) :
    PhraseTranslateListenerAdapter(source) {
    internal var actionStatus = ActionStatus.SHOWING_SOURCE
    protected var phraseTranslation: PhraseTranslation? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * this is initialized by building a span with just the original text as source
     */
    init {
        buildTranslateActionSpan(source)
    }

    /**
     * for updating phraseOption associated with this spannableBuilder
     */
    fun updateOptions(options: PhraseOptions) {
        this@PhraseSpannableBuilder.phraseOptions = options
        buildTranslateActionSpan(source)
    }

    /**
     * for updating source associated with this spannableBuilder. This changes the original text and prepare this builder for another translation
     * @param source is the new original text
     * @param sourceLanguage is the sourceLanguage of the original text. This should remain null if you want Phrase to run language detection for the new text.
     */
    fun updateSource(source: CharSequence, sourceLanguage: String? = null) {
        this@PhraseSpannableBuilder.source = source
        this@PhraseSpannableBuilder.sourceLanguage = sourceLanguage
        buildTranslateActionSpan(source)
    }

    fun cancelPendingJobs() {
        scope.cancel()
    }

    /**
     * get the options for PhraseSpannableBuilder. this returns the default phraseOption in Pgrase.instance if this builder has not been supplied with any option.
     */
    private fun options() = phraseOptions ?: Phrase.instance().phraseImpl.phraseOptions

    /**
     * build string to include original text and a translate actionLabel.
     * Phrase adds translate actionLabel based options provided via phraseOptions.
     * First, sourceLanguage is detected
     * then, sourceLanguage is unable to be detected, translation actionLabel is not appended to source (if user doesn't set BEHAVIOR_IGNORE_DETECTION)
     */
    private fun buildTranslateActionSpan(source: CharSequence) {
        init()
        scope.launch {
            val options = options() ?: return@launch
            val behaviors = options.behavioursOptions.behaviours

            /*
             * detected source language. is sourceLanguage is provided, we want to skip language detection.
             * if sourceLanguage is not provided and BEHAVIOR_IGNORE_DETECTION is set, phraseDetected is null since we want to skip language detection.
             * Otherwise,  Phrase.instance().detectLanguage(source.toString()) is executed.
             */
            val phraseDetected =
                sourceLanguage?.let { sourceLanguage ->
                    val languageName =
                        Languages.values().find { it.code == sourceLanguage.toLowerCase() }?.name
                            ?: sourceLanguage.toLowerCase()
                    PhraseDetected(
                        source.toString(),
                        sourceLanguage.toLowerCase(),
                        languageName,
                        null,
                        true
                    )
                } ?: if (behaviors.ignoreDetection() || source.isEmpty())
                    null
                else {
                    withContext(Dispatchers.IO) {
                        Phrase.instance().detectLanguage(source.toString())
                    }
                }

            if (options.excludeSources.indexOfFirst {
                    it.toLowerCase() == (phraseDetected?.languageCode ?: "").toLowerCase()
                } < 0) {

            }
            /* with the detected sourceLanguage, check to see if there's any rule that check against translating from the source language at all
             */

            phraseDetected?.let { detected ->
                // halt this process if the sourceLanguage is part of the excluded list or it is same with the target language,
                if ((options.targetLanguageCode.indexOfFirst {
                        it.toLowerCase() == (detected.languageCode).toLowerCase()
                    } >= 0) || options.excludeSources.indexOfFirst {
                        it.equals(
                            detected.languageCode,
                            true
                        )
                    } > 0
                ) {
                    actionStatus = ActionStatus.SHOWING_SOURCE
                    onContentChanged(this@PhraseSpannableBuilder)
                    return@launch
                }

                // if options supports translation of only preferred sources, check if sourceLanguage is in preferred list.
                var allowTranslation =
                    if (options.behavioursOptions.behaviours.translatePreferredSourceOnly()) {
                        options.preferredSources.indexOfFirst {
                            it.equals(detected.languageCode, true)
                        } >= 0
                    } else {
                        true
                    }
                // check if source language is defined in languageTranslation preference
                allowTranslation =
                    (options.sourcePreferredTranslation.sourceTranslateRule.filter { it.sourceLanguageCode.toLowerCase() == detected.languageCode.toLowerCase() }
                        .let { sourceOptions ->
                            sourceOptions.find { sourceTranslationOption ->
                                sourceTranslationOption.targetLanguageCode.map { it.toLowerCase() }.intersect(options.targetLanguageCode.map { it.toLowerCase() })
                                    .isNotEmpty()
                                        || sourceTranslationOption.targetLanguageCode.contains(
                                    "*"
                                )
                            }?.let { true }
                                ?: !options.behavioursOptions.behaviours.translatePreferredSourceOnly()
                        }) || allowTranslation
                // if source language is neither in sourcePreferredTranslation nor preferredSources and BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY is set, actionLable for translation shouldn't be appended to this string.
                if (!allowTranslation) {
                    actionStatus = ActionStatus.SHOWING_SOURCE
                    onContentChanged(this@PhraseSpannableBuilder)
                    return@launch
                }
            }
            // another check to ensure nullable phraseDetected is only allowed for BEHAVIOR_IGNORE_DETECTION
            if (phraseDetected == null && !behaviors.ignoreDetection()) {
                onContentChanged(this@PhraseSpannableBuilder)
                return@launch
            }
            // final check to confirm original text is not empty.
            // also, actionLabel is appended only if BEHAVIOR_HIDE_TRANSLATE_PROMPT is not set and source is not same with the content of PhraseSpannableBuilder (to avoid text repetition)
            if (!source.isNullOrBlank() && !behaviors.hideTranslatePrompt() && (this@PhraseSpannableBuilder.toString() == source.toString())) {
                appendln("\n")
                val start = length
                append(options.translateText.invoke(phraseDetected))
                // add clickableSpan to actionLabel
                setSpan(
                    SpannablePhraseClickableSpan(),
                    start,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            }
            actionStatus = ActionStatus.SHOWING_WITH_TRANSLATE_ACTION
            onContentChanged(this@PhraseSpannableBuilder)
        }
    }

    /**
     * build string to include original text and a translated actionLabel and the translated string
     * Phrase adds translate resultActionLabel based options provided via phraseOptions.
     * this is called after translation has happened.
     */
    private fun buildTranslatedPhraseSpan() {
        val options = options() ?: return
        val optionBehavior = options.behavioursOptions.behaviours
        phraseTranslation?.let { phraseTranslation ->
            init()
            // if BEHAVIOR_REPLACE_SOURCE_TEXT is set, we want to replace the source text with translation.
            if (optionBehavior.replaceSourceText()) {
                clear()
            }
            // resultActionLabel is appeneded to result is BEHAVIOR_HIDE_TRANSLATE_PROMPT is not set.
            if (!optionBehavior.hideTranslatePrompt()) {
                appendln("\n")
                var start = length
                append(options.translateFrom.invoke(phraseTranslation))
                // add clickableSpan to resultActionLabel
                setSpan(
                    SpannablePhraseClickableSpan(),
                    start,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // style translation medium name.
                if (!optionBehavior.hideSignature()) {
                    start = length
                    append("${phraseTranslation.translationMediumName}")
                    options.behavioursOptions.signatureTypeFace?.let { typeFace ->
                        setSpan(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) TypefaceSpan(
                                typeFace
                            ) else CustomTypefaceSpan(
                                typeFace
                            ),
                            start,
                            length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    options.behavioursOptions.signatureColor.let { color ->
                        setSpan(
                            ForegroundColorSpan(color),
                            start,
                            length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
            appendln("\n")
            append(phraseTranslation.translation)
            actionStatus = ActionStatus.SHOWING_TRANSLATED
            onContentChanged(this@PhraseSpannableBuilder)
        } ?: kotlin.run {
            buildTranslateActionSpan(source)
        }

    }

    /**
     * we have to clear spans and text before appending source. This is to ensure the visual presentation doesn't change after phrase and processed the source and added a translation prompt
     */
    private fun init() {
        clearSpans()
        clear()
        append(source)
    }

    /**
     * customer ClickableSpan impementation to handle click actions on actionLabel and resultActionLabel
     */
    inner class SpannablePhraseClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            onActionClick(actionStatus)
            // if text is currently prompting user to translate
            if (actionStatus == ActionStatus.SHOWING_WITH_TRANSLATE_ACTION) {
                // notify listener of tranalation
                onPhraseTranslating()
                // translate text
                scope.launch {
                    val options = options()
                    phraseTranslation =
                        withContext(Dispatchers.IO) {
                            Phrase.instance().translate(source.toString(), options)
                        }
                    // build translated string
                    buildTranslatedPhraseSpan()
                    // notify listener of translation
                    onPhraseTranslated(phraseTranslation)
                }

            } else {
                // resultActionLabel is clicked, show original state of text
                buildTranslateActionSpan(source)
            }
            widget.invalidate()

        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    /**
     * for implementing fontSpan for Phrase credit or signature
     */
    internal class CustomTypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeFace(ds, typeface)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeFace(paint, typeface)
        }

        companion object {
            private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
                paint.typeface = tf
            }
        }
    }
}
