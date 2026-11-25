package com.nethsara.aiplayer
import android.content.Context
import java.io.File
// Real Whisper.cpp JNI connect point - tiny model
// If .so not present, falls back to demo to keep app working
class WhisperEngine {
  fun modelFile(ctx: Context) = File(ctx.filesDir, "ggml-tiny.bin")
  fun isModelReady(ctx: Context) = modelFile(ctx).exists()
  suspend fun downloadTinyModel(ctx: Context) {
    // Download from https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin (22MB)
    // Implemented with HttpURLConnection - see full impl in README, kept simple here for A04s
    val url = java.net.URL("https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin")
    url.openStream().use { input -> modelFile(ctx).outputStream().use { input.copyTo(it) } }
  }
  suspend fun transcribe(ctx: Context, wavFile: File): List<WhisperSegment> {
    if (!isModelReady(ctx)) return DemoWhisper.transcribe()
    // TODO: Call native whisper.cpp via JNI: System.loadLibrary("whisper")
    // Native method: nativeTranscribe(modelPath, wavPath) returns segments with timestamps
    // For now, if JNI not built, return demo so app doesn't crash
    return try { NativeWhisper.transcribe(modelFile(ctx).absolutePath, wavFile.absolutePath) } catch(e: Exception) { DemoWhisper.transcribe() }
  }
}
object NativeWhisper { external fun transcribe(modelPath: String, wavPath: String): List<WhisperSegment> }
object DemoWhisper { fun transcribe() = listOf(WhisperSegment(1000,4000,"What are you doing here?","en"), WhisperSegment(5000,8000,"Let's go before it gets late","en"), WhisperSegment(9000,12000,"This is amazing","en")) }
object DemoSubtitles { fun get() = listOf(SubtitleItem(1000,4000,"මොකද ඔයා මෙහේ කරන්නේ?"), SubtitleItem(5000,8000,"පරක්කු වෙන්න කලින් යමු"), SubtitleItem(9000,12000,"අනේ මේක මාරයි නේද")) }
