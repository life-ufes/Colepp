package com.example.colepp.common.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun StringBuilder.appendIfNotEmpty(value: String, separator: String = ",") {
    if (this.isNotEmpty()) {
        this.append(separator)
    }
    this.append(value)
}

fun Date.toFormat(pattern: String = "dd/MM/yyyy"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun String.formatToValidFileName(): String {
    val regex = Regex("""[<>:"/\\|?*\u0000-\u001F]""")

    val nomeLimpo = regex.replace(this, "_")

    return nomeLimpo.trim().take(255)
}

fun Long.toCronometerFormat(): String {
    val millis = this % 1000
    val seconds = (this / 1000) % 60
    val minutes = (this / (1000 * 60))

    return String.format(Locale.getDefault(), "%d:%02d.%03d", minutes, seconds, millis)
}

fun String?.orDefault() = this ?: "-"