package xyz.belvi.phrase.translateMedium.medium

import android.content.Context
import android.os.StrictMode
import androidx.annotation.RawRes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.TranslateOptions
import xyz.belvi.phrase.translateMedium.TranslationMedium
import java.io.InputStream

final class GoogleTranslate(
    context: Context, @RawRes authCredentials: Int,
    val targetedLanguage: String
) : TranslationMedium() {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

    private val stream: InputStream = context.resources.openRawResource(authCredentials)
    private val myCredentials: GoogleCredentials = GoogleCredentials.fromStream(stream);
    private val translateOptions: TranslateOptions =
        TranslateOptions.newBuilder().setCredentials(myCredentials).build();
    val translate = translateOptions.service
    override fun init() {
        StrictMode.setThreadPolicy(policy)
    }

    override fun detect(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translate(text: String): String {
       return translate.translate(text).translatedText
    }
}
