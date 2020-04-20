package xyz.belvi.phrase

import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.TranslationMedium
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate

data class Phrase internal constructor(
    var translationMedium: List<TranslationMedium>
) {
    internal lateinit var phraseOptions: PhraseOptions

    companion object {
        fun with(translationMedium: TranslationMedium): PhraseBuilderUseCase {
            return PhraseImpl.Companion.Builder(translationMedium)
        }

        fun instance(): Phrase {
            return PhraseImpl.phrase
        }
    }

    fun translate(text: String): String {
        return translationMedium.first().translate(text)
    }
}