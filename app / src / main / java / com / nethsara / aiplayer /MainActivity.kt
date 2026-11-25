package com.nethsara.aiplayer

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.nethsara.aiplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
 private lateinit var binding: ActivityMainBinding
 private var player: ExoPlayer? = null
 private var subtitles: List<SubtitleItem> = emptyList()
 private var currentVideoUri: Uri? = null
 private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

 private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
  uri?.let { playVideo(it) }
 }

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  binding = ActivityMainBinding.inflate(layoutInflater)
  setContentView(binding.root)
  player = ExoPlayer.Builder(this).build().also { binding.playerView.player = it }
  binding.pickVideoBtn.setOnClickListener { pickVideo.launch("video/*") }
  binding.generateBtn.setOnClickListener { subtitles = DemoSubtitles.get() }
  binding.exportBtn.setOnClickListener { binding.statusText.text = "SRT Saved!" }
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
