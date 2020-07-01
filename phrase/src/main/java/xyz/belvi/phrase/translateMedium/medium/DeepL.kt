package xyz.belvi.phrase.translateMedium.medium

import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium
import xyz.belvi.phrase.translateMedium.medium.retrofit.ApiClient
import xyz.belvi.phrase.translateMedium.medium.retrofit.DeepLApi

class DeepL(private val apiKey: String) : TranslationMedium() {

    private val mURL = "https://api.deepl.com/"
    private val apiClient = ApiClient.retrofit(mURL).create(DeepLApi::class.java)

    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targeting: String
    ): String {
        val key = "$sourceLanguage:$targeting:$text"
        if (cacheTranslation.containsKey(key))
            return cacheTranslation[key]!!
        val deepLTranslation =
            try {
                apiClient.translate(apiKey, text, targeting).translations.firstOrNull()
            } catch (e: Exception) {
                null
            }
        val result = deepLTranslation?.text ?: ""
        if (!result.isNullOrBlank())
            cacheTranslation[key] = result
        return result
    }

    override fun name(): String {
        return "DeepL"
    }

    override fun isTranslationInCached(
        text: String,
        sourceLanguage: String,
        targeting: String
    ): Boolean {
        val key = "$sourceLanguage:$targeting:$text"
        return cacheTranslation.containsKey(key)
    }

    override suspend fun detect(text: String, targeting: String): PhraseDetected? {
        if (cacheDetected.containsKey(text))
            return cacheDetected[text]!!.copy(fromCache = true)
        val deepLTranslation = try {
            apiClient.translate(apiKey, text, targeting).translations.firstOrNull()
        } catch (e: Exception) {
            null
        }
        return deepLTranslation?.let {
            val detect = deepLTranslation?.detected_source_language ?: ""
            val result = PhraseDetected(
                text,
                detect,
                Languages.values().find { it.code == detect.toLowerCase() }?.name ?: detect,
                name()
            )
            cacheDetected[text] = result
            val key = "$detect:$targeting:$text"
            cacheTranslation[key] = deepLTranslation?.text ?: ""
            return result
        }
    }

    data class DeepLTranslation(val detected_source_language: String, val text: String)
    data class DeepLTranslationResponse(val translations: List<DeepLTranslation>)
}