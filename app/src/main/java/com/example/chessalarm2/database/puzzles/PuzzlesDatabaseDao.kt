package com.example.chessalarm2.database.puzzles

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PuzzlesDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(puzzle: Puzzle)

    @Update
    suspend fun update(puzzle: Puzzle)

    @Query("SELECT * from puzzles_table WHERE id = :key")
    suspend fun get(key: Long): Puzzle?

    @Query("DELETE FROM puzzles_table")
    suspend fun clear()

    @Query("SELECT * FROM puzzles_table WHERE rating < :rating ORDER BY rating DESC")
    fun getEligiblePuzzles(rating: Int): LiveData<List<Puzzle>>

    @Query("SELECT COUNT(*)")
    suspend fun count(): Long
}