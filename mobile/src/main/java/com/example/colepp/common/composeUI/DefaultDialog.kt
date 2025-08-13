package com.example.colepp.common.composeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.colepp.R
import com.example.colepp.common.utils.Size
import com.example.colepp.common.utils.TextStyles

@Composable
fun DefaultDialog(
    modifier: Modifier = Modifier,
    image: Painter?,
    imageDescription: String? = null,
    title: String,
    message: String? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showCloseButton: Boolean = false,
    textAlign: TextAlign = TextAlign.Center,
    primaryButton: @Composable () -> Unit,
    secondaryButton: @Composable () -> Unit,
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(7.dp))
                .padding(Size.size05),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showCloseButton) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            tint = colorResource(id = R.color.gray_50),
                            contentDescription = null
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                image?.let {
                    Image(
                        modifier = Modifier
                            .padding(Size.size03),
                        painter = image,
                        contentDescription = imageDescription
                    )
                    Spacer(modifier.padding(top = Size.size07))
                }
                Text(
                    text = title,
                    style = TextStyles.TextSBoldWithoutColor,
                    textAlign = textAlign
                )
                message?.let {
                    Spacer(modifier.padding(top = Size.size03))
                    Text(
                        text = message,
                        style = TextStyles.TextS,
                        textAlign = textAlign,
                    )
                }
            }
            Spacer(modifier.padding(top = Size.size07))
            primaryButton()
            Spacer(modifier.padding(top = Size.size04))
            secondaryButton()
        }
    }
}

@Preview
@Composable
fun DefaultWarningDialogPreview() {
    DefaultDialog(
        image = painterResource(id = R.drawable.ic_close),
        title = "Title",
        message = "Message",
        primaryButton = {
            DefaultButton(
                buttonStyle = ButtonStyle.outlinedButton(),
                text = "Button 01",
                onClick = {}
            )
        },
        secondaryButton = {
            Text(
                text = "Button 02",
                color = colorResource(id = R.color.gray_50)
            )
        }
    )
}