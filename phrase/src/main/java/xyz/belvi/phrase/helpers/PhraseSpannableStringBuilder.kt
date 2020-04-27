package xyz.belvi.phrase.helpers

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation

open class PhraseSpannableStringBuilder constructor(
    private val source: String,
    val phraseOptions: PhraseOptions? = null
) :
    SpannableStringBuilder(source),
    SpannablePhraseInterface {

    private var showingTranslation = false
    private lateinit var phraseTranslation: PhraseTranslation

    init {
        val options = phraseOptions ?: Phrase.instance().phraseImpl.phraseOptions
        appendln("\n")
        this.apply {
            val start = length
            append(options?.translateText ?: "Translate")
            setSpan(
                SpannablePhraseClikableSpan(),
                start,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    }

    override fun notifyUpdate(text: PhraseSpannableStringBuilder) {
        clear()
        append(source)
        appendln("\n")
        val start = length
        val options = phraseOptions ?: Phrase.instance().phraseImpl.phraseOptions
        if (showingTranslation) {
            append(options?.translateText ?: "Translate")
            setSpan(
                SpannablePhraseClikableSpan(),
                start,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            append(options?.translateFrom?.invoke(phraseTranslation)?:"Translate with ${phraseTranslation.translationMedium.name()}")
            setSpan(
                SpannablePhraseClikableSpan(),
                start,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            appendln("\n")
            append(phraseTranslation.translation)
        }
        showingTranslation = !showingTranslation
    }

    inner class SpannablePhraseClikableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            if (!::phraseTranslation.isInitialized) {
                phraseTranslation = Phrase.instance().translate(source, phraseOptions)
            }
            notifyUpdate(this@PhraseSpannableStringBuilder)
        }

    }
}