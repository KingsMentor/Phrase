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

    override suspend fun translate(text: String, sourceLanguage: String, targeting: String): String {
        val key = "$targeting:$text"
        if (cacheTranslation.containsKey(key))
            return cacheTranslation[key]!!
        val result = translate.await().translate(
            text,
            Translate.TranslateOption.targetLanguage(targeting.toLowerCase()),
            Translate.TranslateOption.format("text")

        ).translatedText
        cacheTranslation[key] = result
        return result

    }

    override fun name(): String {
        return "Google"
    }

    override suspend fun detect(text: String): PhraseDetected? {

        if (cacheDetected.containsKey(text))
            return cacheDetected[text]!!
        return translate.await().let {
            val detect = it.detect(text).language
            val languageName =
                it.listSupportedLanguages().find { it.code == detect }?.name ?: detect
            val result = PhraseDetected(text, detect, languageName, name())
            cacheDetected[text] = result
            result
        }
    }

}
