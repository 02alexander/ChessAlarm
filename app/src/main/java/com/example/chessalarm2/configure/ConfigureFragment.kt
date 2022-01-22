package com.example.chessalarm2.configure

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chessalarm2.R
import com.example.chessalarm2.Sound
import com.example.chessalarm2.database.alarms.Alarm
import com.example.chessalarm2.database.alarms.AlarmsDatabase
import com.example.chessalarm2.database.alarms.AlarmsDatabaseDao
import com.example.chessalarm2.databinding.FragmentConfigureBinding
import com.example.chessalarm2.daysToString
import com.example.chessalarm2.getAlarmSounds
import java.text.SimpleDateFormat
import java.util.*

class ConfigureFragment : Fragment() {
    lateinit var database: AlarmsDatabaseDao
    lateinit var configureViewModel: ConfigureViewModel
    lateinit var binding: FragmentConfigureBinding
    lateinit var sounds: List<Sound>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        Log.d("configure_fragment", "onPause() called")
        configureViewModel.saveAlarm()
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

        sounds = getAlarmSounds(this.requireContext())

        configureViewModel.alarm.observe(this.viewLifecycleOwner, {
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
        binding.editRating.doOnTextChanged { text, start, before, count ->
            Log.d("configure_fragment", "rating edited, text=$text, start=$start, before=$before, count=$count")

            try {
                val rating = text.toString().toInt()
                configureViewModel.alarm.value = configureViewModel.alarm.value?.copy(rating = rating)
                Log.d("configure_fragment", "new alarm = ${configureViewModel.alarm.value}")
            } catch (e: NumberFormatException) {
                val toast = Toast.makeText(application.applicationContext, "Invalid rating input. Must be an integer.", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        populateAudioSpinner()
        return binding.root
    }

    private fun populateAudioSpinner() {
        val titles = mutableListOf<String>()
        for (sound in sounds) {titles.add(sound.title)}

        val adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_list_item_1, titles)
        binding.spinnerAudio.adapter = adapter
        binding.spinnerAudio.onItemSelectedListener = AudioSpinnerListener(configureViewModel, sounds)
    }

    private fun showDaysDialog() {
        val checked_days = BooleanArray(7) {
            it in configureViewModel.alarm.value!!.days
        }
        val builder = AlertDialog.Builder(this.context)
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
        Log.d("ConfigureFragment updateView", "alarm.time=${alarm.time} text=${format.format(date)}")
        if (binding.editTime.text != format.format(date).toString()) {
            binding.editTime.text = format.format(date).toString()
        }
        if (binding.editRating.text.toString() != alarm.rating.toString()) {
            binding.editRating.setText(alarm.rating.toString())
        }
        if (binding.editDays.text != daysToString(alarm.days)) {
            binding.editDays.text = daysToString(alarm.days)
        }
        for (i in sounds.indices) {
            if (alarm.audioId == sounds[i].id) {
                binding.spinnerAudio.setSelection(i)
            }
        }
    }
}

private class AudioSpinnerListener(val configureViewModel: ConfigureViewModel, val sounds: List<Sound>) : OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val sound = sounds[position]
        configureViewModel.alarm.value?.let {
            it.audioId = sound.id
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

}
