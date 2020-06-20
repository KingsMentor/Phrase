package com.example.phrase

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.detected
import kotlinx.android.synthetic.main.activity_main.source_edit
import kotlinx.android.synthetic.main.activity_main.spanish_text
import kotlinx.android.synthetic.main.activity_main.target
import kotlinx.android.synthetic.main.activity_main.translated
import kotlinx.android.synthetic.main.activity_main.update_source
import kotlinx.android.synthetic.main.activity_main.yoruba
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.helpers.PhraseSpannableBuilder
import xyz.belvi.phrase.options
import xyz.belvi.phrase.options.PhraseTranslation
import xyz.belvi.phrase.options.SourceTranslationOption
import xyz.belvi.phrase.phrase
import xyz.belvi.phrase.translateMedium.Languages
import xyz.belvi.phrase.translateMedium.medium.DeepL
import xyz.belvi.phrase.translateMedium.medium.FirebaseMLKitTranslate
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
            mediums = listOf(FirebaseMLKitTranslate())
            options {
                targeting = target.text.toString()
                behaviourFlags {
                    switchAnim
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
            object : PhraseSpannableBuilder("", null) {
                override fun onPhraseTranslating() {
                }

                override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                }

                override fun onActionClick(showingTranslation: Boolean) {
                    Log.i(MainActivity::class.java.name, showingTranslation.toString())
                }

                override fun onContentChanged(content: PhraseSpannableBuilder) {
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
            })
            phraseSpannableBuilder.updateSource(source_edit.text.toString())
        }
    }
}
