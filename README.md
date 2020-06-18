# Introducing Phrase

Phrase provides a way to translate user generated content to a desired target language using different translation engines or options of your choice.

Phrase was built to remove the constraint of having to use one translation engine in your application. It helps android developers leverage the strength of different translation engines while giving the best experience to Users. 

This library was inspired by how twitter handles in-app content translation. 

[ ![Download](https://api.bintray.com/packages/kingsmentor/maven/phrase/images/download.svg) ](https://bintray.com/kingsmentor/maven/phrase/_latestVersion)


![Lib Sample](https://github.com/KingsMentor/phrase/blob/master/phrase.gif)

# Getting Started 

Add this to dependencies in apps `build.gradle`

```
implementation 'xyz.belvi.translate:phrase:1.0.3'

```

## Setting Up Phrase

A single instance of phrase is instantiated for the lifetime of the application. This instance can be created in the application call, activity or any implementation that best suites you. Here's an example showing how to set up the library with some basic options. 

```kotlin
phrase {
    mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
    options {
        targeting = "fr"
        actionLabel = "Translate"
        resultActionLabel = {
            "Translated with "
        }
    }
}

```

Let's talk about the parameters required in setting up phrase: 

### `mediums`

With mediums, you specify a list of `TranslationMedium` to use in order of fallbacks. When translation fails for the first medium, it fallsback to the next medium on the list.  Phrase currently supports: 

* `GoogleTranslate` -  Translation medium using Google Translate Engine
* `FirebaseMLKitTranslate` - Translation Medium Using Google Translate Engine through FirebaseML Kit.
* `DeepL` - Translation medium using DeepL Translation Engine

Phrase also allows implementation of custom translation medium if any of the inbuilt translation mediums doesn't meet the requirements you have in mind. 

#### Building Custom TranslationMedium

Adding a Custom TranslationMedium involves extending `TranslationMedium` and implementing the required members.

```kotlin
final class DeepL(val apiKey: String) : TranslationMedium() {
    override fun translate(text: String, targeting: String): String {
        TODO("handle text translation here") 
    }

    override fun name(): String {
        TODO("preferred name of translation engine")
    }

    override fun detect(text: String): PhraseDetected {
        TODO("handle language detection here")
    }
}
```
see [GoogleTranslate.kt](https://github.com/KingsMentor/Phrase/blob/master/phrase/src/main/java/xyz/belvi/phrase/translateMedium/medium/GoogleTranslate.kt) for a working example.



### `options`

Options provide a way to define translation preferences and library behaivour. 
Here's an example of how an option is built: 

```kotlin
options {
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
}
```

#### Understanding and Building Phrase Options.

1. `targeting` - Set a target language for translation. When not provided, Phrase uses `Locale.getDefault().language` to get device default language.  For Language code,  you can find [this list](https://cloud.google.com/translate/docs/languages) helpful. Phrase also provides:
```kotlin
    enum class Languages(val code: String)
```  
You can use this in selecting a Target Language.  
```kotlin
options {
    targeting = Languages.French.code
}
```

>Phrase uses the target language to:
>- Know which language to translate content to. 
>- Know when to show the `translate` option to the user. Translation action only shows when the detected language of the source is not the same as the targeted language. This also means that there's no translation query executed when source and target language is the same. 

2. `actionLabel` - action label defines the text users see which prompts for translation. This is only visible when the source text is in a language different from the targeted language. This can be hidden by passing `BEHAVIOR_HIDE_TRANSLATE_PROMPT` in `behaviorFlags`.

```kotlin
options {
    actionlabel = "Translate"
}
```
![actionlabel sample](https://github.com/KingsMentor/Phrase/blob/master/imgs/actionlabel.png)

3. `resultActionLabel` - this defines the actiontext users see when a content has been translated. In the sample application, it gives credit to the translation engine used but this can also be customised. Credit can be hidden by passing `BEHAVIOR_HIDE_CREDIT_SIGNATURE` in `behaviorFlags`.
```kotlin
options{
    resultActionLabel = { phraseTranslation ->
        //
        "Translated with "
    }
}
```

![actionlabel sample](https://github.com/KingsMentor/Phrase/blob/master/imgs/resultActionLabelImg.png)

* `preferredDetectionMedium` - Phrase alllows you define a preferred medium to use in language detection. You might want to run language detection with another translation engine different from the engine you want to use for translation. This also accept an instance of `TranslationMedium`. If you are using a custom implementation for this, ensure `detect` returns `PhraseDetected`. See [Building Custom TranslationMedium](#building-custom-translationmedium) for further explanation.
```kotlin
options {
    preferredDetectionMedium = GoogleTranslate(this@MainActivity, R.raw.credential)
}
```

* `sourceTranslation` - this option allows you specify a TranslationMedium for a specific source language. This means you can Specify to use `DeepL` to translate any chinese content while Phrase continue to use whatever medium defined via `mediums` to translate other source content. 

Let us dive deeper into what can be acheive using `SourceTranslation`


```kotlin

// MODEL    
data class SourceTranslationOption(
    val sourceLanguageCode: String,
    val targetLanguageCode: List<String> = emptyList(),
    val translate: List<TranslationMedium> = emptyList()
)

//  USAGE
options {
    sourceTranslation =
        listOf(
            SourceTranslationOption(
                Languages.Chinese.code, 
                listOf("fr", "es"),
                listOf(DeepL(""))
            ),
            SourceTranslationOption(
                Languages.German.code,
                listOf("fr", "es", "*"),
                listOf(DeepL(""))
            )
        )
}
```
`sourceTranslationOption.sourceLanguageCode` is the languagecode you want to specify translation medium for
`sourceTranslationOption.targetLanguageCode` is a list of target language to be considered before using this rule. From the example above, DeepL will only be used as a translation medium if the source language is Chinese and the targetted language is either  French or Spanish. 

What then happens when a target language is english in this case ? 
The default translation medium specified in `mediums` will be used. 

Also, From the above example, DeepL will be used as a translation medium if the source language is German and the targetted language is either French or Spanish. The difference between the first and second rule of  `SourceTranslationOption` is that there's `*` included. This means that if Phrase should use DEEPL to for translation as long as the detected source language is German. 

`sourceTranslationOption.translate` - here, you define translation engine preference for the SourceTranslationOption. Order this list based on fallback preferences. 


* `sourcesToExclude` - this option allows you to ignore language of certain sources for translation. This means that of the detected language source is in this list, users wouldn't be shown the translate action. 
```kotlin
options {
    sourcesToExclude = listOf("fr","es","zh")
}
```
This example implies that Phrase wouldn't handle translation for the language sources detected to be in the list provided. 


* `behaviourOptions` - this helps define how Phrase handles UI behaviour. 
```kotlin
options {
    behaviourFlags {
        switchAnim = R.anim.slide_up
        flags = setOf(BEHAVIOR_HIDE_CREDIT_SIGNATURE,BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY)
        signatureTypeface = typeFace
        signatureColor =
            ContextCompat.getColor(this@MainActivity, R.color.white)
    }
}
```
`switchAnim` -  set anim for text change. If you do not want any text change anomation, do not set this value. 
`signatureTypeface` - Phrase appends Translation engine name to `resultActionLabel`. This is so credit is given to the Translation Engine. `signatureTypeface` defines the typeface for rendering this text. 
`signatureColor` - setting text color for Translation engine credit. 

These behavior can be controlled with flags. In the next phase, I will address flags available in Phrase and how it works. 

### Phrase Behavior Flags
Flags helps define certain behaviour in Phrase to suit your needs. Here's an example of setting flags. 
```kotlin
options {
    behaviourFlags {
        flags = setOf(BEHAVIOR_HIDE_CREDIT_SIGNATURE,BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY)
    }
}
```
`BEHAVIOR_REPLACE_SOURCE_TEXT` - When this flag is set, the source text is replace with translated text after translation

`BEHAVIOR_TRANSLATE_SOURCE_OPTION_ONLY`-  When this flag is set, Phrase only translate sources included in `SourceTranslationOption` only.

`BEHAVIOR_IGNORE_DETECTION`- setting this flag means you always want to run translation without detection first. For this case, Phrase will always show the actionLabel to the user. 

`BEHAVIOR_HIDE_CREDIT_SIGNATURE` - when this flag is set, Phrase doesn't append credit to resultActionLabel. 

`BEHAVIOR_HIDE_TRANSLATE_PROMPT`- Phrase doesn'y show translate action text when this flag is enabled. 


### Using Phrase
#### 1. Phrase Instance
Here's a basic set-up without so much complexity or consideration:
```kotlin
phrase {
    mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
    options {
        targeting = Languages.English.code
    }
}
```
This is a simple set up without many options. This instance can be assigned to a variable like so :

```kotlin
val phrase = phrase {
    mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
    options {
        targeting = Languages.English.code
    }
}

// so something with phrase
phrase.translate("text")

//Phrase instance can also be referenced without assigning to variable like this:
Phrase.instance().translate("text");
```

##### Updating Phrase Options : 
Though phrase runs a single instance, options can be updated after a phrase instance has been created. 
a new option can be created like this :
```kotlin
val options = options {
    targeting = Languages.English.code
}
// this will replace options defined when Phrase was being initialized to the new option provided.
phrase.updateOptions(options)
```
Using Phrase Instance also allows direct translation and detection using preferred options.

```kotlin
    fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation

    fun detectLanguage(text: String, options: PhraseOptions? = null): PhraseDetected?
```

Notice that calling `translate` or `detectLanguage` takes in `PhraseOptions` that is optional. When a PhraseOption is not provided, Phrase uses the default available in the instance. This means, you can translate with a custom option without overriding the default option provided when setting-up Phrase. 

##### PhraseDection
returned by Translation Medium when `detect` is called. It contains: 
`text` - the text passed to the engine for detection

`languageCode` - languageCode of the detected language

`languageName` - languageName of the detected language

`detectionMediumName` - engine name running the detection

##### PhraseTranslation
returned by Phrase Medium when `translate` is called. It contains: 

`translation` - the translated text

`translationMediumName` - engine name that translated this text

`detectedSource` - an instance of PhraseDection containing information about language detection before the text was translated. This can be null when `BEHAVIOR_IGNORE_DETECTION` flag is enabled. 

Finally, TranslationMediums can be updated after a Phrase instance is created. There are few usecases for this that comes to mind as at the time of this writting but this provide a way to always change order of translation medium during runtime.

```kotlin
Phrase.instance().setTranslationMedium(listOf(GoogleTranslate(this@MainActivity, R.raw.credential)));
```

#### 2. PhraseTextView
PhraseTextView is a custom implementation of `androidx.appcompat.widget.AppCompatTextView` with support for Phrase Translation. PhraseTextView handles language translation and detection based on the options defined when setting up Phrase or when `prepare()` is called. 

##### Using PhraseTextView

1. Add to xml

```xml
        <xyz.belvi.phrase.view.PhraseTextView
            android:id="@+id/spanish_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@android:color/white"
            android:textSize="14sp"/>
```

2. Reference in kotlin or Java by calling `prepare()`

```kotlin
        spanish_text.prepare(getString(R.string.spanish),options,object : PhraseTranslateListener{
            override fun onPhraseTranslating() {
                // called when a text is about to be translated. 
            }

            override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                //called when a text has been translated. phraseTranslation contains the translation information
            }

            override fun onActionClick(showingTranslation: Boolean) {
                // called when user clicks on Phrase actionLabel.
            }

            override fun onContentChanged(content: PhraseSpannableBuilder) {
                // // called when there's a content changed due of Phrase translation
            }
        })
 ``` 
 Calling `prepare` updates the content of the TextView with Phrase Configuration. Passing in `options` in `prepare` is optional which is only relevant if you want to run a custom Options for this PhraseTextView. `phraseTextViewListener` is also an optional params.
 

To update the content of PhraseTextView, used `updateSource(text)`. This ensure, the content is updated and prepared for translation.
```kotlin
    spanish_text.updateSource(text)
```

#### 3. Binding a TextView
Using a custom implementation of textView doesn't stop you from using Phrase . Phrase provide `bindTextView` to help bind a textView for Translation and having Phrase capabilities. 

```kotlin
        Phrase.instance().bindTextView(textView,options,object : PhraseTranslateListener("") {
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
        })
        textView.setText(text) // update the text after binding
```
A TextView can be bind with custom Options which would be only applied to translation on this textView. Similar to the behavior in `PhraseTextView`, `PhraseTranslateListener` is a callback to get updates on Phrase Translation in this textView. 

#### 4. Using PhraseSpannableBuilder
Phrase also provides a SpannableStringBuiler implementation called `PhraseSpannableBuilder`. This provides a SpannableString that the user can interacts with. 
```kotlin
        phraseSpannableBuilder =
            object : PhraseSpannableBuilder("",options) {
                override fun onPhraseTranslating() {
                }

                override fun onPhraseTranslated(phraseTranslation: PhraseTranslation?) {
                }

                override fun onActionClick(showingTranslation: Boolean) {
                    Log.i(MainActivity::class.java.name, showingTranslation.toString())
                }

                override fun onContentChanged(content: PhraseSpannableBuilder) {
                // set text of textView 
                    translated.text = content
                }
            }
```
Custom `Options` can also be pass to `PhraseSpannableBuilder`. This option will be used or Phrase instance default options will be used when no `Options` is provided when setting up `PhraseSpannableBuilder`. PhraseSpannableBuilder Options can also be updated by:

`phraseSpannableBuilder.updateOptions(options)`

To change the source of `PhraseSpannableBuilder` call `phraseSpannableBuilder.updateSource(text)`. This update the source text of that should be translated

#### 5. Using PhraseTextWatcher
PhraseTextWatcher is a custom TextWatcher implementation that listens to changes in a textView to update translation source for that textView using Phrase. This is another way of adding Phrase Capability to textView without using any of the approached that has been discussed. 

```kotlin
        textView.addTextChangedListener(
            PhraseTextWatcher(
                options,
                phraseTranslateListener
            )
        )
  ```
  To ensure this works, don't add these 2 lines:
  
```kotlin
    textView.movementMethod = LinkMovementMethod.getInstance()
    textView.highlightColor = Color.TRANSPARENT
 ```
To update source when using `PhraseTextWatcher`, set text of the textView to the new text. Options provided when adding `PhraseTextWatcher` will be used for translating text when `onTextChanged` is called. If nom `Options` was provided, Phrase will use the default Options provided when Phrase was initialised. 

### Extra Information to Keep in mind. 

1. `actionLabel` and `resultActionLabel` color uses `colorAccent` . To change this,  set `android:textColorLink` in the textView that Phrase will be running translation on. 

2. Phrase is best used for runtime translation for user generated content and not for translating static strings or localizing your appliation string resource. 


### Closing Remark. 
Phrase is still actively under development. Pull Request, contributions, thoughts and constructive criticism are welcomed. Please ensure you understand the context before contributing. [Submit issues here](https://github.com/KingsMentor/Phrase/issues).
This library is not a product of or in anyway affiliated to [Phrase.com](https://phrase.com)

To support this work and other of my [Open Source Project](https://github.com/KingsMentor), you can [buy me a coffee or support](https://www.buymeacoffee.com/kingsmentor) 
