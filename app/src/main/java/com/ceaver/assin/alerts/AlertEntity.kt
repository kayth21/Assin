package com.ceaver.assin.alerts

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.Title
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Entity(tableName = "alert",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("titleId"),
                        onDelete = ForeignKey.CASCADE),
                ForeignKey(
                        entity = Title::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("referenceTitleId"),
                        onDelete = ForeignKey.CASCADE)))
data class AlertEntity(//
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val titleId: String,
        val referenceTitleId: String,
        val alertType: AlertType,
        val source: BigDecimal,
        val target: BigDecimal) : Parcelable {

    fun isNew(): Boolean = this.id == 0L;
}
