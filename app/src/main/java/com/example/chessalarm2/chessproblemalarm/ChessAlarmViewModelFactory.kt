package com.example.chessalarm2.chessproblemalarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChessAlarmViewModelFactory (
    private val rating: Int,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChessAlarmViewModel::class.java)) {
            return ChessAlarmViewModel(rating, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}