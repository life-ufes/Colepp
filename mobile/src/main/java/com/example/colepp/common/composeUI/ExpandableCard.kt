package com.example.colepp.common.composeUI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.colepp.R
import com.example.colepp.common.utils.Size

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    toggleCard: () -> Unit,
    header: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(
                color = colorResource(id = R.color.primary_color_light),
                shape = RoundedCornerShape(Size.size03)
            )
            .padding(Size.size05),
        verticalArrangement = Arrangement.spacedBy(Size.size03)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleCard() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            header()
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(
                    if (expanded) R.drawable.ic_arrow_up
                    else R.drawable.ic_arrow_down
                ),
                contentDescription = null,
                tint = colorResource(id = R.color.gray_40),
                modifier = Modifier.size(Size.size07)
            )
        }
        AnimatedVisibility(expanded) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropDownCardPreview() {
    ExpandableCard(
        expanded = true,
        toggleCard = { },
        header = {
            Text(
                text = "Header"
            )
        },
        content = {
            Column {
                Text("Item 1")
                Text("Item 2")
                Text("Item 3")
            }
        },
    )
}