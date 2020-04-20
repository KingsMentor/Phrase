package xyz.belvi.phrase.behaviour

sealed class Behaviour{
    object AUTO_DETECT_BEHAVIOUR : Behaviour()
    object AUTO_TRANSLATE_BEHAVIOUR : Behaviour()
}