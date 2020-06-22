# Introducing Phrase

Phrase provides a way to translate user-generated content to a desired target language using different translation engines or options of your choice.

Phrase was built to remove the constraint of having to use one translation engine in your application. It helps Android developers leverage the strength of different translation engines while giving the best experience to Users. 

This library was inspired by how twitter handles in-app content translation. 

[ ![Download](https://api.bintray.com/packages/kingsmentor/maven/phrase/images/download.svg) ](https://bintray.com/kingsmentor/maven/phrase/_latestVersion)


![Phrase Sample](https://github.com/KingsMentor/phrase/blob/master/documentation/imgs/phrase.gif)

# Getting Started 

Add this to dependencies in apps `build.gradle`

```
implementation 'xyz.belvi.translate:phrase:1.0.7'

```

## Setting Up Phrase

A single instance of phrase is instantiated for the lifetime of the application. This instance can be created in the application call, activity, or any implementation that best suits you. Here's an example showing how to set up the library with some basic options.  

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

With mediums, you specify a list of `TranslationMedium` to use in order of fallbacks. When translation fails for the first medium, it falls back to the next medium on the list.  Phrase currently supports: 

* `GoogleTranslate` -  Translation medium using Google Translate Engine
* `FirebaseMLKitTranslate` - Translation Medium Using Google Translate Engine through FirebaseML Kit.
* `DeepL` - Translation medium using DeepL Translation Engine

Phrase also allows the implementation of custom translation mediums if any of the inbuilt translation mediums doesn't meet the requirements you have in mind. 

`FirebaseMLKitTranslate` requires [FirebaseSetUp](https://firebase.google.com/docs/android/setup)

For both `FirebaseMLKitTranslate` and `GoogleTranslate`, remember to [enable Google Translate API](https://console.cloud.google.com/apis/): 

[This resource](https://medium.com/@yeksancansu/how-to-use-google-translate-api-in-android-studio-projects-7f09cae320c7) proves helpful in getting your credentials needed if you want to use `GoogleTranslate` TranslationMedium

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

Options provide a way to define translation preferences and library behavior. 
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

1)`targeting` - Set a target language for translation. When this is not provided, Phrase uses `Locale.getDefault().language` to get the default language of the device. For Language code, you can find [this list](https://cloud.google.com/translate/docs/languages) helpful. Phrase also provides:
```kotlin
enum class Languages(val code: String)
```  
You can use this to select a Target Language.  
```kotlin
options {
    targeting = Languages.French.code
}
```

Phrase uses the target language to:

* Know which language to translate content to. 
* Know when to show the `translate` option to the user. Translation action only shows when the detected source language is not the same as the target language. This also means that no translation query is executed when the source and the target language are the same. 

2)`actionLabel` - action label sets the text users see which prompts for translation. This is only visible when the source text is in a language different from the target language. This can be hidden by passing `BEHAVIOR_HIDE_TRANSLATE_PROMPT` in `behaviorFlags`.

```kotlin
options {
    actionlabel = "Translate"
}
```
![actionLabel sample](https://github.com/KingsMentor/Phrase/blob/master/documentation/imgs/actionlabel.png)

3)`resultActionLabel` - this defines the actiontext users see when content has been translated. In the sample application, it gives credit to the translation engine used but this can also be customised. Credit can be hidden by passing `BEHAVIOR_HIDE_CREDIT_SIGNATURE` in `behaviorFlags`.
```kotlin
options{
    resultActionLabel = { phraseTranslation ->
        //
        "Translated with "
    }
}
```

![resultActionLabel sample](https://github.com/KingsMentor/Phrase/blob/master/documentation/imgs/resultActionLabelImg.png)

4)`preferredDetectionMedium` - Phrase allows you to define a preferred medium to use in language detection. You might want to run language detection with a translation engine different from the engine you want to use for translation. This also accepts an instance of `TranslationMedium`. If you are using a custom implementation for this, ensure `detect` returns `PhraseDetected`. See [Building Custom TranslationMedium](#building-custom-translationmedium) for further explanation.
```kotlin
options {
    preferredDetectionMedium = GoogleTranslate(this@MainActivity, R.raw.credential)
}
```

5)`sourceTranslation` - this option allows you to specify a TranslationMedium for a specific source language. This means you can specify to use the `DeepL` engine to translate any Chinese content while Phrase continues to use whatever medium that was defined in `mediums` to translate other source content. 

Let us dive deeper into what can be acheived using `SourceTranslation`

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
- `sourceTranslationOption.sourceLanguageCode` is the language code for which you want to specify a translation medium.
- `sourceTranslationOption.targetLanguageCode` is a list of target languages to be considered before using this rule. From the example above, DeepL will be used as a translation medium only if the source language is Chinese and the target language is either French or Spanish. 

    What then happens when a target language is English in this case?

    The default translation medium specified in `mediums` will be used. 

    Also, from the above example, DeepL will be used as a translation medium if the source language is German and the target language is either French or Spanish. The difference between the first and second example is that there's `*` included. This means that Phrase should use DeepL for translation as long as the detected source language is German. 

- `sourceTranslationOption.translate` - here, you define the translation engine preference for the SourceTranslationOption. Order this list based on fallback preferences. 


6)`sourcesToExclude` - this option sets the list of source languages to ignore. This means that if the detected language source is in this list, users wouldn't be shown the translate action.
```kotlin
options {
    sourcesToExclude = listOf("fr","es","zh")
}
```
This example implies that Phrase wouldn't handle translation for the language sources in the list provided. 

7)`preferredSources` - this option sets the list of source languages to translate. This means that if the detected language source is not in this list, users wouldn't be shown the translate action.
```kotlin
options {
    preferredSources = listOf("fr","es","zh")
}
```
This example implies that Phrase wouldn't handle translation for the language sources in the list provided. 

8)`behaviourOptions` - this defines how Phrase handles UI behaviour. 
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
- `switchAnim` -  sets animation for text change. If you do not want any text change animation, do not set this value. 
- `signatureTypeface` - Phrase appends Translation engine name to `resultActionLabel`. This is so that credit is given to the Translation Engine. `signatureTypeface` defines the typeface for rendering this text. 
- `signatureColor` - sets text color for Translation engine credit. 

These behaviors can be controlled with flags. 

#### Phrase Behavior Flags
Flags helps define certain behaviour in Phrase in order to suit your needs. 
```kotlin
options {
    behaviourFlags {
        flags = setOf(BEHAVIOR_HIDE_CREDIT_SIGNATURE,BEHAVIOR_TRANSLATE_PREFERRED_SOURCE_ONLY)
    }
}
```
- `BEHAVIOR_REPLACE_SOURCE_TEXT` - When this flag is set, the source text is replaced with translated text after translation

- `BEHAVIOR_TRANSLATE_SOURCE_OPTION_ONLY`-  When this flag is set, Phrase only translate sources included in `preferredSources`.

- `BEHAVIOR_IGNORE_DETECTION`- setting this flag means you always want to run translation without detection first. For this case, Phrase will always show the actionLabel to the user. 

- `BEHAVIOR_HIDE_CREDIT_SIGNATURE` - when this flag is set, Phrase doesn't append credit to resultActionLabel. 

- `BEHAVIOR_HIDE_TRANSLATE_PROMPT`- Phrase doesn't show translate action text when this flag is enabled. 


# Using Phrase
### 1. Phrase Instance
Here's a basic set-up without so much complexity or consideration:
```kotlin
phrase {
    mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
    options {
        targeting = Languages.English.code
    }
}
```
This is a simple set up without many options. This instance can be assigned to a variable like so:

```kotlin
val phrase = phrase {
    mediums = listOf(GoogleTranslate(this@MainActivity, R.raw.credential))
    options {
        targeting = Languages.English.code
    }
}

// do something with phrase
phrase.translate("text")

//Phrase instance can also be referenced without assigning to variable like this:
Phrase.instance().translate("text");
```

#### Updating Phrase Options
Though phrase runs a single instance, options can be updated after a phrase instance has been created. A new option can be created like this :
```kotlin
val options = options {
    targeting = Languages.English.code
}
// this will replace the options defined when Phrase was initialized to the new option provided.
phrase.updateOptions(options)
```
Using Phrase Instance also allows direct translation and detection using preferred options.

```kotlin
fun translate(text: String, options: PhraseOptions? = null): PhraseTranslation

fun detectLanguage(text: String, options: PhraseOptions? = null): PhraseDetected?
```

Notice that calling `translate` or `detectLanguage` takes in `PhraseOptions` which is optional. When a PhraseOption is not provided, Phrase uses the default available in the instance. This means you can translate with a custom option without overriding the default option provided when setting-up Phrase. 

#### PhraseDection
This is returned by the Translation Medium when `detect` is called. It contains: 
- `text` - the text passed to the engine for detection

- `languageCode` - languageCode of the detected language

- `languageName` - languageName of the detected language

- `detectionMediumName` - engine name running the detection

#### PhraseTranslation
This is returned by the Phrase Medium when `translate` is called. It contains: 

- `translation` - the translated text

- `translationMediumName` - engine name that translated this text

- `detectedSource` - an instance of PhraseDection containing information about language detection before the text was translated. This would be null when the  `BEHAVIOR_IGNORE_DETECTION` flag is enabled. 

Finally, TranslationMediums can be updated after a Phrase instance is created. Though there are few use cases for this as at now, this provides a way to change the order of the translation medium during runtime.

```kotlin
Phrase.instance().setTranslationMedium(listOf(GoogleTranslate(this@MainActivity, R.raw.credential)));
```

### 2. PhraseTextView
PhraseTextView is a custom implementation of `androidx.appcompat.widget.AppCompatTextView` with support for Phrase Translation. PhraseTextView handles language translation and detection based on the options defined when setting up Phrase or when `prepare()` is called. 

#### Using PhraseTextView

Step 1. Add to xml

```xml
<xyz.belvi.phrase.view.PhraseTextView
    android:id="@+id/spanish_text"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:textColor="@android:color/white"
    android:textSize="14sp"/>
```

Step 2. Reference in kotlin or Java by calling `prepare()`

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
        // called when there's a content changed due to Phrase translation
    }
})
``` 
Calling `prepare` updates the content of the TextView with Phrase Configuration. Passing in `options` in `prepare` is optional and is only relevant if you want to use custom Options for this PhraseTextView. `phraseTranslateListener` is also an optional callback that provides updates.
 

To update the content of PhraseTextView, use `updateSource(text)`. This ensures, the content is updated and prepared for translation.
```kotlin
spanish_text.updateSource(text)
```

### 3. Binding a TextView to Phrase
Using a custom implementation of textView doesn't stop you from using Phrase. Phrase provides `bindTextView` which binds a textView for Translation and adds Phrase capabilities. 

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
A TextView can be bound with custom Options which would only be applied to translation on this textView. Similar to the behavior in `PhraseTextView`, `PhraseTranslateListener` is a callback to get updates on Phrase Translation in this textView. 

### 4. Using PhraseSpannableBuilder
Phrase provides a SpannableStringBuilder implementation called `PhraseSpannableBuilder`. This provides a SpannableString that the user can interact with. 
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
Custom `Options` can also be passed to `PhraseSpannableBuilder`. If custom Options are not provided, Phrase instance default options will be used when setting up `PhraseSpannableBuilder`. PhraseSpannableBuilder Options can also be updated by:

```
phraseSpannableBuilder.updateOptions(options)
```

To change the source of `PhraseSpannableBuilder` call `phraseSpannableBuilder.updateSource(text)`. This updates the source text that should be translated

### 5. Using PhraseTextWatcher
PhraseTextWatcher is a custom TextWatcher implementation that listens to changes in a textView to update the translation source for that textView. This is another way of adding Phrase capabilities to textView. 

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
When the content of a textView changes, `PhraseTextWatcher` updates the translation source. Options provided when adding `PhraseTextWatcher` will be used for translating text when `onTextChanged` is called. If no `Options` were provided, Phrase will use the default Options provided when Phrase was initialised. 

### Extra Information to Keep in mind. 

1. `actionLabel` and `resultActionLabel` color uses `colorAccent`. To change this,  set `android:textColorLink` in the textView that Phrase will be running translation on. 

2. Phrase is best used for runtime translation on user-generated content and not for translating static strings or localizing your application string resource. 


## Closing Remarks 
Phrase is still actively under development. Pull Requests, contributions, thoughts, and constructive criticism are welcome. Please ensure you understand the context before contributing. [Submit issues here](https://github.com/KingsMentor/Phrase/issues).
This library is not a product of or in any way affiliated to [Phrase.com](https://phrase.com)

To support this work and any of my other [Open Source Projects](https://github.com/KingsMentor), [buy me a coffee](https://www.buymeacoffee.com/kingsmentor) 
