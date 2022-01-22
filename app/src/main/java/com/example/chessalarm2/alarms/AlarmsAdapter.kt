package com.example.chessalarm2.alarms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chessalarm2.database.alarms.Alarm
import com.example.chessalarm2.databinding.ListItemAlarmBinding

class AlarmsAdapter(val clickListener: AlarmsListener) : RecyclerView.Adapter<AlarmsAdapter.ViewHolder>() {
    var data = listOf<Alarm>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(val binding: ListItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Alarm,
            clickListener: AlarmsListener
        ) {
            binding.clickListener = clickListener
            binding.enableAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
                clickListener.onToggle(item, isChecked)
            }
            binding.alarm = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAlarmBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class AlarmsListener(val clickListener: (alarmId: Long) -> Unit,
                     val onDeleteListener: (alarmId: Long) -> Unit,
                     val toggleListener : (alarmId: Long, isCheched: Boolean) -> Unit) {
    fun onClick(alarm: Alarm) = clickListener(alarm.alarmId)
    fun onClickDelete(alarm: Alarm) = onDeleteListener(alarm.alarmId)
    fun onToggle(alarm: Alarm, isChecked: Boolean) = toggleListener(alarm.alarmId, isChecked)
}