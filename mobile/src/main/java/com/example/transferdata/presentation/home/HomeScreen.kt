package com.example.transferdata.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transferdata.R
import com.example.transferdata.common.composeUI.ButtonStyle
import com.example.transferdata.common.composeUI.DefaultButton
import com.example.transferdata.common.composeUI.DefaultDialog
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.utils.Size
import com.example.transferdata.common.utils.TextStyles
import com.example.transferdata.common.utils.toFormat
import com.example.transferdata.database.model.RecordEntity
import com.example.transferdata.presentation.home.HomeViewModel.Companion.HOME_SECTION
import com.example.transferdata.presentation.home.HomeViewModel.Companion.INSTRUCTIONS_SECTION
import java.util.Date

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    createNewRecording: () -> Unit,
    onRecordClicked: (Long) -> Unit,
    onEditClicked: (Long) -> Unit
) {
    val records = viewModel.recordings.collectAsState()
    val showDialog = viewModel.showDeleteDialog.collectAsState()
    val section = viewModel.section.collectAsState()
    val lazyListHomeState = rememberLazyListState()
    val lazyListInstructionsState = rememberLazyListState()

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
                homeClick = { viewModel.setSection(HOME_SECTION) },
                newRecordingClick = createNewRecording,
                guidanceClick = { viewModel.setSection(INSTRUCTIONS_SECTION) }
            )
        }
    )
    { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        AnimatedVisibility(
            visible = section.value == HOME_SECTION,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            HomeContent(
                modifier = modifier
                    .fillMaxSize(),
                records = records.value,
                onRecordClicked = onRecordClicked,
                onRemoveClicked = viewModel::setShowDeleteDialog,
                onEditClicked = onEditClicked,
                state = lazyListHomeState
            )
        }
        AnimatedVisibility(
            visible = section.value == INSTRUCTIONS_SECTION,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            InstructionsContent(
                modifier = modifier.fillMaxSize(),
                state = lazyListInstructionsState
            )
        }
    }
    if (showDialog.value.first && showDialog.value.second != null) {
        DefaultDialog(
            title = stringResource(
                id = R.string.home_delete_record_dialog_title,
                showDialog.value.second!!.title
            ),
            message = stringResource(id = R.string.home_delete_record_dialog_message),
            image = null,
            primaryButton = {
                DefaultButton(
                    text = stringResource(id = R.string.home_delete_record_dialog_confirm_button),
                    buttonStyle = ButtonStyle.outlinedButton(),
                    onClick = { viewModel.deleteRecording(showDialog.value.second!!.id) }
                )
            },
            secondaryButton = {
                Text(
                    text = stringResource(id = R.string.home_delete_record_dialog_cancel_button),
                    style = TextStyles.TextSGray,
                    modifier = Modifier.clickable { viewModel.dismissDeleteDialog() }
                )
            }
        )
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    records: List<RecordEntity>,
    onRecordClicked: (Long) -> Unit,
    onRemoveClicked: (RecordEntity) -> Unit,
    onEditClicked: (Long) -> Unit,
    state: LazyListState
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = Size.size05),
        verticalArrangement = Arrangement.spacedBy(Size.size04),
        state = state
    ) {
        item {
            Spacer(modifier = Modifier.size(Size.size01))
        }
        items(records) { record ->
            RecordCard(
                record = record,
                onCardClicked = onRecordClicked,
                onRemoveClicked = { onRemoveClicked(record) },
                onEditClicked = onEditClicked
            )
        }
        item {
            Spacer(modifier = Modifier.size(Size.size06))
        }
    }
}

@Composable
private fun RecordCard(
    modifier: Modifier = Modifier,
    record: RecordEntity,
    onCardClicked: (Long) -> Unit,
    onRemoveClicked: () -> Unit,
    onEditClicked: (Long) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.card_background_color),
                shape = RoundedCornerShape(Size.size04)
            )
            .clickable { onCardClicked(record.id) }
            .padding(Size.size05),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Size.size04)
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = record.title,
                style = TextStyles.TitleM,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (record.description.isNotEmpty()) {
                Text(
                    text = record.description,
                    style = TextStyles.TextS,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = Date(record.starRecordingMilli).toFormat("dd/MM/yyyy HH:mm:ss"),
                style = TextStyles.TextSSmall
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            if (record.shared == 0) {
                Surface(
                    modifier = modifier,
                    shape = RoundedCornerShape(Size.size05),
                    color = colorResource(id = R.color.red_light),
                    content = {
                        Text(
                            text = stringResource(id = R.string.home_record_not_shared),
                            style = TextStyles.TextSBold,
                            color = colorResource(id = R.color.red_dark),
                            modifier = Modifier.padding(Size.size02)
                        )
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Size.size04)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = null,
                    modifier = Modifier
                        .size(Size.size08)
                        .clickable { onEditClicked(record.id) }
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    modifier = Modifier
                        .size(Size.size08)
                        .clickable { onRemoveClicked() }
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    homeClick: () -> Unit,
    newRecordingClick: () -> Unit,
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
            icon = R.drawable.ic_add,
            title = stringResource(R.string.create_new_recording),
            onClick = newRecordingClick,
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
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            { onClick() },
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
            style = TextStyles.TextSBold
        )
    }
}

@Composable
private fun InstructionsContent(
    modifier: Modifier,
    state: LazyListState
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = Size.size05),
        verticalArrangement = Arrangement.spacedBy(Size.size04),
        state = state
    ) {
        item {
            Spacer(modifier = Modifier.size(Size.size01))
        }
        items(List(5) { "Instruction ${it + 1}" }) { instruction ->
            val myId = "sharedIcon"

            val text = buildAnnotatedString {
                append("Status: ")
                appendInlineContent(myId, "[icon]")
                append(" Não compartilhado ".repeat(5))
            }

            val inlineContent = mapOf(
                myId to InlineTextContent(
                    Placeholder(
                        width = 16.sp,
                        height = 16.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, // ou outro ícone
                        contentDescription = "Ícone compartilhamento",
                        tint = Color.Gray
                    )
                }
            )

            Text(
                text = text,
                inlineContent = inlineContent
            )
        }
        item {
            Spacer(modifier = Modifier.size(Size.size06))
        }
    }
}

@Preview
@Composable
private fun BottomAppBarPreview() {
    BottomBar(
        homeClick = {},
        newRecordingClick = {},
        guidanceClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        modifier = Modifier.fillMaxSize(),
        records = listOf(
            RecordEntity(
                id = 1L,
                title = "Sample Record 1",
                description = "This is a sample record description.",
                starRecordingMilli = System.currentTimeMillis(),
                shared = 1
            ),
            RecordEntity(
                id = 1L,
                title = "Sample Record 2",
                description = "This is a sample record description.",
                starRecordingMilli = System.currentTimeMillis()
            )
        ),
        state = rememberLazyListState(),
        onRecordClicked = {},
        onRemoveClicked = {},
        onEditClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun InstructionsContentPreview() {
    InstructionsContent(
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyListState()
    )
}