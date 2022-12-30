package com.example.pracainzynierska.Data

import android.content.Context
import androidx.room.Database

import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Wewnętrzna baza danych
 *
 */
@Database(entities = [(HistoryData::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDBDao(): HistoryDataDAO

    companion object{
        @Volatile
        private var Instance:AppDatabase? = null
        // wzorzec singleton
        // tworzenie połączenia z wewnętrzną bazą danych
        /**
         * Funkcja tworzy instancję bazy danych
         *
         * @param context kontekst aplikacji
         * @return instancja bazy danych
         */
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