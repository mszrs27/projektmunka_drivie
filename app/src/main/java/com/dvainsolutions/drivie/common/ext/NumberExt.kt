package com.dvainsolutions.drivie.common.ext

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun stringWithCommaToFloatNumber(input: String): Float? {
    if (input.isBlank()) return 0.0f
    val symbols = DecimalFormatSymbols()
    symbols.decimalSeparator = ','
    val format = DecimalFormat("##,###")
    format.decimalFormatSymbols = symbols
    return format.parse(input)?.toFloat()
}

fun stringWithWhitespaceToIntNumber(input: String): Int? {
    return input.filter { !it.isWhitespace() }.toIntOrNull()
}