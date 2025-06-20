package com.example.transferdata.common.utils

fun StringBuilder.appendIfNotEmpty(value: String, separator: String = ",") {
    if (this.isNotEmpty()) {
        this.append(separator)
    }
    this.append(value)
}