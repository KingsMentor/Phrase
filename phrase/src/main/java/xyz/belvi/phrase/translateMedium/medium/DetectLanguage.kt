package xyz.belvi.phrase.translateMedium.medium

import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium
import xyz.belvi.phrase.translateMedium.medium.retrofit.ApiClient
import xyz.belvi.phrase.translateMedium.medium.retrofit.DetectLanguageApi

class DetectLanguage(private val apiKey: String) : TranslationMedium() {

    private val mURL = "https://ws.detectlanguage.com/"
    private val apiClient = ApiClient.retrofit(mURL).create(DetectLanguageApi::class.java)

    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targeting: String
    ): String {
        return ""
    }

    override fun name(): String {
        return "DetectLanguage"
    }

    override suspend fun detect(text: String, targeting: String): PhraseDetected? {
        if (cacheDetected.containsKey(text))
            return cacheDetected[text]!!
        val deepLTranslation = apiClient.detect(apiKey, text).data.detections.firstOrNull()
        val detect = deepLTranslation?.language ?: ""
        val result = PhraseDetected(
            text,
            detect,
            Languages.values().find { it.code == detect.toLowerCase() }?.name ?: detect,
            name()
        )
        cacheDetected[text] = result
        return result
    }

    data class Detection(val language: String, val isReliable: Boolean)
    data class Detections(val detections: List<Detection>)
    data class DetectionResponse(val data: Detections)
}