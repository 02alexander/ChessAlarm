package com.example.chessalarm2.alarms

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.chessalarm2.database.alarms.Alarm
import com.example.chessalarm2.daysToString
import java.text.SimpleDateFormat
import java.util.*

class BindingUtils {}

@BindingAdapter("alarmRatingString")
fun TextView.setRatingString(item: Alarm) {
    text = item.rating.toString()
}

@BindingAdapter("alarmTimeString")
fun TextView.setTimeString(item: Alarm) {
    val date = Date(item.time)
    val format = SimpleDateFormat("HH:mm")
    text = format.format(date)
}

@BindingAdapter("alarmDaysString")
fun TextView.setDaysString(item: Alarm) {
    text = daysToString(item.days)
}