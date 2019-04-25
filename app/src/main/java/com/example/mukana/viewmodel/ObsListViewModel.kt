package com.example.mukana.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mukana.database.BirdObsDatabase
import com.example.mukana.database.BirdObsRepository
import com.example.mukana.log
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.BirdObservationList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ObsListViewModel(private val initialState: BirdObservationList) : MvRxViewModel<BirdObservationList>(initialState) {

    // coroutine stuffs that are needed to safely access the database...
    // (side note: my thesis could concern a database that's actually usable without a freakin' PhD)
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private lateinit var repository: BirdObsRepository
    lateinit var birdObsList: LiveData<List<BirdObservation>> // items added to the database auto-update this

    fun initDatabase(appContext: Context) {

        // we need to give it the application context (which is inaccessible in an MvRxViewModel class)
        // repository = BirdObsRepository(BirdObsDatabase.getDatabase(appContext).birdObsDao())
        val birdObsDao = BirdObsDatabase.getDatabase(appContext).birdObsDao()
        repository = BirdObsRepository(birdObsDao)
        birdObsList = repository.birdObsList
    }

    // this has to be done here and not in BirdObservationList itself, since the underlying list object is immutable.
    private fun sortByTimeStamp() {

        withState {

            if (it.items.isEmpty() || it.items.size == 1) return@withState

            setState { copy(items = items.sortedByDescending { item -> item.timeStamp }) } // newest first
        }
    } // sortByTimeStamp

    // https://github.com/airbnb/MvRx/wiki#updating-state
    fun addItems(newItems: List<BirdObservation>) {

        // log("adding item list: " + newItems.toString())
        setState { copy(items = items + newItems) }
    }

    fun updateFromDb(list: List<BirdObservation>) {

        setState { copy(items = list) }
        // sortByTimeStamp() // since it's not certain if they're in the correct order coming from the db
    }

    private fun insertIntoDb(birdObs: BirdObservation) = scope.launch(Dispatchers.IO) {

        repository.insert(birdObs)
    }

  /*  private fun insertIntoDb(birdObs: BirdObservation) {

        birdObsDao.insert(birdObs)
    } */

    fun addItem(item: BirdObservation) {

        withState {
            val newList = it.items.toMutableList()
            newList.add(0, item) // add it to the start of the list
            setState { copy(items = newList) }
            insertIntoDb(item)
        }
    } // addItem

    // for clearing all list items. (not used atm)
    fun resetList() {

        setState { copy(items = emptyList()) }
    }

    fun list(): BirdObservationList {
        var list = BirdObservationList()
        withState {
            list = it
        }
        return list
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

} // ObsListViewModel