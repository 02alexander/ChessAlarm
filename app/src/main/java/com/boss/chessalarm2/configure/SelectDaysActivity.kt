package com.boss.chessalarm2.configure

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.boss.chessalarm2.R
import com.boss.chessalarm2.databinding.ActivitySelectDaysBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SelectDaysActivity : AppCompatActivity() {
    var days = MutableLiveData<List<Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySelectDaysBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_days)

        val dm = resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        window.setLayout((width*0.5).toInt(), (height*0.7).toInt())

        intent.extras.let {
            it?.getString("days")?.let {
                days.value = Gson().fromJson<List<Int>>(it, object : TypeToken<List<Int>>() {}.type)
            }
        }

        binding.checkboxMonday.setOnClickListener {
            updateDays(0, binding.checkboxMonday.isChecked)
        }
        binding.checkboxTuesday.setOnClickListener {
            updateDays(1, binding.checkboxTuesday.isChecked)
        }
        binding.checkboxWednesday.setOnClickListener {
            updateDays(2, binding.checkboxWednesday.isChecked)
        }
        binding.checkboxThursday.setOnClickListener {
            updateDays(3, binding.checkboxThursday.isChecked)
        }
        binding.checkboxFriday.setOnClickListener {
            updateDays(4, binding.checkboxFriday.isChecked)
        }
        binding.checkboxSaturday.setOnClickListener {
            updateDays(5, binding.checkboxSaturday.isChecked)
        }
        binding.checkboxSunday.setOnClickListener {
            updateDays(6, binding.checkboxSunday.isChecked)
        }

        days.observe(this, {
            it?.let {
                val json = Gson().toJson(it)
                val intent = Intent()
                intent.putExtra("days", json)
                setResult(Activity.RESULT_OK, intent)

            }
        })

    }

    private fun updateDays(day: Int, should_exist: Boolean) {
        var lst = days.value!!.toMutableList()
        if (should_exist && day !in lst) {
            lst.add(day)
        }
        if (!should_exist) {
            lst.remove(day)
        }
        days.value = lst
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun update() {

    }
}
