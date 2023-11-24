package com.example.dine_aid.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dine_aid.data.RecipeResult

@Database(entities = [RecipeResult::class], version = 1)
abstract class DineAidDatabase : RoomDatabase() {
     abstract val dineAidDatabaseDao : DineAidDatabaseDao
}

private lateinit var INSTANCE: DineAidDatabase

fun getDatabase(context: Context) : DineAidDatabase {
    synchronized(DineAidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                DineAidDatabase::class.java,"DineAidDatabase"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}