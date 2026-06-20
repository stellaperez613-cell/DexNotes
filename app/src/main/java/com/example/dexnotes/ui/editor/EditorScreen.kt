package com.example.dexnotes.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.dexnotes.R

// TODO Phase 1+: real toolbar, tab strip, and editor pane replace the placeholder below.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.editor_top_bar_title)) })
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.editor_empty_state))
        }
    }
}
