package com.example.transferdata.common.composeUI

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.transferdata.R
import com.example.transferdata.common.utils.TextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    title: String? = null,
    hasBackIcon: Boolean = true,
    hasCloseIcon: Boolean = true,
    onBackPressed: () -> Unit = {},
    onClosePressed: () -> Unit = {},
    backgroundColor: Color = colorResource(id = R.color.toolbar_background),
    textColor : Color = Color.Black
) {
    CenterAlignedTopAppBar(
        title = {
            title?.let {
                Text(
                    text = it,
                    style = TextStyles.TopBarTitle.copy(color = textColor)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = backgroundColor),
        navigationIcon = {
            if (hasBackIcon) {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        tint = colorResource(id = R.color.arrow_color),
                        contentDescription = ""
                    )
                }
            }
        },
        actions = {
            if (hasCloseIcon) {
                IconButton(onClick = { onClosePressed() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        tint = colorResource(id = R.color.arrow_color),
                        contentDescription = ""
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun ToolbarPreview() {
    Toolbar(title = "Toolbar", onBackPressed = { }, onClosePressed = {})
}