package xyz.belvi.phrase.TranslateMedium

import android.content.Context
import androidx.annotation.RawRes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.TranslateOptions
import xyz.belvi.phrase.PhraseImpl
import java.io.InputStream

final class GoogleTranslate(
    context: Context, @RawRes authCredentials: Int,
    val targetedLanguage: String
) : TranslationMedium() {
    private val stream: InputStream = context.resources.openRawResource(authCredentials)
    private val myCredentials: GoogleCredentials = GoogleCredentials.fromStream(stream);
    private val translateOptions: TranslateOptions =
        TranslateOptions.newBuilder().setCredentials(myCredentials).build();
    val translate = translateOptions.service

    override fun detect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translate(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
