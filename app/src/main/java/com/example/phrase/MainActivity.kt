package com.example.phrase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val phrase = Phrase.with(GoogleTranslate(this, R.raw.credential, "fr"))
            .options(PhraseOptions.options().build())
            .setUp()

        button.setOnClickListener {
            val text = phrase.translate(edit.text.toString())
            textView.text = text
        }

    }
}