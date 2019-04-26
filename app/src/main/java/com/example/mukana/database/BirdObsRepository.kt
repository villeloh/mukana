package com.example.mukana.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.mukana.log
import com.example.mukana.model.BirdObservation

// it's needed as a wrapper class for safe database operations.
// if the app were to be expanded, it would serve abstraction
// purposes as well (switching out the whole backend if needed).
class BirdObsRepository(private val birdObsDao: BirdObsDao) {

    // auto-updates with new db entries... even though it's an immutable property
    // and this is a one-time assignment. the logic could certainly be clearer here.
    val birdObsList: LiveData<List<BirdObservation>> = birdObsDao.getAll()

    // not sure why it doesn't seem to use the suspend modifier
    @WorkerThread
    suspend fun insert(item: BirdObservation) {

        birdObsDao.insert(item)
    }
}