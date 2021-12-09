package com.example.chessalarm2.configure

import android.view.View
import android.widget.AdapterView

class SpinnerListener(val viewModel: ConfigureViewModel) : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.alarm.value?.difficulty = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}