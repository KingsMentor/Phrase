package xyz.belvi.phrase.translateMedium.medium

import xyz.belvi.phrase.translateMedium.TranslationMedium

final class Smartling(val apiKey: String) : TranslationMedium() {

    override fun <T> detect(text: String): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun detectedLanguageCode(text: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun detectedLanguageName(text: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translate(text: String, target: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun name(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}