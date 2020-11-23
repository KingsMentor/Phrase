package xyz.belvi.phrase.translateMedium.medium

import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium
import xyz.belvi.phrase.translateMedium.containsKey
import xyz.belvi.phrase.translateMedium.medium.retrofit.ApiClient
import xyz.belvi.phrase.translateMedium.medium.retrofit.DetectLanguageApi

class DetectLanguage(private val apiKey: String) : TranslationMedium() {

    private val mURL = "https://ws.detectlanguage.com/"
    private val apiClient = ApiClient.retrofit(mURL).create(DetectLanguageApi::class.java)

    override suspend fun translate(
        text: String,
        targeting: String
    ): String {
        return ""
    }

    override fun name(): String {
        return "DetectLanguage"
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
        return phraseCache[cacheKey(text, targeting)] != null
    }

    override suspend fun detect(
        text: String,
        targeting: String
    ): PhraseDetected? {
        val key = cacheKey(text, targeting)
        if (phraseCache.containsKey(key))
            return phraseCache[key]?.detectedSource?.copy(fromCache = true)
        val deepLTranslation = try {
            apiClient.detect(apiKey, text).data.detections.firstOrNull()
        } catch (e: Exception) {
            null
        }
        val detect = deepLTranslation?.language ?: ""

        return PhraseDetected(
            text,
            detect,
            Languages.values().find { it.code == detect.toLowerCase() }?.name ?: detect,
            name()
        ).also {
            cache(key, "", it)
        }
    }

    data class Detection(val language: String, val isReliable: Boolean)
    data class Detections(val detections: List<Detection>)
    data class DetectionResponse(val data: Detections)
}