package com.ceaver.assin.advices

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeStrategy
import java.time.LocalDate

@Entity(tableName = "advice", foreignKeys = arrayOf(ForeignKey(entity = Trade::class, parentColumns = arrayOf("id"), childColumns = arrayOf("tradeId"), onDelete = CASCADE)))
data class Advice(//
        @ColumnInfo(name = "id")@PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "tradeId") var tradeId: Long,
        @ColumnInfo(name = "adviceDate") val adviceDate: LocalDate,
        @ColumnInfo(name = "strategy") val strategy: TradeStrategy)