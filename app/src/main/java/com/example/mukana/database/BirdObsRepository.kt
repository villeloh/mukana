package com.example.mukana.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.mukana.log
import com.example.mukana.model.BirdObservation

// it's needed as a wrapper class for safe database operations.
// if the app were to be expanded, it would serve abstraction
// purposes as well (switching out the whole backend if needed).
class BirdObsRepository(private val birdObsDao: BirdObsDao) {

    val birdObsList: LiveData<List<BirdObservation>> = birdObsDao.getAll()

    @WorkerThread
    suspend fun insert(item: BirdObservation) {

        log("in repo insert: " + item)
        birdObsDao.insert(item)
    }
}