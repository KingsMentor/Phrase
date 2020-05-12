package xyz.belvi.phrase.helpers

import xyz.belvi.phrase.options.PhraseTranslation

interface PhraseTranslateListener {
        fun onPhraseTranslating()
        fun onPhraseTranslated(phraseTranslation: PhraseTranslation?)
    }
