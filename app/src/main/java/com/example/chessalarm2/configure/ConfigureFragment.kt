package com.example.chessalarm2.configure

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chessalarm2.MainActivity
import com.example.chessalarm2.R
import com.example.chessalarm2.database.Alarm
import com.example.chessalarm2.database.AlarmsDatabase
import com.example.chessalarm2.database.AlarmsDatabaseDao
import com.example.chessalarm2.databinding.FragmentConfigureBinding
import com.example.chessalarm2.daysToString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.Contract
import java.text.SimpleDateFormat
import java.util.*

class ConfigureFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var database: AlarmsDatabaseDao
    lateinit var configureViewModel: ConfigureViewModel
    lateinit var binding: FragmentConfigureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        configureViewModel.alarm.value?.let {
            Log.d("Spinner", position.toString())
            it.difficulty = position
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            Log.d("SaveButton", "pressed")
            //configureViewModel.alarm.value!!.difficulty =
            configureViewModel.saveAlarm()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        val args = ConfigureFragmentArgs.fromBundle(requireArguments())
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentConfigureBinding>(inflater, R.layout.fragment_configure, container, false)
        val application = requireNotNull(this.activity).application
        database = AlarmsDatabase.getInstance(application).alarmsDatabaseDao
        val viewModelFactory = ConfigureViewModelFactory(database, args.alarmId, application)
        configureViewModel = ViewModelProvider(this, viewModelFactory).get(ConfigureViewModel::class.java)

        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.difficulties_array,
            android.R.layout.simple_spinner_item
        ).also {  adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDifficulty.adapter = adapter
        }
        binding.spinnerDifficulty.onItemSelectedListener = this

        configureViewModel.alarm.observe(this, {
            it?.let {
                Log.d("onCreateView()", "configureViewModel changed")
                updateView(it)
            }
        })

        val timepicker = TimePickerFragment(configureViewModel)
        binding.editTime.setOnClickListener {
            timepicker.show(parentFragmentManager, "timePicker")
        }

        binding.editDays.setOnClickListener {
            showDaysDialog()
        }

        return binding.root
    }

    private fun showDaysDialog() {
        val checked_days = BooleanArray(7) {
            it in configureViewModel.alarm.value!!.days
        }
        var builder = AlertDialog.Builder(this.context)
        builder.setTitle("Choose days.")
        builder.setMultiChoiceItems(R.array.days_of_the_week, checked_days, { dialog, which, isChecked ->
            checked_days[which] = isChecked
        })
        builder.setPositiveButton("OK") { _, _ ->
            val temp_days = mutableListOf<Int>()
            for (i in 0..checked_days.size-1) {
                if (checked_days[i]) {
                    temp_days.add(i)
                }
            }
            configureViewModel.alarm.value = configureViewModel.alarm.value?.copy(days=temp_days)
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
    }

    fun updateView(alarm: Alarm) {
        val date = Date(alarm.time)
        val format = SimpleDateFormat("HH:mm")
        binding.editTime.setText(format.format(date))
        binding.spinnerDifficulty.setSelection(alarm.difficulty)
        binding.editDays.text = daysToString(alarm.days)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }


}