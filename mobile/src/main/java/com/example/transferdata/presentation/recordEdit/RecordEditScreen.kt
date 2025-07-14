package com.example.transferdata.presentation.recordEdit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.transferdata.R
import com.example.transferdata.common.composeUI.DefaultButton
import com.example.transferdata.common.composeUI.LabeledTextField
import com.example.transferdata.common.composeUI.LoadingScreen
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.composeUI.arrangementLastItemOnBottom
import com.example.transferdata.common.utils.ScreenState
import com.example.transferdata.common.utils.Size

@Composable
fun RecordEditScreen(
    viewModel: RecordEditViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSave: () -> Unit
) {
    val screenState = viewModel.screenState.collectAsState()
    val titleEdit = viewModel.title.collectAsState()
    val descriptionEdit = viewModel.description.collectAsState()
    val buttonEnabled = viewModel.buttonEnabled.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getContent()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Size.size03)
        ) {
            Toolbar(
                title = stringResource(R.string.edit_record),
                onBackPressed = onBackPressed,
                hasCloseIcon = false
            )
            when (screenState.value) {
                ScreenState.Loading -> {
                    LoadingScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ScreenState.Content -> {
                    ScreenContent(
                        nameValue = titleEdit.value,
                        onNameValueChange = viewModel::updateTitle,
                        descriptionValue = descriptionEdit.value,
                        onDescriptionValueChange = viewModel::updateDescription,
                        createdNewRecording = {
                            viewModel.setContent(onSave)
                        },
                        buttonEnabled = buttonEnabled.value,
                    )
                }

                is ScreenState.Error -> {
                    // Show error state
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    nameValue: String,
    onNameValueChange: (String) -> Unit,
    descriptionValue: String,
    onDescriptionValueChange: (String) -> Unit,
    createdNewRecording: () -> Unit,
    buttonEnabled: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Size.size05)
            .padding(bottom = Size.size09),
        verticalArrangement = arrangementLastItemOnBottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Size.size05)
                .padding(bottom = Size.size09),
            verticalArrangement = Arrangement.spacedBy(Size.size03)
        ) {
            LabeledTextField(
                value = nameValue,
                label = stringResource(R.string.name_label),
                onValueChange = onNameValueChange,
            )
            LabeledTextField(
                value = descriptionValue,
                label = stringResource(R.string.description_label),
                onValueChange = onDescriptionValueChange,
                modifier = Modifier.fillMaxWidth(),
                maxLines = Int.MAX_VALUE,
            )
        }
        DefaultButton(
            text = stringResource(R.string.btn_save_record_edited_recording),
            onClick = createdNewRecording,
            modifier = Modifier.fillMaxWidth(),
            enabled = buttonEnabled
        )
    }
}

@Preview
@Composable
private fun ScreenContentPreview() {
    ScreenContent(
        nameValue = "Sample Title",
        onNameValueChange = {},
        descriptionValue = "This is a sample description for the record edit screen.",
        onDescriptionValueChange = {},
        createdNewRecording = {},
        buttonEnabled = true
    )
}