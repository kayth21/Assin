package com.ceaver.assin.logging

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "log")
data class LogEntity(//
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val timestamp: LocalDateTime,
        val message: String) {


}