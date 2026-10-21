package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SecretaryDocument
import kotlinx.coroutines.flow.Flow

@Dao
interface SecretaryDocumentDao {
    @Query("SELECT * FROM secretary_documents ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SecretaryDocument>>

    @Query("SELECT * FROM secretary_documents WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): SecretaryDocument?

    @Query("SELECT * FROM secretary_documents WHERE docType = :docType ORDER BY createdAt DESC")
    fun getByType(docType: String): Flow<List<SecretaryDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: SecretaryDocument): Long

    @Query("DELETE FROM secretary_documents WHERE id = :id")
    suspend fun deleteById(id: Int)
}
