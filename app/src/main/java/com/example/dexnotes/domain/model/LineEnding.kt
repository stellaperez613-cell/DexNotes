package com.example.dexnotes.domain.model

enum class LineEnding(val sequence: String) {
    LF("\n"),
    CRLF("\r\n"),
    CR("\r"),
}
