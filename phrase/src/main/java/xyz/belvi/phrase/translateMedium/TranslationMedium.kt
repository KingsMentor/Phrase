package xyz.belvi.phrase.translateMedium

import xyz.belvi.phrase.options.PhraseDetected

/**
 * Base implementation for all TranslationMedium supported by Phrase.
 * To build your customer TranslationMedium, extend this class and implement the appropriate members.
 * See the implementation mentioned below for example on building a custom TranslationMedium
 * @see xyz.belvi.phrase.translateMedium.medium.GoogleTranslate
 * @see xyz.belvi.phrase.translateMedium.medium.DeepL
 * @see xyz.belvi.phrase.translateMedium.medium.FirebaseMLKitTranslate
 * @ee xyz.belvi.phrase.translateMedium.medium.DetectLanguage
 *
 */
abstract class TranslationMedium {
    /**
     * for handling cache for detection. To avoid running multiple detection for same text, text are cached in a hashMap. Subsequent calls return same successful result.
     * Ensure, result is only store on successful response to avoid caching a failed response
     */
    protected val cacheDetected = HashMap<String, PhraseDetected>()
    /**
     * for handling cache for translation. To avoid running multiple translation for same text, text are cached in a hashMap. Subsequent calls return same successful result.
     * Ensure, result is only store on successful response to avoid caching a failed response
     */
    protected val cacheTranslation = HashMap<String, String>()

    /**
     * implementation of langauage detection goes here
     * @param text is the original text
     * @param targeting is the targeted language for translation
     * @return PhraseDetected
     */
    abstract suspend fun detect(text: String, targeting: String = ""): PhraseDetected?
    /**
     * implementation of langauage translation goes here
     * @param text is the original text
     * @param sourceLanguage detected sourceLanguage of the original text
     * @param targeting is the targeted language for translation
     * @return text
     */
    abstract suspend fun translate(text: String, sourceLanguage: String, targeting: String): String

    /**
     * name of the engine running this translation and detection.
     * This is used for translation credit.
     * @return String : name of TranslationMedium engine.
     */
    abstract fun name(): String

    /**
     * a check implementation for when a text has already been translated and cached.
     * @param text : original text
     * @param sourceLanguage detected sourceLanguage of the original text
     * @param targeting is the targeted language for translation
     * @return true if text is in cached. Otherwise, false.
     */
    abstract fun isTranslationInCached(
        text: String,
        sourceLanguage: String,
        targeting: String
    ): Boolean
}
