package com.gc.uploadfile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gc.uploadfile.HttpClient.client
import com.gc.uploadfile.ui.theme.UploadFileTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UploadFileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val viewModel = viewModel {
                        UploadFileViewModel(
                            FileRepository(
                                httpClient = client,
                                fileReader = FileReader(applicationContext)
                            )
                        )
                    }
                    val state = viewModel.state

                    val filePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { contentUri ->
                        contentUri?.let {
                            viewModel.uploadFile(it)
                        }
                    }

                    LaunchedEffect(key1 = state.errorMessage) {
                        state.errorMessage?.let {
                            Toast.makeText(
                                applicationContext,
                                state.errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    LaunchedEffect(key1 = state.isUploadComplete) {
                        if (state.isUploadComplete) {
                            Toast.makeText(
                                applicationContext,
                                "Upload Complete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {

                        when {
                            !state.isUploading -> {
                                Button(onClick = {
                                    filePickerLauncher.launch("*/*")
                                }) {
                                    Text(text = "Pick a file")
                                }
                            }

                            else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    LinearProgressIndicator(
                                        progress = { state.progress },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                            .height(16.dp)
                                    )
                                    Text(text = "${(state.progress * 100).roundToInt()} %")

                                    Button(onClick = { viewModel.cancelUpload() }) {
                                        Text(text = "Cancel Upload")
                                    }

                                }


                            }


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
    UploadFileTheme {
        Greeting("Android")
    }
}