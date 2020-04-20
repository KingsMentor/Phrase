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
    private val targetedLanguage: String
) : TranslationMedium() {
    private val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

    private val stream: InputStream = context.resources.openRawResource(authCredentials)
    private val myCredentials: GoogleCredentials = GoogleCredentials.fromStream(stream);
    private val translateOptions: TranslateOptions =
        TranslateOptions.newBuilder().setCredentials(myCredentials).build();
    private val translate = translateOptions.service

    init {
        StrictMode.setThreadPolicy(policy)
    }

    override fun detect(text: String): Detection {
        return translate.detect(text)
    }

    override fun translate(text: String): String {

        return translate.translate(text, Translate.TranslateOption.targetLanguage(targetedLanguage))
            .translatedText
    }

    override fun detectedLanguage(text: String): String {
        return detect(text).language
    }
}
