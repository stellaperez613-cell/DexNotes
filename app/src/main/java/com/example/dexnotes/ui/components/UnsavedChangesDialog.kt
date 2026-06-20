package com.example.dexnotes.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.dexnotes.R

@Composable
fun UnsavedChangesDialog(
    documentName: String,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.dialog_unsaved_title)) },
        text = { Text(stringResource(R.string.dialog_unsaved_message, documentName)) },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDiscard) {
                Text(stringResource(R.string.action_discard))
            }
        },
    )
}
