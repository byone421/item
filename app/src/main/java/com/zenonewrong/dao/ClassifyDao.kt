package com.zenonewrong.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zenonewrong.entity.Classify
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassifyDao {


//    @Query("SELECT * FROM classify ORDER BY sort,create_time DESC")
//    suspend fun getAllClassifies(): List<Classify>

    @Query("SELECT * FROM classify ORDER BY sort,create_time DESC")
    fun getAllClassifies():  Flow<List<Classify>>

    @Query("SELECT * FROM classify WHERE show_on_home = 1 ORDER BY sort,create_time DESC")
    fun getHomeClassifies(): Flow<List<Classify>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(classify: Classify): Long

    @Delete
    suspend fun deleteData(classify: Classify)

    @Update
    suspend fun updateData(classify: Classify)

    @Query("SELECT * FROM classify ORDER BY sort,create_time DESC")
    suspend fun getAllClassifiesExcel(): List<Classify>

    @Query("DELETE FROM classify")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM classify  WHERE name = :classifyName")
    suspend fun getCountByClassifyName(classifyName: String): Int

}