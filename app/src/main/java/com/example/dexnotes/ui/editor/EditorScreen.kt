package com.example.dexnotes.ui.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dexnotes.R
import com.example.dexnotes.ui.components.UnsavedChangesDialog

// TODO Phase 2+: Edit and View menus, tab strip, line numbers, wrap toggle.

private enum class PendingAction { NEW, OPEN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(vm: EditorViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── SAF launchers ──────────────────────────────────────────────────────
    val openLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(vm::loadDocument) }

    val currentDisplayName by rememberUpdatedState(state.document.displayName)
    val createLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri -> uri?.let { vm.saveAs(it, currentDisplayName) } }

    // ── needsSaveAs: launch CreateDocument picker ──────────────────────────
    LaunchedEffect(state.needsSaveAs) {
        if (state.needsSaveAs) {
            vm.onSaveAsLaunched()
            createLauncher.launch(currentDisplayName)
        }
    }

    // ── Error → Snackbar ───────────────────────────────────────────────────
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    // ── Unsaved-changes dialog state ────────────────────────────────────────
    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    // After a save completes, execute the queued action.
    var waitingForSave by remember { mutableStateOf(false) }
    LaunchedEffect(state.isDirty, state.isLoading) {
        if (waitingForSave && !state.isLoading && !state.isDirty) {
            waitingForSave = false
            val action = pendingAction
            pendingAction = null
            when (action) {
                PendingAction.NEW -> vm.newDocument()
                PendingAction.OPEN -> openLauncher.launch(arrayOf("*/*"))
                null -> Unit
            }
        }
    }

    fun requestNew() {
        if (state.isDirty) {
            pendingAction = PendingAction.NEW
            showUnsavedDialog = true
        } else {
            vm.newDocument()
        }
    }

    fun requestOpen() {
        if (state.isDirty) {
            pendingAction = PendingAction.OPEN
            showUnsavedDialog = true
        } else {
            openLauncher.launch(arrayOf("*/*"))
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EditorTopBar(
                title = buildString {
                    if (state.isDirty) append("• ")
                    append(state.document.displayName)
                },
                onNew = ::requestNew,
                onOpen = ::requestOpen,
                onSave = { vm.save() },
                onSaveAs = { createLauncher.launch(state.document.displayName) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            BasicTextField(
                state = vm.textFieldState,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                ),
                lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 1),
            )

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showUnsavedDialog) {
        UnsavedChangesDialog(
            documentName = state.document.displayName,
            onSave = {
                showUnsavedDialog = false
                waitingForSave = true
                vm.save()
            },
            onDiscard = {
                showUnsavedDialog = false
                val action = pendingAction
                pendingAction = null
                when (action) {
                    PendingAction.NEW -> vm.newDocument()
                    PendingAction.OPEN -> openLauncher.launch(arrayOf("*/*"))
                    null -> Unit
                }
            },
            onCancel = {
                showUnsavedDialog = false
                pendingAction = null
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTopBar(
    title: String,
    onNew: () -> Unit,
    onOpen: () -> Unit,
    onSave: () -> Unit,
    onSaveAs: () -> Unit,
) {
    var fileMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        actions = {
            // Room left here for Edit / View menus in later phases.
            TextButton(onClick = { fileMenuExpanded = true }) {
                Text(stringResource(R.string.menu_file))
            }
            DropdownMenu(
                expanded = fileMenuExpanded,
                onDismissRequest = { fileMenuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_new)) },
                    onClick = { fileMenuExpanded = false; onNew() },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_open)) },
                    onClick = { fileMenuExpanded = false; onOpen() },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_save)) },
                    onClick = { fileMenuExpanded = false; onSave() },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_save_as)) },
                    onClick = { fileMenuExpanded = false; onSaveAs() },
                )
            }
        },
    )
}
