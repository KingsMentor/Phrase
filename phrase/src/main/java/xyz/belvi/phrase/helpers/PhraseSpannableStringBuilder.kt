package xyz.belvi.phrase.helpers

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseSpannableStringBuilder constructor(
    private var source: String,
    val phraseOptions: PhraseOptions? = null
) :
    SpannableStringBuilder(source),
    SpannablePhraseInterface {

    val options = phraseOptions ?: Phrase.instance().phraseImpl.phraseOptions
    private var showingTranslation = false
    private lateinit var phraseTranslation: PhraseTranslation

    init {
        buildWithoutTranslation()
    }

    override fun notifyUpdate(text: PhraseSpannableStringBuilder) {
        if (showingTranslation) {
            buildWithoutTranslation()
        } else {
            buildShowingTranslation()
        }
        showingTranslation = !showingTranslation
    }


    fun updateSource(source: String) {
        this.source = source
        showingTranslation = false
        buildWithoutTranslation()

    }

    private fun buildWithoutTranslation() {
        init()
        requireNotNull(options)
        val detect = Phrase.instance().detectLanguage(source)
        if (detect.code != options.targetLanguageCode) {
            appendln("\n")
            val start = length
            append(options.translateText ?: "Translate")
            setSpan(
                SpannablePhraseClickableSpan(),
                start,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun buildShowingTranslation() {
        init()
        appendln("\n")
        val start = length
        append(
            options?.translateFrom?.invoke(phraseTranslation)
                ?: "Translate with ${phraseTranslation.translationMedium.name()}"
        )
        setSpan(
            SpannablePhraseClickableSpan(),
            start,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        appendln("\n")
        append(phraseTranslation.translation)
    }

    private fun init() {
        clear()
        append(source)
    }

    inner class SpannablePhraseClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            if (!::phraseTranslation.isInitialized || phraseTranslation.source.text != source) {
                phraseTranslation = Phrase.instance().translate(source, phraseOptions)
            }
            notifyUpdate(this@PhraseSpannableStringBuilder)
        }

    }
}