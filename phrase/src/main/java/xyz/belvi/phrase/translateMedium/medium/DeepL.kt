package xyz.belvi.phrase.translateMedium.medium

import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium
import xyz.belvi.phrase.translateMedium.medium.retrofit.ApiClient
import xyz.belvi.phrase.translateMedium.medium.retrofit.DeepLApi

class DeepL(private val apiKey: String) : TranslationMedium() {

    private val mURL = "https://api.deepl.com/"
    private val apiClient = ApiClient.retrofit(mURL).create(DeepLApi::class.java)

    override suspend fun translate(
        text: String,
        targeting: String
    ): String {
        val key = cacheKey(text, targeting)
        if (isTranslationInCached(key, targeting))
            return phraseCache[key]?.translation!!
        val deepLTranslation =
            try {
                apiClient.translate(apiKey, text, targeting).translations.firstOrNull()
            } catch (e: Exception) {
                null
            }
        val result = deepLTranslation?.text ?: ""
        if (!result.isNullOrBlank()) {
            val detect = deepLTranslation?.detected_source_language
            cache(
                key,
                result,
                detectedLanguageCode = detect,
                detectedLanguageName = Languages.values()
                    .find { it.code == detect?.toLowerCase() }?.name ?: detect
            )
        }

        return result
    }

    override fun name(): String {
        return "DeepL"
    }

    override fun cacheKey(text: String, targeting: String): String {
        return "$targeting:${text.hashCode()}"
    }

    override fun clearCache() {
        phraseCache.evictAll()
    }

    override fun isTranslationInCached(
        text: String,
        targeting: String
    ): Boolean {
        return !phraseCache[cacheKey(text, targeting)]?.translation.isNullOrBlank()
    }

    override suspend fun detect(
        text: String,
        targeting: String
    ): PhraseDetected? {
        val key = cacheKey(text, targeting)
        if (phraseCache[key]?.detectedSource != null)
            return phraseCache[key]?.detectedSource?.copy(fromCache = true)
        val deepLTranslation = try {
            apiClient.translate(apiKey, text, targeting).translations.firstOrNull()
        } catch (e: Exception) {
            null
        }
        return deepLTranslation?.let {
            val detect = deepLTranslation.detected_source_language
            return PhraseDetected(
                text,
                detect,
                Languages.values().find { it.code == detect.toLowerCase() }?.name ?: detect,
                name()
            ).also {
                cache(key, detectedPhrase = it)
            }
        }
    }

    data class DeepLTranslation(val detected_source_language: String, val text: String)
    data class DeepLTranslationResponse(val translations: List<DeepLTranslation>)
}