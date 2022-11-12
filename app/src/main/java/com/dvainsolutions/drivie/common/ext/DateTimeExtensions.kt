package com.dvainsolutions.drivie.common.ext

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toDateString(): String {
    return String.format(
        "%d. %d. %d.",
        this.get(Calendar.YEAR),
        this.get(Calendar.MONTH),
        this.get(Calendar.DAY_OF_MONTH)
    )
}

fun String.toDate(formatPattern: String = "yyyy.MM.dd"): Date? {
    return if (this.isNotBlank()) SimpleDateFormat(formatPattern, Locale.getDefault()).parse(
        this
    ) else null
}

fun Date.toFormattedString(formatPattern: String = "yyyy.MM.dd"): String? {
    val formatter = SimpleDateFormat(formatPattern, Locale.getDefault())
    return formatter.format(this)
}

fun Long.toDateTimeString(formatPattern: String = "MMM dd, HH:mm"): String {
    val date = Date(this)
    val format = SimpleDateFormat(formatPattern, Locale.getDefault())
    return format.format(date)
}