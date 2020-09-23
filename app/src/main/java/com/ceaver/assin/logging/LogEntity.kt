package com.ceaver.assin.logging

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "log")
data class LogEntity(//
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val timestamp: LocalDateTime,
        val message: String,
        val uuid: UUID) {

    object Difference : DiffUtil.ItemCallback<LogEntity>() {
        override fun areItemsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
            return oldItem == newItem
        }
    }
}