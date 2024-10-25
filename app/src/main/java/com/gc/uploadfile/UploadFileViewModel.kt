package com.gc.uploadfile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.io.FileNotFoundException
import kotlin.coroutines.cancellation.CancellationException


//
// Created by Code For Android on 25/10/24.
// Copyright (c) 2024 CFA. All rights reserved.
//

class UploadFileViewModel(
    private val repository: FileRepository
) : ViewModel() {

    var state by mutableStateOf(UploadState())
        private set


    private var uploadJob: Job? = null

    fun uploadFile(contentUri: Uri) {

        uploadJob = repository.uploadFile(contentUri)
            .onStart {
                state = state.copy(
                    isUploading = true,
                    isUploadComplete = false,
                    progress = 0f,
                    errorMessage = null)
            }
            .onEach {
                state = state.copy(
                    progress = it.byteSent.toFloat() / it.totalBytes
                )
            }
            .onCompletion { cause ->
                if (cause == null) {
                    state = state.copy(
                        isUploading = false,
                        isUploadComplete = true
                    )
                } else if (cause is CancellationException)  {
                     state = state.copy(
                         isUploading = false,
                         isUploadComplete = false,
                         errorMessage = cause.message,
                         progress = 0f
                     )
                }
            }.catch { cause ->
                val message = when (cause) {
                    is OutOfMemoryError -> {
                        "Not enough memory"
                    }
                    is FileNotFoundException -> {
                        "File not Found"
                    }
                    is UnresolvedAddressException -> {
                        "Unable to resolve host"
                    }
                    else -> {
                        "Unknown error"
                    }
                }

                state = state.copy(
                    isUploading = false,
                    isUploadComplete = false,
                    errorMessage = message
                )

            }
            .launchIn(
            viewModelScope
        )



    }


    fun cancelUpload() {
        uploadJob?.cancel()
    }




}


data class UploadState(
    val isUploading: Boolean = false,
    val isUploadComplete: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null
)