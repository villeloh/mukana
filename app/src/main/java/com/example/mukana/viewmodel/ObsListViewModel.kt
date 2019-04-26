package com.example.mukana.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mukana.database.BirdObsDatabase
import com.example.mukana.database.BirdObsRepository
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.BirdObservationList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/*
* For keeping track of the observation list state during the lifetime of the app
* (MvRxViewModels are only destroyed with the application process itself).
* */

class ObsListViewModel(private val initialState: BirdObservationList) : MvRxViewModel<BirdObservationList>(initialState) {

    // coroutine stuffs that are needed to safely access the database...
    // (side note: my thesis could concern a database that's actually usable without having a PhD)
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private lateinit var repository: BirdObsRepository
    lateinit var databaseBirdObsList: LiveData<List<BirdObservation>> // items added to the database auto-update this

    fun initDatabase(appContext: Context) {

        // we need to give it the application context, which is inaccessible in an MvRxViewModel class.
        // hence it needs to be passed in from outside. this, I've heard, is a Bad Thing(tm), but it
        // can't be helped here.
        val birdObsDao = BirdObsDatabase.getDatabase(appContext).birdObsDao()
        repository = BirdObsRepository(birdObsDao)
        databaseBirdObsList = repository.birdObsList
    } // initDatabase

    // https://github.com/airbnb/MvRx/wiki#updating-state
    fun replaceViewModelList(list: List<BirdObservation>) {

        setState { copy(items = list) }
    }

    fun addItem(item: BirdObservation) {

        withState {
            val newList = it.items.toMutableList()
            newList.add(0, item) // add it to the start of the list
            setState { copy(items = newList) }

            // without this call, the updates to the list view simply stop working.
            // ideally, the database would only be used to persist data on app closure
            // (or a long pause), using the (indestructible MvRx)ViewModel to keep state while the app is running.
            // i'm running out of time to solve this issue, so I'm just going to save every item to the db here.
            insertIntoDb(item)
        }
    } // addItem

    private fun insertIntoDb(birdObs: BirdObservation) = scope.launch(Dispatchers.IO) {

        repository.insert(birdObs)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
        // saveAllDataToDb() // ideal scenario: only save data to the database when exiting, to conserve resources
    }

    // there's no need to use this, as the database call already gives the items
    // in the correct order. but I'm leaving it here since it works and shows
    // how I would have done it if I had to do it outside the SQL.
    private fun sortByTimeStamp() {

        withState {

            if (it.items.isEmpty() || it.items.size == 1) return@withState

            setState { copy(items = items.sortedByDescending { item -> item.timeStamp }) } // newest first
        }
    } // sortByTimeStamp

    // for clearing all list items. (not used atm)
    fun resetList() {

        setState { copy(items = emptyList()) }
        // would need to clear the database as well
    }

    // no need to use it atm, since individual items are saved on adding.
    private fun saveAllDataToDb() {

        withState {
            for (item in it.items) {

                insertIntoDb(item)
            }
        }
    } // saveAllDataToDb

} // ObsListViewModel