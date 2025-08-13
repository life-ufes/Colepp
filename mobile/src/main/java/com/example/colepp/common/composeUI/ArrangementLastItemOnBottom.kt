package com.example.colepp.common.composeUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Density

val arrangementLastItemOnBottom =
    object : Arrangement.Vertical {
        override fun Density.arrange(
            totalSize: Int, sizes: IntArray, outPositions: IntArray
        ) {
            var currentOffset = 0
            sizes.forEachIndexed { index, size ->
                if (index == sizes.lastIndex) {
                    outPositions[index] = totalSize - size
                } else {
                    outPositions[index] = currentOffset
                    currentOffset += size
                }
            }
        }
    }