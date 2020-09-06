package com.ceaver.assin.alerts

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
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
        @ColumnInfo(name = "target") val target: BigDecimal) : Parcelable {

    fun isNew(): Boolean = this.id == 0L;

    object Difference : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem == newItem
        }
    }
}
