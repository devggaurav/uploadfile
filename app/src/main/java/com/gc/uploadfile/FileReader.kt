package com.gc.uploadfile

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID


//
// Created by Code For Android on 25/10/24.
// Copyright (c) 2024 CFA. All rights reserved.
//

class FileReader(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {


    suspend fun uriToFileInfo(contentUri: Uri): FileInfo {
        return withContext(ioDispatcher) {
            val bytes = context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                inputStream.readBytes()

            } ?: byteArrayOf()

            val filename = UUID.randomUUID().toString()
            val mimeType = context.contentResolver.getType(contentUri) ?: ""

            FileInfo(
                name = filename,
                mimeType = mimeType,
                bytes = bytes
            )

        }

    }


}


class FileInfo(
    val name: String,
    val mimeType: String,
    val bytes: ByteArray
)