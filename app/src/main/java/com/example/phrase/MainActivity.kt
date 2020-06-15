package com.example.phrase

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.phrase
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val font = Typeface.createFromAsset(assets, "rb.ttf")


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
                    detected.text = "Detected Language Source: " + it.detectedSource?.languageName?:""
                    "Translated with "
                }
            }
        }

        Phrase.instance().bindTextView(translated)

        spanish_text.prepare(getString(R.string.spanish))

        update_source.setOnClickListener {
            Log.e("text", "translated_txt.text.toString()")
            translated.text = source_edit.text.toString()
            spanish_text.updateSource(getString(R.string.spanish))
        }

    }
}
//N&#39;abandonnez jamais un rêve pour le temps qu&#39;il faut pour le réaliser. Le temps passera de toute façon.