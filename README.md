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
