package com.example.dexnotes.ui.editor

import com.example.dexnotes.domain.model.LineEnding
import com.example.dexnotes.domain.model.TextDocument

data class EditorUiState(
    val document: TextDocument = TextDocument(
        uri = null,
        displayName = "untitled.txt",
        charset = Charsets.UTF_8,
        hasBom = false,
        lineEnding = LineEnding.LF,
    ),
    val isDirty: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    /** True while the UI should launch the SAF CreateDocument picker. */
    val needsSaveAs: Boolean = false,
)
