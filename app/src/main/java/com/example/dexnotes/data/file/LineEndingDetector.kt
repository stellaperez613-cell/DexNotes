package com.example.dexnotes.data.file

import com.example.dexnotes.domain.model.LineEnding

internal object LineEndingDetector {

    /** Scan for the first line-ending sequence and return it; defaults to LF. */
    fun detect(text: String): LineEnding {
        for (i in text.indices) {
            when (text[i]) {
                '\r' -> return if (i + 1 < text.length && text[i + 1] == '\n') LineEnding.CRLF
                        else LineEnding.CR
                '\n' -> return LineEnding.LF
            }
        }
        return LineEnding.LF
    }

    /** Replace all line endings with '\n' for in-memory storage. */
    fun normalize(text: String): String = text.replace("\r\n", "\n").replace("\r", "\n")
}
