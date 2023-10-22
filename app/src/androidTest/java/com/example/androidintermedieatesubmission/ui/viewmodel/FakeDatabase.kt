package com.example.androidintermedieatesubmission.ui.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidintermedieatesubmission.data.database.RemoteKeysDao
import com.example.androidintermedieatesubmission.data.database.StoryDao

abstract class FakeDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: FakeDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): FakeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    FakeDatabase::class.java
                ).build()
            }
        }
    }
}