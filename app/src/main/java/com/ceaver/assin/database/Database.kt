package com.ceaver.assin.database

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertDao
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionDao
import com.ceaver.assin.logging.Log
import com.ceaver.assin.logging.LogDao
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleDao
import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeDao

@android.arch.persistence.room.Database(entities = arrayOf(Trade::class, Title::class, Intention::class, Alert::class, Log::class), version = 1, exportSchema = false)
@TypeConverters(com.ceaver.assin.database.converter.Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
    abstract fun alertDao(): AlertDao
    abstract fun intentionDao(): IntentionDao
    abstract fun logDao(): LogDao
    abstract fun titleDao(): TitleDao

    companion object {
        private var INSTANCE: Database? = null

        fun getInstance(): Database {
            if (INSTANCE == null) {
                synchronized(Database::class) {
                    INSTANCE = Room.databaseBuilder(com.ceaver.assin.MyApplication.appContext!!, Database::class.java, "database").build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}