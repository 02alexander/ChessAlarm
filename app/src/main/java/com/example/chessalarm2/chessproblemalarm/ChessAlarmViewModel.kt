package com.example.chessalarm2.chessproblemalarm

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel

class ChessAlarmViewModel(rating: Int, application: Application) : AndroidViewModel(application){
    private val mp = MediaPlayer()
    private lateinit var solution: List<Pair<Coordinate, Coordinate>>

    init {

    }
}