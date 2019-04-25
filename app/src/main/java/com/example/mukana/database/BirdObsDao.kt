package com.example.mukana.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mukana.model.BirdObservation

@Dao
interface BirdObsDao {

    @Query("SELECT * FROM bird_obs ORDER BY timeStamp DESC")
    fun getAll(): LiveData<List<BirdObservation>>

    /*
    @Query("SELECT * FROM birdobservation WHERE timeStamp IN (:timeStamps)")
    fun loadAllByTimeStamps(timeStamps: LongArray): List<BirdObservation> */
/*
    @Query("SELECT * FROM birdobservation WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User */

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(observation: BirdObservation)

    @Delete
    fun delete(obs: BirdObservation)

} // BirdObsDao
