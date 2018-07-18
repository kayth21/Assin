package com.ceaver.tradeadvisor.database

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.ceaver.adviceadvisor.advices.AdviceDao
import com.ceaver.tradeadvisor.MyApplication
import com.ceaver.tradeadvisor.advices.Advice
import com.ceaver.tradeadvisor.database.converter.Converters
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeDao

@android.arch.persistence.room.Database(entities = arrayOf(Trade::class, Advice::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
    abstract fun adviceDao(): AdviceDao

    companion object {
        private var INSTANCE: Database? = null

        fun getInstance(): Database {
            if (INSTANCE == null) {
                synchronized(Database::class) {
                    INSTANCE = Room.databaseBuilder(MyApplication.appContext!!, Database::class.java, "database").allowMainThreadQueries().build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}