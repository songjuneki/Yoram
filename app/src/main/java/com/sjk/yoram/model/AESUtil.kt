package com.sjk.yoram.model

import java.lang.Exception
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AESUtil {
    private val key = "SJKSJKSJKSJKSJKSJKSJKSJK"
    fun Encrypt(plain: String): String {
        val keyByte = key.toByteArray()
        val keySpec = SecretKeySpec(keyByte, "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding").apply { init(Cipher.ENCRYPT_MODE, keySpec) }

        val enc = cipher.doFinal(plain.toByteArray())
        return enc.toHex()
    }

    fun Decrypt(enc: String): String {
        val keyByte = key.toByteArray()
        val keySpec = SecretKeySpec(keyByte, "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding").apply { init(Cipher.DECRYPT_MODE, keySpec) }

        return try {
            val encByte = enc.decodeHex()
            cipher.doFinal(encByte).decodeToString()
        } catch (e: Exception) {
            "####"
        }
    }
    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
    fun String.decodeHex(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}