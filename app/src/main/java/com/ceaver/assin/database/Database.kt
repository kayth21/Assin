package com.ceaver.assin.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.action.ActionEntity
import com.ceaver.assin.action.ActionEntityDao
import com.ceaver.assin.alerts.AlertDao
import com.ceaver.assin.alerts.AlertEntity
import com.ceaver.assin.intentions.IntentionDao
import com.ceaver.assin.intentions.IntentionEntity
import com.ceaver.assin.logging.Log
import com.ceaver.assin.logging.LogDao
import com.ceaver.assin.markets.TitleDao
import com.ceaver.assin.markets.TitleEntity

@androidx.room.Database(entities = arrayOf(ActionEntity::class, TitleEntity::class, IntentionEntity::class, AlertEntity::class, Log::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun actionDao(): ActionEntityDao
    abstract fun alertDao(): AlertDao
    abstract fun intentionDao(): IntentionDao
    abstract fun logDao(): LogDao
    abstract fun titleDao(): TitleDao

    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getInstance(): Database {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(AssinApplication.appContext!!, Database::class.java, "database").fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}