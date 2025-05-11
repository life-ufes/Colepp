package com.example.transferdata.presentation.createNewRecording

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transferdata.R
import com.example.transferdata.bluetoothHandler.BluetoothViewModel
import com.example.transferdata.polarHandler.PolarBleApiSingleton
import com.example.transferdata.common.composeUI.CardOfWearable
import com.example.transferdata.common.composeUI.DefaultButton
import com.example.transferdata.common.composeUI.LabeledTextField
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.utils.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CreateNewRecording(
    viewModel: CreateNewRecordingViewModel = hiltViewModel(),
    bluetoothViewModel: BluetoothViewModel = hiltViewModel(),
    createdNewRecording: () -> Unit,
    onBackPressed: () -> Unit,
    apiAvailable: Boolean,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.setPolarApi(
                PolarBleApiSingleton.getInstance(context)
            )
        }
    }

    val nameValue = viewModel.nameValue.collectAsState()
    val descriptionValue = viewModel.descriptionValue.collectAsState()

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
                title = stringResource(R.string.create_new_recording),
                onBackPressed = onBackPressed,
                hasCloseIcon = false
            )
            ScreenContent(
                nameValue = nameValue.value,
                onDescriptionValueChange = viewModel::onDescriptionValueChange,
                descriptionValue = descriptionValue.value,
                onNameValueChange = viewModel::onNameValueChange,
                createdNewRecording = createdNewRecording,
                apiAvailable = apiAvailable,
            )
        }
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    nameValue: String,
    descriptionValue: String,
    onNameValueChange: (String) -> Unit,
    onDescriptionValueChange: (String) -> Unit,
    createdNewRecording: () -> Unit,
    apiAvailable: Boolean,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Size.size05),
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
        )
        CardOfWearable(
            title = "Polar H10",
            subtitle = "Conectado",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
        CardOfWearable(
            title = "API available",
            subtitle = "$apiAvailable",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.weight(1f))
        DefaultButton(
            text = "Iniciar",
            onClick = createdNewRecording,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun ScreenContentPreview() {
    ScreenContent(
        nameValue = "Polar H10",
        descriptionValue = "Conectado",
        onNameValueChange = {},
        onDescriptionValueChange = {},
        createdNewRecording = {},
        apiAvailable = true,
    )
}