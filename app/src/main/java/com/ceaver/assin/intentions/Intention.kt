package com.ceaver.assin.intentions

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.markets.Title
import java.time.LocalDate

@Entity(tableName = "intention")
data class Intention(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "type") val type: IntentionType,
        @ColumnInfo(name = "title") var title: Title,
        @ColumnInfo(name = "amount") var amount: Double,
        @ColumnInfo(name = "referenceTitle") var referenceTitle: Title,
        @ColumnInfo(name = "referencePrice") var referencePrice: Double,
        @ColumnInfo(name = "creationDate") var creationDate: LocalDate = LocalDate.now(),
        @ColumnInfo(name = "status") val status: IntentionStatus = IntentionStatus.WAIT,
        @ColumnInfo(name = "comment") var comment: String = "") {
}