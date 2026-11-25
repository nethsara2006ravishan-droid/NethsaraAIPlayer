package com.nethsara.aiplayer

class NaturalSinhalaTranslator {
 private val phrases = mapOf(
  "what are you doing here?" to "mokada oya mehe karanne?",
  "let's go before it gets late" to "parakku wenna kalin yamu",
  "this is amazing" to "ane meka marai neda",
  "how are you?" to "kohomada ithin?",
  "i don't know" to "ane manda ban",
  "i love you" to "mama oyata adarei",
  "what is your name?" to "oyage nama mokadda?",
  "where are you going?" to "koheda yanne?",
  "come here" to "mehe varen",
  "don't go" to "yanna epa ban"
 )

 fun toNaturalSinhala(text: String, lang: String): String {
  if (lang == "si") return text
  val key = text.trim().lowercase().trimEnd('.', '!', '?')
  phrases[key]?.let { return it }
  return text
 }
}
