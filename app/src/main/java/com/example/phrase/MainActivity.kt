package com.example.phrase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.belvi.phrase.Phrase
import xyz.belvi.phrase.translateMedium.medium.GoogleTranslate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Phrase.with(GoogleTranslate(this, 0, "fr"))
            .setUp()
    }
}