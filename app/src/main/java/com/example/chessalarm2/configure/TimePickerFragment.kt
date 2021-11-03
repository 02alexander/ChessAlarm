package com.example.chessalarm2.configure

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.DialogFragment
import java.util.*
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import com.example.chessalarm2.database.Alarm

class TimePickerFragment(val viewModel: ConfigureViewModel) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        Log.d("onTimeSet", c.timeInMillis.toString())
        viewModel.alarm.value = viewModel.alarm.value?.copy(time=c.timeInMillis)
    }
}