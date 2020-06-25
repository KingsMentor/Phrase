package com.example.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.helpers.PhraseSpannableBuilder
import xyz.belvi.phrase.options
import xyz.belvi.phrase.options.Behaviour
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.options.SourceTranslationOption
import xyz.belvi.phrase.phrase
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate


class MainActivity : AppCompatActivity() {

    private lateinit var phraseSpannableBuilder: PhraseSpannableBuilder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        val font = Typeface.createFromAsset(assets, "rb.ttf")
        // setting up phrase
        val phrase = phrase {
            mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
            options {
                targeting = target.text.toString()
                sourceTranslation = listOf(SourceTranslationOption("fr", listOf("en")))
                behaviourFlags {
                    flags = setOf(Behaviour.BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY)
                    signatureTypeface = font
                    signatureColor =
                        ContextCompat.getColor(this@MainActivity, R.color.white)
                }
                actionLabel = { detected ->
                    "Translate"
                }

                resultActionLabel = { phraseTranslation ->
                    detected.text =
                        "Detected Language Source: " + phraseTranslation.detectedSource?.languageName
                            ?: ""
                    "Translated with "
                }
            }
        }


        phraseSpannableBuilder =
            object : PhraseSpannableBuilder("", null) {
                override fun onContentChanged(content: PhraseSpannableBuilder) {
                    super.onContentChanged(content)
                    translated.text = content
                }
            }

        Phrase.instance().bindTextView(yoruba)
        yoruba.setText(R.string.yoruba)

        spanish_text.prepare(getString(R.string.spanish))

        translated.movementMethod = LinkMovementMethod.getInstance()
        translated.highlightColor = Color.TRANSPARENT


        update_source.setOnClickListener {
            phrase.updateOptions(options {
                targeting = target.text.toString()
                preferredSources = listOf("es", "yo")
                sourceTranslation = listOf(SourceTranslationOption("fr", listOf("en")))
                behaviourFlags {
                    flags = setOf(Behaviour.BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY)
                    signatureTypeface = font
                    signatureColor =
                        ContextCompat.getColor(this@MainActivity, R.color.white)
                }
                actionLabel = { "Translate" }
                resultActionLabel = {
                    detected.text =
                        "Detected Language Source: " + it.detectedSource?.languageName ?: ""
                    "Translated with "
                }
            })
            spanish_text.updateSource(getString(R.string.spanish))
            phraseSpannableBuilder.updateSource(source_edit.text.toString())
            yoruba.setText(R.string.yoruba)
        }
    }
}
