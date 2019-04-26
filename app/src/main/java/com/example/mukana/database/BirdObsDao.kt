package com.example.mukana.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mukana.model.BirdObservation

/*
* For easily accessing the Room database.
* */

@Dao
interface BirdObsDao {

    @Query("SELECT * FROM bird_obs ORDER BY timeStamp DESC")
    fun getAll(): LiveData<List<BirdObservation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(observation: BirdObservation)

    @Delete
    fun delete(obs: BirdObservation)

} // BirdObsDao
