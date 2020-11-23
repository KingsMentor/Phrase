package xyz.belvi.phrase.translateMedium

import android.util.LruCache
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseTranslation

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


abstract class TranslationMedium(cacheSize: Int = 100) {

    /**
     * for handling cache for translation. To avoid running multiple translation for same text, text are cached in a hashMap. Subsequent calls return same successful result.
     * Ensure, result is only store on successful response to avoid caching a failed response
     */
    protected val phraseCache = LruCache<String, PhraseTranslation>(cacheSize)

    /**
     * implementation of langauage detection goes here
     * @param text is the original text
     * @param targeting is the targeted language for translation
     * @return PhraseDetected
     */
    abstract suspend fun detect(
        text: String,
        targeting: String
    ): PhraseDetected?

    /**
     * implementation of langauage translation goes here
     * @param text is the original text
     * @param targeting is the targeted language for translation
     * @return text
     */
    abstract suspend fun translate(text: String, targeting: String): String

    /**
     * name of the engine running this translation and detection.
     * This is used for translation credit.
     * @return String : name of TranslationMedium engine.
     */
    abstract fun name(): String

    abstract fun cacheKey(
        text: String,
        targeting: String
    ): String

    abstract fun clearCache()

    /**
     * a check implementation for when a text has already been translated and cached.
     * @param text : original text
     * @param targeting is the targeted language for translation
     * @return true if text is in cached. Otherwise, false.
     */
    abstract fun isTranslationInCached(
        text: String,
        targeting: String
    ): Boolean

    protected fun cache(
        key: String,
        translation: String? = null,
        detectedPhrase: PhraseDetected? = null,
        detectedLanguageCode: String? = null,
        detectedLanguageName: String? = null
    ) {
        val cache = phraseCache[key]?.copy(
            translation = translation ?: phraseCache[key]?.translation!!,
            detectedSource = detectedPhrase ?: phraseCache[key]?.detectedSource
        )
        detectedLanguageCode?.let {
            cache?.detectedSource?.languageCode = it
        }
        detectedLanguageName?.let {
            cache?.detectedSource?.languageName = it
        }

        if (phraseCache.containsKey(key)) {
            phraseCache.put(key, cache)
        } else {
            phraseCache.put(
                key,
                PhraseTranslation(translation ?: "", name(), detectedPhrase, false)
            )
        }
    }
}

fun LruCache<String, PhraseTranslation>.containsKey(key: String): Boolean {
    return this[key] != null
}
