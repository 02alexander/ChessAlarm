package com.example.chessalarm2.configure

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.chessalarm2.R
import java.lang.IllegalStateException

class SelectDaysDialog(val original_days : List<Int>) : DialogFragment() {
    lateinit var checked_days: BooleanArray
    val days: List<Int> get() {
        val res = mutableListOf<Int>()
        for (i in 0..checked_days.size) {
            res.add(i)
        }
        return res.toList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        checked_days = BooleanArray(7)
        for (i in 0..checked_days.size) {
            checked_days[i] = i in original_days
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMultiChoiceItems(R.array.days_of_the_week, checked_days, { dialog, which, isChecked ->

            })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}