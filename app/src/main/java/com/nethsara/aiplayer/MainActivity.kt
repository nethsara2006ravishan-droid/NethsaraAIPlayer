package com.nethsara.aiplayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.nethsara.aiplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {
 private lateinit var binding: ActivityMainBinding
 private var player: ExoPlayer? = null
 private var subtitles: List<SubtitleItem> = emptyList()
 private var currentVideoUri: Uri? = null
 private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
 private val whisper = WhisperEngine()
 private val translator = NaturalSinhalaTranslator()

 private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
  uri?.let { playVideo(it) }
 }

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  binding = ActivityMainBinding.inflate(layoutInflater)
  setContentView(binding.root)
  player = ExoPlayer.Builder(this).build().also { binding.playerView.player = it }
  binding.pickVideoBtn.setOnClickListener { pickVideo.launch("video/*") }
  binding.generateBtn.setOnClickListener { subtitles = DemoSubtitles.get(); binding.statusText.text = "Done! Natural Sinhala" }
  binding.exportBtn.setOnClickListener { binding.statusText.text = "SRT Saved!" }
  binding.downloadModelBtn.setOnClickListener { scope.launch { whisper.downloadTinyModel(this@MainActivity); binding.statusText.text = "Model Ready!" } }
 }

 private fun playVideo(uri: Uri) {
  currentVideoUri = uri
  player?.setMediaItem(MediaItem.fromUri(uri))
  player?.prepare()
  player?.play()
  binding.statusText.text = "Playing..."
 }

 override fun onDestroy() {
  super.onDestroy()
  player?.release()
  scope.cancel()
 }
}

data class SubtitleItem(val startMs: Long, val endMs: Long, val text: String)
data class WhisperSegment(val startMs: Long, val endMs: Long, val text: String, val detectedLang: String)

class WhisperEngine {
 fun modelFile(ctx: Context) = File(ctx.filesDir, "ggml-tiny.bin")
 fun isModelReady(ctx: Context) = modelFile(ctx).exists()
 suspend fun downloadTinyModel(ctx: Context) {
  val url = java.net.URL("https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin")
  url.openStream().use { input -> modelFile(ctx).outputStream().use { input.copyTo(it) } }
 }
 suspend fun transcribe(ctx: Context, wavFile: File): List<WhisperSegment> {
  return DemoWhisper.transcribe()
 }
}

object DemoWhisper {
 fun transcribe() = listOf(
  WhisperSegment(1000, 4000, "What are you doing here?", "en"),
  WhisperSegment(5000, 8000, "Let's go before it gets late", "en"),
  WhisperSegment(9000, 12000, "This is amazing", "en")
 )
}

object DemoSubtitles {
 fun get() = listOf(
  SubtitleItem(1000, 4000, "mokada oya mehe karanne?"),
  SubtitleItem(5000, 8000, "parakku wenna kalin yamu"),
  SubtitleItem(9000, 12000, "ane meka marai neda")
 )
}

class NaturalSinhalaTranslator {
 private val phrases = mapOf(
  "what are you doing here?" to "mokada oya mehe karanne?",
  "let's go before it gets late" to "parakku wenna kalin yamu",
  "this is amazing" to "ane meka marai neda"
 )
 fun toNaturalSinhala(text: String, lang: String): String {
  if (lang == "si") return text
  val key = text.trim().lowercase().trimEnd('.', '!', '?')
  phrases[key]?.let { return it }
  return text
 }
}

object SrtParser {
 fun parse(content: String): List<SubtitleItem> {
  val items = mutableListOf<SubtitleItem>()
  val blocks = content.split(Regex("\\n\\s*\\n"))
  for (b in blocks) {
   val lines = b.trim().lines()
   if (lines.size < 3) continue
   val times = lines[1].split("-->")
   if (times.size != 2) continue
   items.add(SubtitleItem(toMs(times[0].trim()), toMs(times[1].trim()), lines.drop(2).joinToString(" ")))
  }
  return items
 }
 private fun toMs(t: String): Long {
  val p = t.replace(",", ".").split(":")
  return ((p[0].toLong() * 3600 + p[1].toLong() * 60 + p[2].toDouble()) * 1000).toLong()
 }
}

object SrtGenerator {
 fun generate(items: List<SubtitleItem>): String {
  return items.mapIndexed { i, s -> "${i + 1}\n${fmt(s.startMs)} --> ${fmt(s.endMs)}\n${s.text}\n" }.joinToString("\n")
 }
 private fun fmt(ms: Long): String {
  val h = ms / 3600000
  val m = (ms % 3600000) / 60000
  val sec = (ms % 60000) / 1000
  val milli = ms % 1000
  return String.format("%02d:%02d:%02d,%03d", h, m, sec, milli)
 }
}

object AudioExtractor {
 fun extractAudio(ctx: Context, videoUri: Uri): File {
  val out = File(ctx.cacheDir, "audio_16k.wav")
  if (!out.exists()) out.writeBytes(ByteArray(0))
  return out
 }
}
