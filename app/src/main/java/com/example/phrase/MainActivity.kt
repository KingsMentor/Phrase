package com.example.phrase

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.helpers.PhraseSpannableStringBuilder
import xyz.belvi.phrase.options.PhraseOptions
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Phrase.with(GoogleTranslate(this, R.raw.credential))
            .options(
                PhraseOptions.options().targeting("fr").build(
                    translateText = "Translate Text",
                    translateFrom = { medium ->
                        "Translated from ${medium.source.name} with ${medium.translationMedium.name()} "
                    }
                ))
            .setUp()


        button.setOnClickListener {
            val text = object : PhraseSpannableStringBuilder(edit.text.toString()) {
                override fun notifyUpdate(text: PhraseSpannableStringBuilder) {
                    super.notifyUpdate(text)
                    textView.text = text
                }
            }
            textView.text = text
        }

        textView.movementMethod = LinkMovementMethod.getInstance();
    }

}