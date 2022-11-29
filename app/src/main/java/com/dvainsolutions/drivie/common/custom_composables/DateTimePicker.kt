package com.dvainsolutions.drivie.common.custom_composables

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import java.util.*

@Composable
fun DateTimePicker(time: String?, onTimeChange: (time: Calendar) -> Unit,  label: @Composable() (() -> Unit)? = null,  placeholderRes: Int? = null, placeholderColor: Color = Color.Gray.copy(alpha = 0.7f)) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.time = Date()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            calendar.set(mYear, mMonth + 1, mDayOfMonth)
            onTimeChange.invoke(calendar)
        }, year, month, day
    )

    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        placeholder = { if (placeholderRes != null) Text(text = stringResource(id = placeholderRes), color = placeholderColor) },
        label = label,
        text = time,
        enabled = false,
        onClickableFunction = {
            datePickerDialog.show()
        }
    )
}