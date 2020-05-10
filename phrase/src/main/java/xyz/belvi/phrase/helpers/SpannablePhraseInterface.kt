package xyz.belvi.phrase.helpers

import xyz.belvi.phrase.options.PhraseTranslation

interface SpannablePhraseInterface {
    fun notifyUpdate(phraseTranslation: PhraseTranslation?)
    fun translating()
}