package com.example.chessalarm2.database.puzzles

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puzzles_table")
data class Puzzle(

    @PrimaryKey
    val id: Long,

    @ColumnInfo
    val FEN: String,

    @ColumnInfo
    val moves: String, // UCI of solution

    @ColumnInfo
    val rating: Int,

    @ColumnInfo
    val deviation: Int,

    @ColumnInfo
    val popularity: Int,

    @ColumnInfo
    val nbplays: Int,

    @ColumnInfo
    val beenPlayed: Boolean,

    )