# NETHSARA AI PLAYER V1 - GitHub Ready
Offline AI Sinhala Subtitle Player - No API Key

## Features
- MP4 / MKV Play (Media3 ExoPlayer)
- Video Picker (SAF)
- Offline Whisper.cpp Voice Recognition (tiny model)
- Language Detect (auto)
- Natural Sinhala Translation (offline dictionary + rule-based)
- Auto Timestamp + SRT Generate + Export
- Original Audio preserved

## Setup
1. GitHub repo ekata me files okkoma upload karanna
2. GitHub Actions -> APK build wenawa (see .github/workflows/build.yml)
3. App eka open karala, Settings -> Download Whisper Tiny Model (22MB) obanna - eka parak witharai internet one
4. Passe 100% offline

## Phone: Samsung A04s optimized
- Whisper tiny model only (fast + low RAM)
- Chunk: 30 sec audio slices
- Translation: offline, no internet

## Flow
Video pick -> FFmpeg extract audio 16kHz wav -> Whisper.cpp transcribe with timestamps -> Language detect -> Natural Sinhala translate -> SRT show + export

Made in Sri Lanka 🇱🇰 - Nethsara
