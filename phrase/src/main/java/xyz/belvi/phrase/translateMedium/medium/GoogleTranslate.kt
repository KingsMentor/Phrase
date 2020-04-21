package xyz.belvi.phrase.translateMedium.medium

import android.content.Context
import android.os.StrictMode
import androidx.annotation.RawRes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Detection
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.io.InputStream

class GoogleTranslate(
    context: Context, @RawRes authCredentials: Int,
    override val targetedLanguage: String
) : TranslationMedium(targetedLanguage) {
    private val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

    private val stream: InputStream = context.resources.openRawResource(authCredentials)
    private val myCredentials: GoogleCredentials = GoogleCredentials.fromStream(stream);
    private val translateOptions: TranslateOptions =
        TranslateOptions.newBuilder().setCredentials(myCredentials).build();
    private val translate = translateOptions.service

    init {
        StrictMode.setThreadPolicy(policy)
    }

    override fun translate(text: String): String {

        return translate.translate(text, Translate.TranslateOption.targetLanguage(targetedLanguage))
            .translatedText
    }

    override fun <T> detect(text: String): T {
        return translate.detect(text) as T
    }

    override fun detectedLanguage(text: String): String {
        return (detect(text) as Detection).language
    }
}
