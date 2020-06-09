package com.example.phrase

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.phrase
import xyz.belvi.phrase.translateMedium.TranslationMedium
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val font = Typeface.createFromAsset(assets, "rb.ttf")

        phrase {
            with {
                GoogleTranslate(this,0)
            }
            options {
                targetting = "fr"
                behaviourFlags {
                    flags = setOf()
                    signatureTypeface = font
                }
                actionLabel = "Translate With"
                resultActionLabel = { "Trams" }
            }
        }

        Phrase.instance().bindTextView(translated_txt)

        update_source.setOnClickListener {
            Log.e("text", "translated_txt.text.toString()")
            translated_txt.text = source_edit.text.toString()
        }

        textView.setOnClickListener {
            Log.e("text", "translated_txt.text.toString()")
        }
    }
}
