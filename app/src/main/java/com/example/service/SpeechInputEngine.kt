package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechInputEngine(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private val TAG = "SpeechInputEngine"
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _inputAmplitude = MutableStateFlow(0f)
    val inputAmplitude: StateFlow<Float> = _inputAmplitude.asStateFlow()

    private var isAvailable = false

    init {
        try {
            isAvailable = SpeechRecognizer.isRecognitionAvailable(context)
            if (isAvailable) {
                initRecognizer()
            } else {
                Log.w(TAG, "Speech recognition is not available on this device")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SpeechRecognizer", e)
        }
    }

    private fun initRecognizer() {
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _isListening.value = true
                    _partialText.value = ""
                }

                override fun onBeginningOfSpeech() {
                    _isListening.value = true
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Normalise rmsdB (-2 to 10 typical) to 0.0 .. 1.0 range
                    val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                    _inputAmplitude.value = normalized
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _isListening.value = false
                    _inputAmplitude.value = 0f
                }

                override fun onError(error: Int) {
                    _isListening.value = false
                    _inputAmplitude.value = 0f
                    val errorMsg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected."
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                        SpeechRecognizer.ERROR_NETWORK -> "Network issue for voice recognition."
                        else -> "Voice recognition issue (Code $error)"
                    }
                    Log.w(TAG, "SpeechRecognizer error: $errorMsg")
                    onError(errorMsg)
                }

                override fun onResults(results: Bundle?) {
                    _isListening.value = false
                    _inputAmplitude.value = 0f
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spokenText = matches?.firstOrNull().orEmpty()
                    if (spokenText.isNotBlank()) {
                        _partialText.value = spokenText
                        onResult(spokenText)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull().orEmpty()
                    if (text.isNotBlank()) {
                        _partialText.value = text
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    fun startListening() {
        if (!isAvailable) {
            onError("Speech recognition service is not available.")
            return
        }

        try {
            if (speechRecognizer == null) {
                initRecognizer()
            }
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-GB")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-GB")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-GB")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            _isListening.value = true
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            _isListening.value = false
            onError("Failed to activate microphone: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping speech recognition", e)
        }
        _isListening.value = false
        _inputAmplitude.value = 0f
    }

    fun cancel() {
        try {
            speechRecognizer?.cancel()
        } catch (e: Exception) {
            Log.w(TAG, "Error cancelling speech recognizer", e)
        }
        _isListening.value = false
        _inputAmplitude.value = 0f
    }

    fun destroy() {
        cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
