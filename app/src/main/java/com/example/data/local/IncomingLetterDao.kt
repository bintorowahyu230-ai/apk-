package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.IncomingLetter
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomingLetterDao {
    @Query("SELECT * FROM incoming_letters ORDER BY id DESC")
    fun getAll(): Flow<List<IncomingLetter>>

    @Query("SELECT COUNT(*) FROM incoming_letters")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(letter: IncomingLetter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(letters: List<IncomingLetter>)

    @Query("DELETE FROM incoming_letters WHERE id = :id")
    suspend fun deleteById(id: Int)
}
