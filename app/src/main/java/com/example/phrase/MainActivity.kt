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

        val font = Typeface.createFromAsset(assets, "test.ttf")


        phrase {
            mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
            options {
                targetting = "fr"
                behaviourFlags {
                    flags = setOf()
                    signatureTypeface = font
                    signatureColor =
                        ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark)
                }
                actionLabel = "Translate"
                resultActionLabel = {
                    detected.text = "Detected Language Source: " + it.detectedSource?.languageName?:""
                    "Translated with "
                }
            }
        }

        Phrase.instance().bindTextView(translated)

        update_source.setOnClickListener {
            Log.e("text", "translated_txt.text.toString()")
            translated.text = source_edit.text.toString()
        }

    }
}
