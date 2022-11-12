package com.dvainsolutions.drivie.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dvainsolutions.drivie.R

val poppinsFont = FontFamily(
    Font(R.font.poppins_light, weight = FontWeight.Light),
    Font(R.font.poppins_regular, weight = FontWeight.Normal),
    Font(R.font.poppins_medium, weight = FontWeight.Medium),
    Font(R.font.poppins_bold, weight = FontWeight.Bold),
)


val Typography = Typography(
    body1 = TextStyle(
        fontFamily = poppinsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = poppinsFont,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        letterSpacing = 1.5.sp
    ),
    h2 = TextStyle(
        fontFamily = poppinsFont,
        fontSize = 32.sp,
        fontWeight = FontWeight.W500
    ),
    h6 = TextStyle(
        fontFamily = poppinsFont,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
    button = TextStyle(
        fontFamily = poppinsFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    )
)