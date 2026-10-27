package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class TtsEngine(context: Context) : TextToSpeech.OnInitListener {
    private val TAG = "TtsEngine"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _voiceAmplitude = MutableStateFlow(0f)
    val voiceAmplitude: StateFlow<Float> = _voiceAmplitude.asStateFlow()

    private var amplitudeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private var onUtteranceComplete: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.UK)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "Locale.UK not supported, falling back to Locale.ENGLISH")
                tts?.setLanguage(Locale.ENGLISH)
            }
            isInitialized = true
            setupVoicePreferences(isMale = true)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    startSimulatedAmplitude()
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopAmplitude()
                    onUtteranceComplete?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopAmplitude()
                }
            })
            Log.d(TAG, "TTS initialized successfully with British English.")
        } else {
            Log.e(TAG, "TTS initialization failed with status $status")
        }
    }

    fun setupVoicePreferences(isMale: Boolean, speechRate: Float = 0.95f, pitch: Float = 1.0f) {
        if (!isInitialized || tts == null) return
        tts?.setPitch(pitch)
        tts?.setSpeechRate(speechRate)

        try {
            val voices: Set<Voice>? = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val britishVoices = voices.filter {
                    it.locale == Locale.UK || it.locale.toLanguageTag().contains("en-GB", ignoreCase = true)
                }
                val matchedVoice = if (isMale) {
                    britishVoices.firstOrNull { it.name.contains("male", ignoreCase = true) && !it.name.contains("female", ignoreCase = true) }
                        ?: britishVoices.firstOrNull()
                } else {
                    britishVoices.firstOrNull { it.name.contains("female", ignoreCase = true) }
                        ?: britishVoices.firstOrNull()
                }

                matchedVoice?.let {
                    tts?.voice = it
                    Log.d(TAG, "Selected British voice: ${it.name}")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error selecting specific voice: ${e.message}")
        }
    }

    fun speak(text: String, speechRate: Float = 0.95f, pitch: Float = 1.0f, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            Log.w(TAG, "TTS not yet initialized")
            onDone?.invoke()
            return
        }

        stop()
        tts?.setSpeechRate(speechRate)
        tts?.setPitch(pitch)
        this.onUtteranceComplete = onDone

        val utteranceId = UUID.randomUUID().toString()
        // Strip markdown asterisks or quotes before speaking for clean audio
        val cleanText = text.replace("*", "").replace("#", "").trim()
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _isSpeaking.value = false
        stopAmplitude()
    }

    private fun startSimulatedAmplitude() {
        amplitudeJob?.cancel()
        amplitudeJob = scope.launch {
            var phase = 0f
            while (isActive && _isSpeaking.value) {
                // Natural fluctuating audio wave amplitude between 0.3 and 1.0
                phase += 0.4f
                val wave = (Math.sin(phase.toDouble()).toFloat() * 0.35f) + 0.65f
                val jitter = (Math.random().toFloat() * 0.2f) - 0.1f
                _voiceAmplitude.value = (wave + jitter).coerceIn(0.2f, 1.0f)
                delay(60)
            }
            _voiceAmplitude.value = 0f
        }
    }

    private fun stopAmplitude() {
        amplitudeJob?.cancel()
        _voiceAmplitude.value = 0f
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
