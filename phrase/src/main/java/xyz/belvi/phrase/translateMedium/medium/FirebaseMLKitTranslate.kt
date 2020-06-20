package xyz.belvi.phrase.translateMedium.medium

import android.content.Context
import androidx.annotation.RawRes
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import kotlinx.coroutines.tasks.await
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium

class FirebaseMLKitTranslate : TranslationMedium() {
    override suspend fun translate(text: String, targeting: String): String {
        return "Sample"
    }

    override fun name(): String {
        return "Google"
    }

    override suspend fun detect(text: String): PhraseDetected? {
        val language =
            FirebaseNaturalLanguage.getInstance().languageIdentification.identifyLanguage(text)
                .await()
        val languageName = Languages.values().find { it.code == language }?.name ?: language
        return PhraseDetected(text, language, languageName, name())
    }
}
