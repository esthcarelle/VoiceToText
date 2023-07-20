package com.mine.voicetotext

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mine.voicetotext.ui.theme.VoiceToTextTheme

class MainActivity : ComponentActivity() {
    private val voiceToText by lazy {
        VoiceToTextParser(application)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var canRecord by remember {
                mutableStateOf(false)
            }

            // Creates an permission request
            val recordAudioLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    canRecord = isGranted
                }
            )

            LaunchedEffect(key1 = recordAudioLauncher) {
                // Launches the permission request
                recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            val state by voiceToText.state.collectAsState()

            Scaffold(
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if (canRecord) {
                                if (!state.isSpeaking) {
                                    voiceToText.startListening("en")
                                } else {
                                    voiceToText.stopListening()
                                }
                            }
                        }
                    ) {
                        AnimatedContent(targetState = state.isSpeaking) { isSpeaking ->
                            if (isSpeaking) {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Send,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(targetState = state.isSpeaking) { isSpeaking ->
                        if (isSpeaking) {
                            Text(
                                text = "Speak...",
                                style = MaterialTheme.typography.titleMedium)
                        } else {
                            println(state.spokenText)
                            Text(
                                text = state.spokenText.ifEmpty { "Click on record" },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VoiceToTextTheme {
        Greeting("Android")
    }
}