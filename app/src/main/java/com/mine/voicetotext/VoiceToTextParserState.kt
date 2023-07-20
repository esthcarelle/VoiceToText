package com.mine.voicetotext

data class VoiceToTextParserState(
    val isSpeaking: Boolean = false,
    val spokenText: String = "",
    val error: String? = null
)
