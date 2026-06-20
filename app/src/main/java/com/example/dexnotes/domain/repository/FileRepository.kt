package com.example.dexnotes.domain.repository

import android.net.Uri
import com.example.dexnotes.domain.model.LineEnding
import com.example.dexnotes.domain.model.LoadedDocument
import java.nio.charset.Charset

interface FileRepository {
    suspend fun read(uri: Uri): Result<LoadedDocument>
    suspend fun write(
        uri: Uri,
        text: String,
        charset: Charset,
        hasBom: Boolean,
        lineEnding: LineEnding,
    ): Result<Unit>
}
