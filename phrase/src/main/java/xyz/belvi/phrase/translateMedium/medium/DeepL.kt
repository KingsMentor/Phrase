package xyz.belvi.phrase.translateMedium.medium

import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.TranslationMedium

final class DeepL(val apiKey: String) : TranslationMedium() {

    override suspend fun translate(text: String, sourceLanguage: String, targeting: String): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun name(): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun detect(text: String): PhraseDetected? {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
