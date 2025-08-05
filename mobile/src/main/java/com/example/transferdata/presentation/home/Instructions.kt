package com.example.transferdata.presentation.home

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.transferdata.R
import com.example.transferdata.common.utils.InstructionCard

private const val bulletId = "bulletIcon"

@Composable
fun getAllInstructions(): Map<InstructionCard, List<Pair<AnnotatedString, Map<String, InlineTextContent>>>> {
    val instructions = mapOf(
        InstructionCard.InitNewRecord to getNewRecordInstructions(),
        InstructionCard.ConnectToDevice to getConnectDevicesInstructions(),
        InstructionCard.DeleteRecord to getDeleteRecordInstructions(),
        InstructionCard.EditRecord to getEditRecordInstructions(),
        InstructionCard.DownloadRecord to getDownloadRecordInstructions(),
        InstructionCard.ShareRecord to getShareRecordInstructions()
    )

    return instructions
}

@Composable
private fun getNewRecordInstructions(): List<Pair<AnnotatedString, Map<String, InlineTextContent>>> {
    val addId = "addIcon"
    val instructions = listOf(
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.new_record_instructions_1_pt_1))
            appendInlineContent(addId, "[icon]")
            append(
                stringResource(
                    id = R.string.new_record_instructions_1_pt_2,
                    stringResource(id = R.string.home_bottom_bar_new_recording)
                )
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(
                    id = R.string.new_record_instructions_2,
                    stringResource(id = R.string.create_new_recording)
                )
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(
                    id = R.string.new_record_instructions_3,
                    stringResource(id = R.string.btn_create_new_recording),
                    stringResource(id = R.string.new_recording)
                )
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.new_record_instructions_4))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(
                    id = R.string.new_record_instructions_5,
                    stringResource(id = R.string.start_recording)
                )
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(
                    id = R.string.new_record_instructions_6,
                    stringResource(id = R.string.finish_recording)
                )
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(id = R.string.new_record_instructions_7)
            )
        }
    )

    val inlineContent = getInlineContent(
        values = listOf(
            bulletId to R.drawable.ic_circle,
            addId to R.drawable.ic_add
        ),
        size = 16.sp,
        color = Color.Black
    )

    return instructions.map { it to inlineContent }
}

@Composable
private fun getConnectDevicesInstructions(): List<Pair<AnnotatedString, Map<String, InlineTextContent>>> {
    val instructions = listOf(
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.connect_devices_instructions_1))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.connect_devices_instructions_2))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.connect_devices_instructions_3))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.connect_devices_instructions_4))
        }
    )

    val inlineContent = getInlineContent(
        values = listOf(
            bulletId to R.drawable.ic_circle
        ),
        size = 16.sp,
        color = Color.Black
    )

    return instructions.map { it to inlineContent }
}

@Composable
private fun getDeleteRecordInstructions(): List<Pair<AnnotatedString, Map<String, InlineTextContent>>> {
    val deleteId = "deleteIcon"
    val instructions = listOf(
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.delete_record_instructions_1_pt_1))
            appendInlineContent(deleteId, "[icon]")
            append(stringResource(id = R.string.delete_record_instructions_1_pt_2))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.delete_record_instructions_2))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(
                    id = R.string.delete_record_instructions_3,
                    stringResource(id = R.string.home_delete_record_dialog_confirm_button)
                )
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(
                    id = R.string.delete_record_instructions_4,
                    stringResource(id = R.string.home_delete_record_dialog_cancel_button)
                )
            )
        }
    )

    val inlineContent = getInlineContent(
        values = listOf(
            bulletId to R.drawable.ic_circle,
            deleteId to R.drawable.ic_delete
        ),
        size = 16.sp,
        color = Color.Black
    )

    return instructions.map { it to inlineContent }
}

@Composable
private fun getEditRecordInstructions(): List<Pair<AnnotatedString, Map<String, InlineTextContent>>> {
    val editId = "editIcon"
    val instructions = listOf(
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.edit_record_instructions_1_pt_1))
            appendInlineContent(editId, "[icon]")
            append(stringResource(id = R.string.edit_record_instructions_1_pt_2))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(id = R.string.edit_record_instructions_2,
                stringResource(id = R.string.edit_record))
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(
                stringResource(id = R.string.edit_record_instructions_3,
                stringResource(id = R.string.btn_save_record_edited_recording))
            )
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.edit_record_instructions_4))
        }
    )

    val inlineContent = getInlineContent(
        values = listOf(
            bulletId to R.drawable.ic_circle,
            editId to R.drawable.ic_edit
        ),
        size = 16.sp,
        color = Color.Black
    )

    return instructions.map { it to inlineContent }
}

@Composable
private fun getDownloadRecordInstructions(): List<Pair<AnnotatedString, Map<String, InlineTextContent>>> {
    val downloadId = "downloadIcon"
    val instructions = listOf(
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.download_record_instructions_1))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.download_record_instructions_2_pt_1))
            appendInlineContent(downloadId, "[icon]")
            append(stringResource(id = R.string.download_record_instructions_2_pt_2))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.download_record_instructions_3))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.download_record_instructions_4))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.download_record_instructions_5))
        }
    )

    val inlineContent = getInlineContent(
        values = listOf(
            bulletId to R.drawable.ic_circle,
            downloadId to R.drawable.ic_download
        ),
        size = 16.sp,
        color = Color.Black
    )

    return instructions.map { it to inlineContent }
}

@Composable
private fun getShareRecordInstructions(): List<Pair<AnnotatedString, Map<String, InlineTextContent>>> {
    val shareId = "shareIcon"
    val instructions = listOf(
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.share_record_instructions_1))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.share_record_instructions_2_pt_1))
            appendInlineContent(shareId, "[icon]")
            append(stringResource(id = R.string.share_record_instructions_2_pt_2))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.share_record_instructions_3))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.share_record_instructions_4))
        },
        buildAnnotatedString {
            appendInlineContent(bulletId, "[icon]")
            append(stringResource(id = R.string.share_record_instructions_5))
        }
    )

    val inlineContent = getInlineContent(
        values = listOf(
            bulletId to R.drawable.ic_circle,
            shareId to R.drawable.ic_share
        ),
        size = 16.sp,
        color = Color.Black
    )

    return instructions.map { it to inlineContent }
}

private fun getInlineContent(
    values: List<Pair<String, Int>>,
    size: TextUnit,
    color: Color = Color.Gray,
): Map<String, InlineTextContent> {
    return values.associate { (id, iconId) ->
        id to InlineTextContent(
            Placeholder(
                width = size,
                height = size,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = color
            )
        }
    }
}