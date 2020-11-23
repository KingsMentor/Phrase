package xyz.belvi.phrase.translateMedium.medium

import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import kotlinx.coroutines.tasks.await
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium


class FirebaseMLKitTranslate(
    private val conditions: FirebaseModelDownloadConditions = FirebaseModelDownloadConditions.Builder()
        .build()
) :
    TranslationMedium() {
    override suspend fun translate(
        text: String,
        targeting: String
    ): String {
        val key = cacheKey(text, targeting)
        if (isTranslationInCached(key, targeting))
            return phraseCache[key]?.translation!!
        val options = FirebaseTranslatorOptions.Builder()
            .setTargetLanguage(
                FirebaseTranslateLanguage.languageForLanguageCode(targeting)
                    ?: FirebaseTranslateLanguage.EN
            )
            .build()
        val englishGermanTranslator =
            FirebaseNaturalLanguage.getInstance().getTranslator(options)
        englishGermanTranslator.downloadModelIfNeeded(conditions).await()
        val result = englishGermanTranslator.translate(text).await()
        if (!result.isNullOrBlank())
            cache(key, result)
        return result
    }

    override fun name(): String {
        return "Google"
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
        val language =
            FirebaseNaturalLanguage.getInstance().languageIdentification.identifyLanguage(text)
                .await()
        val languageName =
            Languages.values().find { it.code == language.toLowerCase() }?.name ?: language
        return PhraseDetected(text, language, languageName, name()).also {
            cache(key, detectedPhrase = it)
        }
    }
}
