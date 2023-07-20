package com.mine.voicetotext

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.log

class VoiceToTextParser(
    private val app: Application
) : RecognitionListener {

    private val _state = MutableStateFlow(VoiceToTextParserState())

    val state: StateFlow<VoiceToTextParserState>
        get() = _state.asStateFlow()

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(app)

    fun startListening(languageCode: String = "en") {
        // Clears the state
        _state.update { VoiceToTextParserState() }

        // If is not available shows the error
        if (!SpeechRecognizer.isRecognitionAvailable(app)) {
            _state.update {
                it.copy(
                    error = "Speech recognition is not available"
                )
            }
        }

        // Creates an Intent for speech recognition in a specified language
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        }

        // Sets the listener that will receive all the callbacks
        recognizer.setRecognitionListener(this)

        // Starts listening for speech
        recognizer.startListening(intent)

        // Indicates that speech recognition has started
        _state.update {
            it.copy(
                isSpeaking = true
            )
        }
    }

    fun stopListening() {
        // Indicates that speech recognition has stopped
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }

        // Stops listening for speech
        recognizer.stopListening()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        // Clears the error
        _state.update {
            it.copy(
                error = null
            )
        }
    }

    override fun onBeginningOfSpeech() {
        // Implement onBeginningOfSpeech functionality here
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Implement onRmsChanged functionality here
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // Implement onBufferReceived functionality here
    }

    override fun onEndOfSpeech() {
        // Indicates that speech recognition has stopped
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onError(error: Int) {
        if (error == SpeechRecognizer.ERROR_CLIENT) {
            return
        }
        _state.update {
            it.copy(
                error = "Error: $error"
            )
        }
    }

    override fun onResults(results: Bundle?) {
        // Gets recognition results
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { text ->
                _state.update {
                    Log.e(TAG, "onResults: "+it )
                    it.copy(
                        spokenText = text
                    )

                }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        // Implement onPartialResults functionality here
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // Implement onEvent functionality here
    }
}
