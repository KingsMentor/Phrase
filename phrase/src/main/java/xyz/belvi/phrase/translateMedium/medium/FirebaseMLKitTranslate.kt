package xyz.belvi.phrase.translateMedium.medium

import android.content.Context
import androidx.annotation.RawRes
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.TranslationMedium

class FirebaseMLKitTranslate(
    context: Context,
    @RawRes authCredentials: Int
) : TranslationMedium() {
    override suspend fun translate(text: String, targeting: String): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun name(): String {
        return "Google"
    }

    override suspend fun detect(text: String): PhraseDetected? {
        val language =
            FirebaseNaturalLanguage.getInstance().languageIdentification.identifyLanguage(text).result
                ?: ""
        return PhraseDetected(text, language, language, name())
    }
}
