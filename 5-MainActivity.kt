package com.nethsara.aiplayer
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
 private val srtGen = SrtGenerator()
 private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { playVideo(it) } }
 private val pickSrt = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { scope.launch(Dispatchers.IO){ val text = contentResolver.openInputStream(it)?.bufferedReader()?.readText() ?: ""; subtitles = SrtParser.parse(text); withContext(Dispatchers.Main){ binding.statusText.text = "SRT Loaded: ${subtitles.size} lines" } } } }
 override fun onCreate(savedInstanceState: Bundle?) {
   super.onCreate(savedInstanceState); binding = ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
   player = ExoPlayer.Builder(this).build().also { binding.playerView.player = it }
   binding.pickVideoBtn.setOnClickListener { pickVideo.launch("video/*") }
   binding.pickSrtBtn.setOnClickListener { pickSrt.launch("*/*") }
   binding.generateBtn.setOnClickListener { generateAiSubtitles() }
   binding.exportBtn.setOnClickListener { exportSrt() }
   binding.downloadModelBtn.setOnClickListener { scope.launch { binding.statusText.text = "Model downloading..."; whisper.downloadTinyModel(this@MainActivity); binding.statusText.text = "Model ready - Offline!" } }
   scope.launch { while(true){ delay(100); val pos = player?.currentPosition ?: 0; val line = subtitles.find { pos in it.startMs..it.endMs }; binding.subtitleText.text = line?.text ?: "" } }
 }
 private fun playVideo(uri: Uri){ currentVideoUri = uri; player?.setMediaItem(MediaItem.fromUri(uri)); player?.prepare(); player?.play(); binding.statusText.text = "Playing..." }
 private fun generateAiSubtitles(){
   val uri = currentVideoUri ?: run{ binding.statusText.text = "Issella video ekak pick karapan machan!"; return }
   binding.statusText.text = "AI wada: Audio -> Whisper -> Translate..."
   scope.launch(Dispatchers.IO){
     try {
       val wav = AudioExtractor.extractAudio(this@MainActivity, uri)
       val segments = whisper.transcribe(this@MainActivity, wav)
       val mapped = segments.map { seg -> SubtitleItem(seg.startMs, seg.endMs, translator.toNaturalSinhala(seg.text, seg.detectedLang)) }
       subtitles = mapped
       withContext(Dispatchers.Main){ binding.statusText.text = "Done! ${mapped.size} subtitles - Natural Sinhala" }
     } catch(e: Exception){ withContext(Dispatchers.Main){ binding.statusText.text = "Error: ${e.message}"; subtitles = DemoSubtitles.get() } }
   }
 }
 private fun exportSrt(){ val srt = srtGen.generate(subtitles); val file = File(getExternalFilesDir(null), "nethsara-subtitles.srt"); file.writeText(srt); binding.statusText.text = "SRT Saved: ${file.absolutePath}" }
 override fun onDestroy(){ super.onDestroy(); player?.release(); scope.cancel() }
}
data class SubtitleItem(val startMs: Long, val endMs: Long, val text: String)
data class WhisperSegment(val startMs: Long, val endMs: Long, val text: String, val detectedLang: String)
