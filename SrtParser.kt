package com.nethsara.aiplayer
object SrtParser {
 fun parse(content: String): List<SubtitleItem> {
   val items = mutableListOf<SubtitleItem>()
   val blocks = content.split(Regex("\n\s*\n"))
   for (b in blocks) { val lines = b.trim().lines(); if (lines.size < 3) continue; val times = lines[1].split("-->"); if (times.size!=2) continue; items.add(SubtitleItem(toMs(times[0].trim()), toMs(times[1].trim()), lines.drop(2).joinToString(" "))) }
   return items
 }
 private fun toMs(t: String): Long { val p = t.replace(",",".").split(":"); return ((p[0].toLong()*3600 + p[1].toLong()*60 + p[2].toDouble())*1000).toLong() }
}
object SrtGenerator {
 fun generate(items: List<SubtitleItem>): String = items.mapIndexed { i, s -> "${i+1}\n${fmt(s.startMs)} --> ${fmt(s.endMs)}\n${s.text}\n" }.joinToString("\n")
 private fun fmt(ms: Long): String { val h=ms/3600000; val m=(ms%3600000)/60000; val sec=(ms%60000)/1000; val milli=ms%1000; return String.format("%02d:%02d:%02d,%03d", h,m,sec,milli) }
}
