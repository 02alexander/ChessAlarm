package com.example.chessalarm2

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

fun daysToString(days: List<Int>) : String {
    var res = ""
    if (days.isEmpty()) {
        res = "No days"
    } else {
        for (day in days) {
            res += when (day) {
                0 -> "mon "
                1 -> "tue "
                2 -> "wed "
                3 -> "thu "
                4 -> "fri "
                5 -> "sat "
                else -> "sun "
            }
        }
    }
    return res
}