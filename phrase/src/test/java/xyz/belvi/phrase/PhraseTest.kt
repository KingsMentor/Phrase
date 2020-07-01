package xyz.belvi.phrase

import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import xyz.belvi.phrase.options.PhraseDetected
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.TranslationMedium

class PhraseTest {

    lateinit var phrase: Phrase
    lateinit var phraseOptions: PhraseOptions

    @Mock
    lateinit var googleTranslate: TranslationMedium

    @Mock
    lateinit var deepL: TranslationMedium

    @Mock
    lateinit var firebaseTranslate: TranslationMedium
    private var targetLanguage = "fr"
    var languageName = "en"
    var languageCode = "english"
    private val originalText = "This is a sample of the original text. Assume this is in English"
    var translated = "This is a sample of the translated text. Assume this is in French"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        phraseOptions = options {
            targeting = targetLanguage
            actionLabel = { detected -> "Translate With" }
            resultActionLabel =
                { translation -> "Translated from ${translation.detectedSource?.languageName} by" }
        }
        phrase = phrase {
            mediums = listOf(googleTranslate, deepL, firebaseTranslate)
            options = phraseOptions
        }

    }

    @Test
    fun `when preferred medium is not set, use translation medium for both translation and detection`() {
        Mockito.`when`(googleTranslate.name()).thenReturn("Google")
        val detected = PhraseDetected(
            originalText,
            languageCode,
            languageName,
            googleTranslate.name(),
            false
        )
        runBlocking {
            Mockito.`when`(googleTranslate.detect(originalText, targetLanguage)).thenReturn(detected)
            val result = phrase.detectLanguage(originalText)
            Assert.assertEquals(result?.detectionMediumName ?: "", googleTranslate.name())
        }
    }

    @Test
    fun `when first medium fails to detect language, falls back to second medium`() {
        Mockito.`when`(googleTranslate.name()).thenReturn("Google")
        Mockito.`when`(deepL.name()).thenReturn("DeepL")
        val detected = PhraseDetected(
            originalText,
            languageCode,
            languageName,
            deepL.name(),
            false
        )
        runBlocking {
            Mockito.`when`(googleTranslate.detect(originalText, targetLanguage)).thenReturn(null) // this will ensure google translate trial will fail and DeepL (which is the second on our list) will be used
            Mockito.`when`(deepL.detect(originalText, targetLanguage)).thenReturn(detected)
            val result = phrase.detectLanguage(originalText)
            Assert.assertEquals(result?.detectionMediumName ?: "", deepL.name())
        }
    }
}