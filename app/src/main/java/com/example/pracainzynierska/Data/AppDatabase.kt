package com.example.pracainzynierska.Data

import android.content.Context
import androidx.room.Database

import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(HistoryData::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDBDao(): HistoryDataDAO

    companion object{
        @Volatile
        private var Instance:AppDatabase? = null
        fun getDatabase(context:Context): AppDatabase{
            val tempInstance = Instance
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                "odbyte rozgrywki")
                    .allowMainThreadQueries()
                    .build()
                Instance = instance
                return instance
            }
        }
    }
}