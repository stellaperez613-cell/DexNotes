package com.example.dexnotes.data.file

import java.nio.charset.Charset

internal object EncodingDetector {

    data class Result(
        val charset: Charset,
        val hasBom: Boolean,
        val bomLength: Int,
    )

    fun detect(bytes: ByteArray): Result = when {
        bytes.size >= 3 &&
            bytes[0] == 0xEF.toByte() &&
            bytes[1] == 0xBB.toByte() &&
            bytes[2] == 0xBF.toByte() ->
            Result(Charsets.UTF_8, hasBom = true, bomLength = 3)

        bytes.size >= 2 &&
            bytes[0] == 0xFF.toByte() &&
            bytes[1] == 0xFE.toByte() ->
            Result(Charsets.UTF_16LE, hasBom = true, bomLength = 2)

        bytes.size >= 2 &&
            bytes[0] == 0xFE.toByte() &&
            bytes[1] == 0xFF.toByte() ->
            Result(Charsets.UTF_16BE, hasBom = true, bomLength = 2)

        else -> Result(Charsets.UTF_8, hasBom = false, bomLength = 0)
    }

    fun bomBytesFor(charset: Charset): ByteArray = when (charset) {
        Charsets.UTF_8 -> byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        Charsets.UTF_16LE -> byteArrayOf(0xFF.toByte(), 0xFE.toByte())
        Charsets.UTF_16BE -> byteArrayOf(0xFE.toByte(), 0xFF.toByte())
        else -> byteArrayOf()
    }
}
