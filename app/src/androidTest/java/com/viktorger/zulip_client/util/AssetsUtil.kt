package com.viktorger.zulip_client.util

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import java.io.BufferedReader
import java.io.InputStreamReader

object AssetsUtil {
    fun fromAssets(path: String, vararg formatArgs: Any? = emptyArray()): String {
        val assetManager = getInstrumentation().context.assets
        val stringFile = BufferedReader(InputStreamReader(assetManager.open(path), Charsets.UTF_8)).readText()
        return if (formatArgs.isNotEmpty()) {
            stringFile.format(*formatArgs)
        } else {
            stringFile
        }
    }
}