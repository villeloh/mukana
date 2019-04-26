package com.example.mukana.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mukana.model.BirdObservation

@Database(entities = [BirdObservation::class], version = 2, exportSchema = false)
@TypeConverters(TypeConverterz::class)
abstract class BirdObsDatabase : RoomDatabase() {

    abstract fun birdObsDao(): BirdObsDao

    // singleton to ensure only one instance of the database
    companion object {

        @Volatile
        private var INSTANCE: BirdObsDatabase? = null

        fun getDatabase(context: Context): BirdObsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirdObsDatabase::class.java,
                    "BirdObsDatabase"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            } // synchronized
        } // getDatabase
    } // companion object

} // BirdObsDatabase
