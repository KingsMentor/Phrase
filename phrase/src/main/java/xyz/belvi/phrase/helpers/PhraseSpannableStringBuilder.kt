package xyz.belvi.phrase.helpers

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import xyz.belvi.phrase.Phrase

open class PhraseSpannableStringBuilder constructor(private val source: String) :
    SpannableStringBuilder(source),
    SpannablePhraseInterface {

    private var showingTranslation = false
    private var translated = ""
    private var sourceLanguage = ""

    init {
        appendln("\n")
        this.apply {
            val start = length
            append("Translate")
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
        if (showingTranslation) {
            append("Translate")
            setSpan(
                SpannablePhraseClikableSpan(),
                start,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            append("Translate from $sourceLanguage With")
            setSpan(
                SpannablePhraseClikableSpan(),
                start,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            appendln("\n")
            append(translated)
        }
        showingTranslation = !showingTranslation
    }

    inner class SpannablePhraseClikableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            if (translated.isEmpty()) {
                sourceLanguage = Phrase.instance().detectLanguage(source).name
                translated = Phrase.instance().translate(source)
            }
            notifyUpdate(this@PhraseSpannableStringBuilder)
        }

    }
}