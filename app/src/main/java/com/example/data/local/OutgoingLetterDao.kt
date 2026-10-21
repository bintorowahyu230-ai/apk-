package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.OutgoingLetter
import kotlinx.coroutines.flow.Flow

@Dao
interface OutgoingLetterDao {
    @Query("SELECT * FROM outgoing_letters ORDER BY id DESC")
    fun getAll(): Flow<List<OutgoingLetter>>

    @Query("SELECT COUNT(*) FROM outgoing_letters")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(letter: OutgoingLetter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(letters: List<OutgoingLetter>)

    @Query("DELETE FROM outgoing_letters WHERE id = :id")
    suspend fun deleteById(id: Int)
}
