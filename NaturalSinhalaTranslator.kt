package com.nethsara.aiplayer
// Offline Natural Sinhala - Rule-based + Dictionary, NO API KEY
// Stage 1: Common phrases dictionary (most accurate), Stage 2: word-by-word fallback
class NaturalSinhalaTranslator {
 private val phrases = mapOf(
  "what are you doing here?" to "මොකද ඔයා මෙහේ කරන්නේ?",
  "let's go before it gets late" to "පරක්කු වෙන්න කලින් යමු",
  "this is amazing" to "අනේ මේක මාරයි නේද",
  "how are you?" to "කොහොමද ඉතින්?",
  "i don't know" to "අනේ මන්දා බන්",
  "i love you" to "මම ඔයාට ආදරෙයි",
  "what is your name?" to "ඔයාගේ නම මොකද්ද?",
  "where are you going?" to "කොහෙද යන්නේ?",
  "come here" to "මෙහෙ වරෙන්",
  "don't go" to "යන්න එපා බන්",
  "i am hungry" to "මට බඩගිනියි බන්",
  "good morning" to "සුභ උදෑසනක්",
  "thank you" to "බොහොම ස්තූතියි / තැන්ක්ස් මචන්",
  "i am sorry" to "සමාවෙන්න බන්",
  "what happened?" to "මොකද වුණේ?"
 )
 private val words = mapOf("you" to "ඔයා","i" to "මම","we" to "අපි","go" to "යමු","come" to "වරෙන්","good" to "හොඳ","bad" to "නරක","love" to "ආදරෙයි","friend" to "යාලුවා","brother" to "අයියා / මල්ලි","today" to "අද","tomorrow" to "හෙට")
 fun toNaturalSinhala(text: String, lang: String): String {
   if (lang == "si") return text // already Sinhala
   val key = text.trim().lowercase().trimEnd('.','!','?')
   phrases[key]?.let { return it }
   // fallback: word replace, keep natural tone
   var out = text.lowercase()
   words.forEach { (k,v) -> out = out.replace(Regex("\\b$k\\b"), v) }
   return out.ifBlank { text }
 }
}
