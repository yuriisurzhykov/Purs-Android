package com.github.yuriisurzhykov.purs.core.android

import android.util.Log
import com.github.yuriisurzhykov.purs.core.Logger

fun AndroidLogcatLogger(): Logger = object : Logger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
}