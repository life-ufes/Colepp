package com.example.colepp.common.utils

import android.graphics.Typeface
import android.text.Layout
import android.text.TextUtils
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import com.example.colepp.R
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.text.DecimalFormat

val yDecimalFormat = DecimalFormat("#' bpm'")
val xDecimalFormat = DecimalFormat("#'s'")
val startAxisValueFormatter = CartesianValueFormatter.decimal(yDecimalFormat)
val bottomAxisValueFormatter = CartesianValueFormatter.decimal(xDecimalFormat)
val markerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(yDecimalFormat)
val polarLineColorId = R.color.heart_rate_polar_color
val smartwatchLineColorId = R.color.heart_rate_smartwatch_color
val columnColors = listOf(polarLineColorId, smartwatchLineColorId)
val legendItemLabelComponent = TextComponent(
    Color.Black.toArgb(),
    Typeface.DEFAULT,
    12f.sp.value,
    Layout.Alignment.ALIGN_NORMAL,
    null,
    1,
    TextUtils.TruncateAt.END,
    Insets.Zero,
    Insets.Zero,
    null,
    TextComponent.MinWidth.fixed(),
)