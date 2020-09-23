package com.ceaver.assin.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ceaver.assin.AssinApplication
import com.ceaver.assin.action.ActionEntity
import com.ceaver.assin.action.ActionEntityDao
import com.ceaver.assin.alerts.AlertEntity
import com.ceaver.assin.alerts.AlertEntityDao
import com.ceaver.assin.intentions.IntentionEntity
import com.ceaver.assin.intentions.IntentionEntityDao
import com.ceaver.assin.logging.LogEntity
import com.ceaver.assin.logging.LogEntityDao
import com.ceaver.assin.markets.TitleEntity
import com.ceaver.assin.markets.TitleEntityDao

@androidx.room.Database(entities = arrayOf(ActionEntity::class, TitleEntity::class, IntentionEntity::class, AlertEntity::class, LogEntity::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun actionDao(): ActionEntityDao
    abstract fun alertDao(): AlertEntityDao
    abstract fun intentionDao(): IntentionEntityDao
    abstract fun logDao(): LogEntityDao
    abstract fun titleDao(): TitleEntityDao

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