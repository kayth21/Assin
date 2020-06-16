package com.ceaver.assin.alerts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import java.math.BigDecimal

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
        @ColumnInfo(name = "source") val source: BigDecimal,
        @ColumnInfo(name = "target") val target: BigDecimal) {

    fun isNew(): Boolean = this.id == 0L;
}
