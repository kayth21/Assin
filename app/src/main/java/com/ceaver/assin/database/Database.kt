package com.ceaver.assin.database

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.ceaver.adviceadvisor.advices.AdviceDao
import com.ceaver.assin.advices.Advice
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertDao
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.assets.AssetDao
import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeDao

@android.arch.persistence.room.Database(entities = arrayOf(Trade::class, Advice::class, Asset::class, Alert::class), version = 1, exportSchema = false)
@TypeConverters(com.ceaver.assin.database.converter.Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
    abstract fun adviceDao(): AdviceDao
    abstract fun assetDao(): AssetDao
    abstract fun alertDao(): AlertDao

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