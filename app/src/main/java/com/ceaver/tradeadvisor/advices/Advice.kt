package com.ceaver.tradeadvisor.advices

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "advice")
data class Advice(//
        @ColumnInfo(name = "id")@PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "adviceDate") val adviceDate: Date) {
}