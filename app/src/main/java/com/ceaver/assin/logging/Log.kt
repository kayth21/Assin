package com.ceaver.assin.logging

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "log")
data class Log(//
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "timestamp") val timestamp: LocalDateTime,
        @ColumnInfo(name = "message") val message: String,
        @ColumnInfo(name = "uuid") val uuid: UUID)