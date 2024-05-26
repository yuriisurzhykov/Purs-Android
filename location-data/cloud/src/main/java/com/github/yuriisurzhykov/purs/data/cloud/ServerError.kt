package com.github.yuriisurzhykov.purs.data.cloud

import java.io.IOException

data class ServerError(val code: Int, override val message: String) :
    IOException("Response ended with error code: $code. $message")