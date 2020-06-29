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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    /**
     * get the options for PhraseSpannableBuilder. this returns the default phraseOption in Pgrase.instance if this builder has not been supplied with any option.
     */
    private fun options() = phraseOptions ?: Phrase.instance().phraseImpl.phraseOptions

    private fun buildTranslateActionSpan(source: CharSequence) {
        init()
        GlobalScope.launch(Dispatchers.Main) {
            val options = options() ?: return@launch
            val behaviors = options.behavioursOptions.behaviours
            val detectedMedium =
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

            detectedMedium?.let { phraseDetected ->

                var allowTranslation =
                    if (options.behavioursOptions.behaviours.translatePreferredSourceOnly()) {
                        options.preferredSources.indexOfFirst {
                            it.equals(phraseDetected.languageCode, true)
                        } >= 0
                    } else {
                        true
                    }
                allowTranslation =
                    (options.sourcePreferredTranslation.sourceTranslateRule.filter { it.sourceLanguageCode.toLowerCase() == phraseDetected.languageCode.toLowerCase() }
                        .let { sourceOptions ->
                            sourceOptions.find { sourceTranslationOption ->
                                sourceTranslationOption.targetLanguageCode.indexOfFirst {
                                    it.equals(
                                        options.targetLanguageCode,
                                        true
                                    )
                                } >= 0 || sourceTranslationOption.targetLanguageCode.contains(
                                    "*"
                                )
                            }?.let { true }
                                ?: !options.behavioursOptions.behaviours.translatePreferredSourceOnly()
                        }) || allowTranslation
                if (!allowTranslation) {
                    actionStatus = ActionStatus.SHOWING_SOURCE
                    onContentChanged(this@PhraseSpannableBuilder)
                    return@launch
                }

                if (phraseDetected.languageCode.equals(
                        options.targetLanguageCode.toLowerCase(),
                        true
                    ) || options.excludeSources.indexOfFirst {
                        it.equals(
                            phraseDetected.languageCode,
                            true
                        )
                    } > 0
                ) {
                    actionStatus = ActionStatus.SHOWING_SOURCE
                    onContentChanged(this@PhraseSpannableBuilder)
                    return@launch
                }
            }

            if (detectedMedium == null && !behaviors.ignoreDetection()) {
                onContentChanged(this@PhraseSpannableBuilder)
                return@launch
            }
            if (!source.isNullOrBlank() && !behaviors.hideTranslatePrompt() && (this@PhraseSpannableBuilder.toString() == source.toString())) {
                appendln("\n")
                val start = length
                append(options.translateText.invoke(detectedMedium))
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

    private fun buildTranslatedPhraseSpan() {
        val options = options() ?: return
        val optionBehavior = options.behavioursOptions.behaviours
        phraseTranslation?.let { phraseTranslation ->
            init()
            appendln("\n")
            if (optionBehavior.replaceSourceText()) {
                clear()
            }
            if (!optionBehavior.hideTranslatePrompt()) {
                var start = length
                append(options.translateFrom.invoke(phraseTranslation))
                setSpan(
                    SpannablePhraseClickableSpan(),
                    start,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
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
                appendln("\n")
            }
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

    inner class SpannablePhraseClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            onActionClick(actionStatus)
            if (actionStatus == ActionStatus.SHOWING_WITH_TRANSLATE_ACTION) {
                onPhraseTranslating()
                GlobalScope.launch(Dispatchers.Main) {
                    val options = options()
                    phraseTranslation =
                        withContext(Dispatchers.IO) {
                            Phrase.instance().translate(source.toString(), options)
                        }
                }
                buildTranslatedPhraseSpan()
                onPhraseTranslated(phraseTranslation)
            } else {
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
