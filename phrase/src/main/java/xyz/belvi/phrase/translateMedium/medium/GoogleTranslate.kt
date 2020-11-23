package xyz.belvi.phrase.translateMedium.medium

import android.content.Context
import androidx.annotation.RawRes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.io.InputStream

class GoogleTranslate(
    context: Context,
    @RawRes authCredentials: Int
) : TranslationMedium() {

    val translate by lazy {
        GlobalScope.async(Dispatchers.IO) {
            val stream: InputStream = context.resources.openRawResource(authCredentials)
            val myCredentials = GoogleCredentials.fromStream(stream)
            val translateOptions: TranslateOptions =
                TranslateOptions.newBuilder().setCredentials(myCredentials).build()
            translateOptions.service
        }
    }

    override suspend fun translate(
        text: String,
        targeting: String
    ): String {
        val key = cacheKey(text, targeting)
        if (isTranslationInCached(key, targeting))
            return phraseCache[key]?.translation!!
        val result = translate.await().translate(
            text,
            Translate.TranslateOption.targetLanguage(targeting),
            Translate.TranslateOption.format("text")

        ).translatedText
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
        val result = translate.await().translate(
            text,
            Translate.TranslateOption.targetLanguage(targeting.toLowerCase()),
            Translate.TranslateOption.format("text")
        )
        val languageName =
            Languages.values().find { it.code == result.sourceLanguage.toLowerCase() }?.name
                ?: result.sourceLanguage

        return PhraseDetected(text, result.sourceLanguage, languageName, name()).also {
            if (!result.translatedText.isNullOrBlank()) {
                cache(key, result.translatedText, it)
            }
        }
    }

}
