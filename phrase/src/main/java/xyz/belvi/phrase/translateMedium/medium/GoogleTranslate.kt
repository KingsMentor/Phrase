package xyz.belvi.phrase.translateMedium.medium

import android.content.Context
import androidx.annotation.RawRes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Detection
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.*
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.io.InputStream

class GoogleTranslate(
    context: Context, @RawRes authCredentials: Int
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

    override fun translate(text: String, target: String): String {
        return runBlocking {
            withContext(Dispatchers.IO) {
                translate.await().translate(
                    text,
                    Translate.TranslateOption.targetLanguage(target)
                ).translatedText
            }
        }
    }

    override fun name(): String {
        return "Google"
    }

    override fun <T> detect(text: String): T {
        return runBlocking {
            withContext(Dispatchers.IO) {
                translate.await().detect(text) as T
            }
        }
    }

    override fun detectedLanguageCode(text: String): String {
        return (detect(text) as Detection).language
    }

    override fun detectedLanguageName(text: String): String {
        return runBlocking {
            withContext(Dispatchers.IO) {
                translate.await().let {
                    val detect = translate.await().detect(text).language
                    it.listSupportedLanguages().find { it.code == detect }?.name ?: detect
                }
            }
        }
    }
}
