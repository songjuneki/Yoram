package com.sjk.yoram.model

import android.util.Base64

class MySecurity {
    fun encodeBase64(plain: String): String {
        return Base64.encode(plain.toByteArray(), Base64.NO_WRAP).toHex()
    }

    fun decodeBase64(cipher: String): String {
        return Base64.decode(cipher.hexToByteArray(), Base64.NO_WRAP).decodeToString()
    }
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
fun String.hexToByteArray(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()