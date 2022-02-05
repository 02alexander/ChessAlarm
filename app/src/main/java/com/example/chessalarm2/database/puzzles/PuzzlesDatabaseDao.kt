package com.example.chessalarm2.database.puzzles

import androidx.room.*

@Dao
interface PuzzlesDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(puzzle: Puzzle)

    @Update
    suspend fun update(puzzle: Puzzle)

    @Query("SELECT * from puzzles_table WHERE id = :key")
    suspend fun get(key: Long): Puzzle?

    @Query("DELETE FROM puzzles_table")
    suspend fun clear()

    @Query("SELECT * FROM puzzles_table WHERE rating < :rating AND NOT beenPlayed ORDER BY rating DESC LIMIT 3")
    suspend fun getEligiblePuzzles(rating: Int): List<Puzzle>

    @Query("UPDATE puzzles_table SET beenPlayed=0")
    suspend fun resetBeenPlayed()

    @Query("SELECT COUNT(*) FROM puzzles_table")
    suspend fun count(): Long
}