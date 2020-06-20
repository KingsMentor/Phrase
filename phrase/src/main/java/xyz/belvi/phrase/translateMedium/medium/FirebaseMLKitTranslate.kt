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
        val key = "$sourceLanguage:$targeting:$text"
        if (cacheTranslation.containsKey(key))
            return cacheTranslation[key]!!
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
        val result = englishGermanTranslator.translate(text).await()
        cacheTranslation[key] = result
        return result
    }

    override fun name(): String {
        return "Google"
    }

    override suspend fun detect(text: String, targeting: String): PhraseDetected? {
        if (cacheDetected.containsKey(text))
            return cacheDetected[text]!!
        val language =
            FirebaseNaturalLanguage.getInstance().languageIdentification.identifyLanguage(text)
                .await()
        val languageName = Languages.values().find { it.code == language.toLowerCase() }?.name ?: language
        val result = PhraseDetected(text, language, languageName, name())
        cacheDetected[text] = result
        return result
    }
}
