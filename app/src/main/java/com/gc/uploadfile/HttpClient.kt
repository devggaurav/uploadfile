package com.gc.uploadfile

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging


//
// Created by Code For Android on 25/10/24.
// Copyright (c) 2024 CFA. All rights reserved.
//

object HttpClient {

    val client by lazy {
        HttpClient(CIO){
            install(Logging){
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }
    }

}