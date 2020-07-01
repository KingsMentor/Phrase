package xyz.belvi.phrase

import android.widget.TextView
import xyz.belvi.phrase.helpers.PhraseTranslateListener
import xyz.belvi.phrase.options.BehaviourOptions
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.TranslationMedium

/**
 * Define a phrase instance that will be used throughout the lifetime of the application
 */
fun phrase(phraseBuilder: Phrase.Builder.() -> Unit): Phrase =
    Phrase.Builder().apply(phraseBuilder).build()

/**
 * Sets any custom options that should be used
 */
fun options(phraseOptions: PhraseImpl.OptionsBuilder.() -> Unit): PhraseOptions {
    return PhraseImpl.OptionsBuilder().apply(phraseOptions).build()
}

/**
 * Sets any behavior that should be associated with PhraseOption
 */
fun behaviour(behaviourOptions: PhraseImpl.BehaviourOptionsBuilder.() -> Unit): BehaviourOptions {
    return PhraseImpl.BehaviourOptionsBuilder().apply(behaviourOptions).build()
}

class Phrase internal constructor() {

    internal val phraseImpl = PhraseImpl()

    companion object {
        private val phrase = Phrase()
        /**
         * Get current instance of Phrase. Only one instance tuns through the lifeCycle of the application.
         */
        fun instance(): Phrase {
            return phrase
        }
    }

    /**
     *
     * bind a textView with Phrase. Phrase will handle language detection and translation for text changes in @param textView
     * @param textView the textView to be binded to Phrase
     * @param sourceLanguage is the language for the text in this textView. if provided, Phrase will ignore language detection for this textView.
     * @param options is PhraseOptions to be used by Phrase in handling translation and detection. If not provided, Phrase uses @see defaultOption
     * @param phraseTranslateListener is for listening to Phrase activities happening in @param textView
     *
     */
    fun bindTextView(
        textView: TextView,
        sourceLanguage: String? = null,
        options: PhraseOptions? = null,
        phraseTranslateListener: PhraseTranslateListener? = null
    ) {
        return phraseImpl.bindTextView(textView, sourceLanguage, options, phraseTranslateListener)
    }

    /**
     * change translation mediums used by Phrase
     */
    fun setTranslationMedium(translationMediums: List<TranslationMedium>) {
        phraseImpl.setTranslationMediums(translationMediums)
    }

    /**
     * update default PhraseOption used by Phrase
     */
    fun updateOptions(options: PhraseOptions) {
        phraseImpl.updateOptions(options)
    }

    /**
     * get default PhraseOption used by Phrase.
     */
    fun defaultOption() = phrase.phraseImpl.phraseOptions


    /**
     * translate a text
     *
     * @param options is used by Phrase for processing translation. If non is provided, Default PhraseOption provided when initialising Phrase will be used
     * @return PhraseTranslation
     * @see defaultOption for PhraseOption used when @param options is not provided
     *
     */
    suspend fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation?{
        return phraseImpl.translate(text, options)
    }

    /**
     * detect language of @param text
     *
     * @param options is used by Phrase for processing translation. If non is provided, Default PhraseOption provided when initialising Phrase will be used
     * @return PhraseDetected
     * @see defaultOption for PhraseOption used when @param options is not provided
     */
    suspend fun detectLanguage(text: String, options: PhraseOptions? = null): PhraseDetected? {
        return phraseImpl.detect(text, options)
    }

    class Builder {
        /**
         * Define PhraseOption. Phrase can not be initialized without PhraseOptions being defined.
         */
        lateinit var options: PhraseOptions

        /**
         * Define translation medium to be used by Phrase. This should be listed in order of preference.
         * Phrase translation uses a fallback mechanism. If the first item on this list fails to process the translation, it will process the translation
         * using the second item on the list until the list is exhausted.
         */
        var mediums = listOf<TranslationMedium>()

        internal fun build(): Phrase {
            if (!::options.isInitialized)
                throw  Exception("phrase requires an option to be defined")
            phrase.phraseImpl.translationMediums = mediums
            phrase.phraseImpl.phraseOptions = options
            return phrase
        }
    }
}
