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
        sourceLanguage: String,
        targeting: String
    ): String {
        val key = "$sourceLanguage:$targeting:$text"
        if (cacheTranslation.containsKey(key))
            return cacheTranslation[key]!!
        val result = translate.await().translate(
            text,
            Translate.TranslateOption.sourceLanguage(sourceLanguage),
            Translate.TranslateOption.targetLanguage(targeting),
            Translate.TranslateOption.format("text")

        ).translatedText
        cacheTranslation[key] = result
        return result

    }

    override fun name(): String {
        return "Google"
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
        val result = translate.await().translate(
            text,
            Translate.TranslateOption.targetLanguage(targeting.toLowerCase()),
            Translate.TranslateOption.format("text")
        )
        val key = "${result.sourceLanguage}:$targeting:$text"
        if (!result.translatedText.isNullOrBlank())
            cacheTranslation[key] = result.translatedText
        val languageName =
            Languages.values().find { it.code == result.sourceLanguage.toLowerCase() }?.name
                ?: result.sourceLanguage
        return PhraseDetected(text, result.sourceLanguage, languageName, name())
    }

}
