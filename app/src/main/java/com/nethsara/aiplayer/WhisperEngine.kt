package com.nethsara.aiplayer

import android.content.Context
import java.io.File

class WhisperEngine {
 fun modelFile(ctx: Context) = File(ctx.filesDir, "ggml-tiny.bin")
 fun isModelReady(ctx: Context) = modelFile(ctx).exists()

 suspend fun downloadTinyModel(ctx: Context) {
  val url = java.net.URL("https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin")
  url.openStream().use { input -> modelFile(ctx).outputStream().use { input.copyTo(it) } }
 }

 suspend fun transcribe(ctx: Context, wavFile: File): List<WhisperSegment> {
  if (!isModelReady(ctx)) return DemoWhisper.transcribe()
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
