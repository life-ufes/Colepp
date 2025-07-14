package com.example.transferdata.common.composeUI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.transferdata.R
import com.example.transferdata.common.utils.Size
import com.example.transferdata.common.utils.TextStyles

@Composable
fun CardOfWearable(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .background(
                color = colorResource(id = R.color.primary_color_light),
                shape = RoundedCornerShape(Size.size03)
            )
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.gray_50),
                shape = RoundedCornerShape(Size.size03)
            )
            .padding(Size.size05),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = TextStyles.TitleS
        )
        Text(
            text = subtitle,
            style = TextStyles.TextS
        )
    }
}

@Preview
@Composable
private fun CardOfWearablePreview() {
    CardOfWearable(
        title = "Polar H10",
        subtitle = "Connected",
        onClick = {},
        modifier = Modifier.fillMaxWidth()
    )
}