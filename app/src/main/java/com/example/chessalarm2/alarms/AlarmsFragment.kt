package com.example.chessalarm2.alarms

import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chessalarm2.R
import com.example.chessalarm2.Scheduler
import com.example.chessalarm2.database.alarms.AlarmsDatabase
import com.example.chessalarm2.databinding.FragmentAlarmsBinding
import kotlinx.coroutines.launch

class AlarmsFragment : Fragment() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentAlarmsBinding>(inflater,
            R.layout.fragment_alarms,container,false)

        val application = requireNotNull(this.activity).application
        val database = AlarmsDatabase.getInstance(application).alarmsDatabaseDao
        val viewModelFactor = AlarmsViewModelFactory(database, application)
        val alarmsViewModel = ViewModelProvider(this, viewModelFactor).get(AlarmsViewModel::class.java)

        val scheduler = Scheduler(this.requireContext())
        val adapter = AlarmsAdapter(AlarmsListener({ alarmId ->
            alarmsViewModel.onAlarmClicked(alarmId)
        }, { alarmId ->
            alarmsViewModel.onDeleteAlarm(alarmId)
        }, { alarmId, isChecked ->
            lifecycleScope.launch {
                val alarm = database.get(alarmId)!!
                val isChanged = (alarm.isEnabled xor isChecked)
                alarm.isEnabled = isChecked
                if (isChanged && isChecked) {
                    scheduler.enableAlarm(alarm)
                } else if (isChanged && !isChecked) {
                    scheduler.disableAlarm(alarm)
                }
                database.update(alarm)
            }
        }))
        binding.alarmsList.adapter = adapter

        alarmsViewModel.alarms.observe(this, {
            it?.let {
                adapter.data = it
            }
        })

        alarmsViewModel.navigateUpListener.observe(viewLifecycleOwner, Observer { alarmId ->
            alarmId?.let {
                this.findNavController().navigate(
                    AlarmsFragmentDirections.actionAlarmsFragmentToConfigureFragment(alarmId))
                alarmsViewModel.onConfigureNavigated()
            }
        })

        binding.addAlarmButton.setOnClickListener {
            alarmsViewModel.onAddAlarm()
        }


        return binding.root
    }

}
