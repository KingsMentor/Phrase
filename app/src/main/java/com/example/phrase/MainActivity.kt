package com.example.phrase

import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.helpers.PhraseSpannableBuilder
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.phrase
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate

class MainActivity : AppCompatActivity() {

    private lateinit var phraseSpannableBuilder: PhraseSpannableBuilder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val font = Typeface.createFromAsset(assets, "rb.ttf")

        // setting up phrase
        phrase {
            mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
            options {
                targetting = "en"
                behaviourFlags {
                    flags = setOf()
                    signatureTypeface = font
                    signatureColor =
                        ContextCompat.getColor(this@MainActivity, R.color.white)
                }
                actionLabel = "Translate"
                resultActionLabel = {
                    detected.text =
                        "Detected Language Source: " + it.detectedSource?.languageName ?: ""
                    "Translated with "
                }
            }
        }

        phraseSpannableBuilder =
            object : PhraseSpannableBuilder("") {
                override fun onPhraseTranslating() {
                }

                override fun buildSpannableString(phraseTranslation: PhraseTranslation?) {
                    super.buildSpannableString(phraseTranslation)
                    translated.text = phraseSpannableBuilder
                }
            }



        Phrase.instance().bindTextView(yoruba)
        yoruba.setText(R.string.yoruba)

        spanish_text.prepare(getString(R.string.spanish))

        update_source.setOnClickListener {
            phraseSpannableBuilder.updateSource(source_edit.text.toString())
        }

    }
}
