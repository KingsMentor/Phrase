package com.example.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.behaviour
import xyz.belvi.phrase.helpers.PhraseSpannableBuilder
import xyz.belvi.phrase.options
import xyz.belvi.phrase.options.Behaviour
import xyz.belvi.phrase.options.SourceTranslationRule
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
        val pOptions = options {
            targeting = listOf(target.text.toString())
            preferredSources = listOf("es", "yo")
            sourceTranslation = listOf(SourceTranslationRule("fr", listOf("en")))
            behaviour = behaviour {
                flags = setOf(Behaviour.BEHAVIOR_TRANSLATE_PREFERRED_OPTION_ONLY)
                signatureTypeface = font
                signatureColor =
                    ContextCompat.getColor(this@MainActivity, R.color.white)
            }
            actionLabel = { detected ->
                "Translate"
            }

            resultActionLabel = { phraseTranslation ->
                "Translated from ${phraseTranslation.detectedSource?.languageName} by "
            }
        }
        val phrase = phrase {
            mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
            options = pOptions
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
            pOptions.targetLanguageCode = listOf(target.text.toString())
            phraseSpannableBuilder.updateSource(source_edit.text.toString())
        }
    }
}
