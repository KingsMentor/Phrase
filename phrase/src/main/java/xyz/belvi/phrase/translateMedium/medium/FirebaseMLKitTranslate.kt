package xyz.belvi.phrase.translateMedium.medium

import android.content.Context
import androidx.annotation.RawRes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.TranslationMedium

class FirebaseMLKitTranslate(
    context: Context,
    @RawRes authCredentials: Int
) : TranslationMedium() {
    override fun translate(text: String, targeting: String): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun name(): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun detect(text: String): PhraseDetected? {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
