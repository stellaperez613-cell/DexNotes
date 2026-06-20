package com.example.dexnotes.ui.editor

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dexnotes.domain.model.LineEnding
import com.example.dexnotes.domain.model.TextDocument
import com.example.dexnotes.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val fileRepository: FileRepository,
) : ViewModel() {

    val textFieldState = TextFieldState()

    private var savedSnapshot: String = ""

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            snapshotFlow { textFieldState.text.toString() }
                .collect { current ->
                    _uiState.update { it.copy(isDirty = current != savedSnapshot) }
                }
        }
    }

    fun newDocument() {
        _uiState.update {
            it.copy(
                document = TextDocument(
                    uri = null,
                    displayName = "untitled.txt",
                    charset = Charsets.UTF_8,
                    hasBom = false,
                    lineEnding = LineEnding.LF,
                ),
                isDirty = false,
                error = null,
                needsSaveAs = false,
            )
        }
        textFieldState.setTextAndPlaceCursorAtEnd("")
        savedSnapshot = ""
    }

    fun loadDocument(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            fileRepository.read(uri).fold(
                onSuccess = { loaded ->
                    takePersistablePermission(uri)
                    textFieldState.setTextAndPlaceCursorAtEnd(loaded.text)
                    savedSnapshot = loaded.text
                    _uiState.update {
                        it.copy(
                            document = loaded.document,
                            isDirty = false,
                            isLoading = false,
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.localizedMessage ?: e.message)
                    }
                },
            )
        }
    }

    fun save() {
        val doc = _uiState.value.document
        if (doc.uri == null) {
            _uiState.update { it.copy(needsSaveAs = true) }
            return
        }
        val currentText = textFieldState.text.toString()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            fileRepository.write(doc.uri, currentText, doc.charset, doc.hasBom, doc.lineEnding).fold(
                onSuccess = {
                    savedSnapshot = currentText
                    _uiState.update { it.copy(isDirty = false, isLoading = false) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.localizedMessage ?: e.message)
                    }
                },
            )
        }
    }

    fun saveAs(uri: Uri, suggestedName: String) {
        val doc = _uiState.value.document
        val currentText = textFieldState.text.toString()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            takePersistablePermission(uri)
            fileRepository.write(uri, currentText, doc.charset, doc.hasBom, doc.lineEnding).fold(
                onSuccess = {
                    savedSnapshot = currentText
                    val actualName = DocumentFile.fromSingleUri(appContext, uri)?.name ?: suggestedName
                    _uiState.update {
                        it.copy(
                            document = doc.copy(uri = uri, displayName = actualName),
                            isDirty = false,
                            isLoading = false,
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.localizedMessage ?: e.message)
                    }
                },
            )
        }
    }

    fun onSaveAsLaunched() {
        _uiState.update { it.copy(needsSaveAs = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun takePersistablePermission(uri: Uri) {
        val rwFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        runCatching {
            appContext.contentResolver.takePersistableUriPermission(uri, rwFlags)
        }.onFailure {
            runCatching {
                appContext.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
        }
    }
}
