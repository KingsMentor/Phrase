# Introducing Phrase

Phrase provides a way to translate user generated content to desired target language using different translation engine or options of your choice.

I built phrase to remove the constraint of having to use one translation engine in your application. It helps android developers leverage on the strength of different translation engine while giving the best experience to Users. 

[ ![Download](https://api.bintray.com/packages/kingsmentor/maven/phrase/images/download.svg) ](https://bintray.com/kingsmentor/maven/phrase/_latestVersion)


![Lib Sample](https://github.com/KingsMentor/phrase/blob/master/phrase.gif)

## Getting Started 

Add this to dependency in apps `build.gradle`

```
implementation 'xyz.belvi.translate:phrase:1.0.0'

```

### Setting Up Phrase

A single instance of phrase is instantiated for the lifetime of the application. This instance can be created in the application call , activity or any implementation that best suite you. Here's an example of setting up the library with some basic options. 

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

Let's talk about the parameters required in setting up phrase : 

#### `mediums`

With mediums, you specify a list of `TranslationMedium` to use in order of fallbacks. Phrase currently supports: 

* `GoogleTranslate` -  Translation medium using Google Translate Engine
* `FirebaseMLKitTranslate` - Translation Medium Using Googke Translate Engine through FirebaseML Kit.
* `DeepL` - Translation medium using DeepL Translation Engine

Phrase also allows implementation of custom translation medium of any of the inbuilt translation medium doesn't meet the requirements you have in mind. 

##### Building Custom TranslationMedium

Adding a Custom TranslationMedium involves extending `TranslationMedium` and implementing the required members. Here's an example.

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


#### `options`

Options provide a way to define translation preference and library behaivour. 
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

##### Understanding an Building Phrase Options.
