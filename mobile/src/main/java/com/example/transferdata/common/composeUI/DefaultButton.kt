package com.example.transferdata.common.composeUI

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.transferdata.R
import com.example.transferdata.common.utils.Size.size03
import com.example.transferdata.common.utils.Size.size05
import com.example.transferdata.common.utils.TextStyles

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    buttonStyle: ButtonStyle = ButtonStyle.primaryButton(),
    text: String,
    icon: Painter? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyles.TextSBoldWithoutColor
) {
    Button(
        onClick = { onClick() },
        enabled = enabled,
        modifier = modifier
            .border(
                width = if (buttonStyle.hasBorder) 1.dp else (-1).dp,
                color = if (enabled) buttonStyle.colorBorder else colorResource(buttonStyle.colorBorderIfDisable) ,
                shape = buttonStyle.shapeBorder
            ),
        shape = buttonStyle.shapeBorder,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonStyle.backgroundId,
            disabledContainerColor = colorResource(id = R.color.gray_20)
        ),
        contentPadding = buttonStyle.paddingValues
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    tint = if (enabled) buttonStyle.colorText else colorResource(id = R.color.gray_50),
                    modifier = Modifier.padding(end = 8.dp).size(size05)
                )
                Spacer(Modifier.padding(start = size03))
            }
            Text(
                text = text,
                style = textStyle,
                color = if (enabled) buttonStyle.colorText else colorResource(id = R.color.gray_50),
            )
        }
    }
}

@Preview
@Composable
fun ButtonPrimaryPreview() {
    DefaultButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Button",
        buttonStyle = ButtonStyle.primaryButton(),
        onClick = { })
}

@Preview
@Composable
fun ButtonOutlinedPreview() {
    DefaultButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Button",
        buttonStyle = ButtonStyle.outlinedButton(),
        onClick = { })
}

@Preview
@Composable
fun ButtonTertiaryPreview() {
    DefaultButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Button",
        buttonStyle = ButtonStyle.tertiaryButton(),
        onClick = { }
    )
}

@Preview
@Composable
fun ButtonPrimaryPreviewDisabled() {
    DefaultButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Button",
        buttonStyle = ButtonStyle.primaryButton(),
        onClick = { },
        enabled = false
    )
}

@Preview
@Composable
fun ButtonOutlinedPreviewDisabled() {
    DefaultButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Button",
        buttonStyle = ButtonStyle.outlinedButton(),
        onClick = { },
        enabled = false
    )
}