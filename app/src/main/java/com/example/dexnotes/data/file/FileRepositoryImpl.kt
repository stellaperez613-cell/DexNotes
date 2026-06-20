package com.example.dexnotes.data.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import com.example.dexnotes.domain.model.LineEnding
import com.example.dexnotes.domain.model.LoadedDocument
import com.example.dexnotes.domain.model.TextDocument
import com.example.dexnotes.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : FileRepository {

    override suspend fun read(uri: Uri): Result<LoadedDocument> = withContext(Dispatchers.IO) {
        runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("Could not open stream for $uri")

            val encoding = EncodingDetector.detect(bytes)
            val rawText = String(bytes, encoding.bomLength, bytes.size - encoding.bomLength, encoding.charset)
            val lineEnding = LineEndingDetector.detect(rawText)
            val normalizedText = LineEndingDetector.normalize(rawText)
            val displayName = resolveDisplayName(uri)

            LoadedDocument(
                document = TextDocument(
                    uri = uri,
                    displayName = displayName,
                    charset = encoding.charset,
                    hasBom = encoding.hasBom,
                    lineEnding = lineEnding,
                ),
                text = normalizedText,
            )
        }
    }

    override suspend fun write(
        uri: Uri,
        text: String,
        charset: Charset,
        hasBom: Boolean,
        lineEnding: LineEnding,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val denormalized = text.replace("\n", lineEnding.sequence)
            val textBytes = denormalized.toByteArray(charset)
            val payload = if (hasBom) EncodingDetector.bomBytesFor(charset) + textBytes else textBytes

            context.contentResolver.openOutputStream(uri, "wt")?.use { stream ->
                stream.write(payload)
            } ?: error("Could not open output stream for $uri")
        }
    }

    private fun resolveDisplayName(uri: Uri): String {
        DocumentFile.fromSingleUri(context, uri)?.name?.let { return it }
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null, null, null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) cursor.getString(idx)?.let { return it }
            }
        }
        return "untitled.txt"
    }
}
