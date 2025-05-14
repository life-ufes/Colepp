package com.example.transferdata.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.transferdata.R
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.utils.Size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel(),
    onRecordingPressed: () -> Unit,
    createNewRecording: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.home_toolbar_title),
                hasCloseIcon = false,
                hasBackIcon = false,
            )
        },
        bottomBar = {
            BottomBar(
                homeClick = { /* TODO: Handle home click */ },
                guidanceClick = { /* TODO: Handle guidance click */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = createNewRecording,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = stringResource(R.string.create_new_recording)
                )
            }
        }
    )
    { paddingValues ->
        HomeContent(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
    ) {

    }
}

@Composable
private fun BottomBar(
    homeClick: () -> Unit,
    guidanceClick: () -> Unit
) {
    BottomAppBar {
        BottomBarItem(
            icon = R.drawable.ic_home,
            title = stringResource(R.string.home_bottom_bar_home),
            onClick = homeClick,
            modifier = Modifier.weight(1f)
        )
        BottomBarItem(
            icon = R.drawable.ic_guidance,
            title = stringResource(R.string.home_bottom_bar_guidance),
            onClick = guidanceClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(Size.size09)
        )
        Text(
            text = title,
            fontSize = 10.sp,
        )
    }
}

@Preview
@Composable
private fun BottomAppBarPreview() {
    BottomBar(
        homeClick = {},
        guidanceClick = {}
    )
}

@Preview
@Composable
private fun HomeContentPreview() {
    HomeScreen(
        onRecordingPressed = {},
        createNewRecording = {}
    )
}