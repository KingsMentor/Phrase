package xyz.belvi.phrase.translateMedium.medium

import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import kotlinx.coroutines.tasks.await
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.TranslationMedium


class FirebaseMLKitTranslate(
    private val conditions: FirebaseModelDownloadConditions = FirebaseModelDownloadConditions.Builder()
        .build(),
    private val defaultSource: Int = FirebaseTranslateLanguage.EN
) :
    TranslationMedium() {
    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targeting: String
    ): String {
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(
                FirebaseTranslateLanguage.languageForLanguageCode(sourceLanguage) ?: defaultSource
            )
            .setTargetLanguage(
                FirebaseTranslateLanguage.languageForLanguageCode(targeting)
                    ?: FirebaseTranslateLanguage.EN
            )
            .build()
        val englishGermanTranslator =
            FirebaseNaturalLanguage.getInstance().getTranslator(options)
        englishGermanTranslator.downloadModelIfNeeded(conditions).await()
        return englishGermanTranslator.translate(text).await()
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
