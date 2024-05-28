package com.github.yuriisurzhykov.purs.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.yuriisurzhykov.purs.data.cache.LocationDao
import com.github.yuriisurzhykov.purs.data.cache.model.LocationCache
import com.github.yuriisurzhykov.purs.data.cache.model.WorkingHourCache

@Database(
    entities = [LocationCache::class, WorkingHourCache::class],
    version = 1,
    exportSchema = true
)
abstract class PursDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}