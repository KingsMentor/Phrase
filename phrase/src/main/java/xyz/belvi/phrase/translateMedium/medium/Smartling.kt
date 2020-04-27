package xyz.belvi.phrase.translateMedium.medium

import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.translateMedium.TranslationMedium

final class Smartling(val apiKey: String) : TranslationMedium() {
    override fun detect(text: String): PhraseDetected {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun name(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translate(text: String, targeting: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}