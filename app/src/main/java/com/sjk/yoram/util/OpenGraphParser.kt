package com.sjk.yoram.util

import android.util.ArrayMap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import javax.net.ssl.HttpsURLConnection

object OpenGraphParser {
    suspend fun parse(url: String,
                      onLoading: () -> Unit,
                      onSuccess: (ArrayMap<String, String>) -> Unit,
                      onFailure: (String) -> Unit) {
        onLoading.invoke()
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null

            try {
                connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val result = connection.inputStream?.run {
                    val reader = BufferedReader(InputStreamReader(this))
                    val buffer = StringBuffer()

                    var line: String? = ""
                    while (reader.readLine().also { line = it } != null) {
                        buffer.append(line)
                    }
                    buffer.toString()
                }
                onSuccess(parseOgTag(result ?: ""))
            } catch (e: Exception) {
                onFailure(e.message ?: "og parser has unknown exception")
            } finally {
                connection?.disconnect()
            }
        }
    }

    suspend fun parse(url: String): ArrayMap<String, String> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            val conn: URLConnection = URL(url).openConnection()
            connection = conn as HttpURLConnection
            connection.connect()

            val result = connection.inputStream?.run {
                val reader = BufferedReader(InputStreamReader(this))
                val buffer = StringBuffer()

                var line: String? = ""
                while (reader.readLine().also { line = it } != null) {
                    buffer.append(line)
                }
                buffer.toString()
            }
            parseOgTag(result ?: "")
        } catch (e: Exception) {
            Log.d("JKJK", "OGParser Exception :: $e")
            return@withContext ArrayMap()
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseOgTag(html: String): ArrayMap<String, String> {
        val ogTags = ArrayMap<String, String>()

        Regex("<meta property[^>]([^<]*)>").findAll(html).forEach { prop ->
            val metaProperty = prop.groupValues.getOrNull(1) ?: ""

            Regex("\"og:(.*)\" content=\"(.*)\"").find(metaProperty)?.let {
                val ogType = it.groupValues.getOrNull(1)
                val content = it.groupValues.getOrNull(2)

                if (ogType != null && content != null) ogTags[ogType] = content
            }
        }
        return ogTags
    }
}