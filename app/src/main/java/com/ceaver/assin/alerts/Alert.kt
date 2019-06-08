package com.ceaver.assin.alerts

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.ceaver.assin.markets.Title

@Entity(tableName = "alert",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("reference"),
                        onDelete = ForeignKey.CASCADE),
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("symbol"),
                        onDelete = ForeignKey.CASCADE)))
data class Alert(//
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "symbol") val symbol: Title,
        @ColumnInfo(name = "reference") val reference: Title,
        @ColumnInfo(name = "alertType") val alertType: AlertType,
        @ColumnInfo(name = "source") val source: Double,
        @ColumnInfo(name = "target") val target: Double) {

    fun isNew(): Boolean = this.id == 0L;
}
