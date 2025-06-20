package com.example.transferdata.presentation.recordDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.transferdata.R
import com.example.transferdata.common.composeUI.LoadingScreen
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.utils.ScreenState
import com.example.transferdata.common.utils.Size

@Composable
fun RecordDetailScreen(
    viewModel: RecordDetailViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    createDatasetFile: (Long) -> Unit
) {
    val screenState = viewModel.screenState.collectAsState()
    val record = viewModel.record.collectAsState()

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
            modifier = Modifier.fillMaxSize()
        ) {
            Toolbar(
                title = stringResource(R.string.new_recording),
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
                    record.value?.let {
                        ScreenContent(
                            title = it.title,
                            description = it.description,
                            createDatasetFile = { createDatasetFile(it.id) }
                        )
                    }
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
    title: String,
    description: String? = null,
    createDatasetFile: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Size.size05)
            .padding(top = Size.size04),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        Row {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_download),
                contentDescription = null,
                modifier = Modifier.clickable { createDatasetFile() }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = null
            )
        }
        description?.let {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenContentPreview() {
    ScreenContent(
        title = "Sample Title",
        description = "This is a sample description for the record detail screen.",
        createDatasetFile = {}
    )
}