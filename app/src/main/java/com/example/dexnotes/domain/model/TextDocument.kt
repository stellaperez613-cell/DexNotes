package com.example.dexnotes.domain.model

import android.net.Uri
import java.nio.charset.Charset

data class TextDocument(
    val uri: Uri?,
    val displayName: String,
    val charset: Charset,
    val hasBom: Boolean,
    val lineEnding: LineEnding,
)

data class LoadedDocument(
    val document: TextDocument,
    val text: String,
)
