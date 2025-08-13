package com.example.colepp.common.composeUI

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.colepp.R
import com.example.colepp.common.utils.Size

data class ButtonStyle(
    val icon: Painter? = null,
    val backgroundId: Color,
    val disableColorId: Color,
    val hasBorder: Boolean = false,
    val paddingValues: PaddingValues,
    val colorBorder: Color,
    val shapeBorder: RoundedCornerShape,
    val colorText: Color,
    val colorBorderIfDisable: Int = R.color.gray_50
) {
    companion object {
        @Composable
        fun primaryButton() = ButtonStyle(
            backgroundId = colorResource(R.color.primary_color),
            disableColorId = colorResource(R.color.gray_50),
            hasBorder = false,
            paddingValues = PaddingValues(Size.size05),
            shapeBorder = RoundedCornerShape(10.dp),
            colorBorder = colorResource(R.color.primary_color),
            colorText = colorResource(R.color.white)
        )

        @Composable
        fun outlinedButton() = ButtonStyle(
            backgroundId = colorResource(R.color.white),
            disableColorId = colorResource(R.color.gray_50),
            hasBorder = true,
            paddingValues = PaddingValues(Size.size05),
            shapeBorder = RoundedCornerShape(10.dp),
            colorBorder = colorResource(R.color.red_dark),
            colorText = colorResource(R.color.red_dark),
            colorBorderIfDisable = R.color.gray_50
        )

        @Composable
        fun tertiaryButton() = ButtonStyle(
            backgroundId = colorResource(R.color.white),
            disableColorId = colorResource(R.color.gray_50),
            hasBorder = false,
            paddingValues = PaddingValues(Size.size05),
            shapeBorder = RoundedCornerShape(10.dp),
            colorBorder = colorResource(R.color.primary_color),
            colorText = colorResource(R.color.primary_color)
        )
    }
}