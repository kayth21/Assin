package com.ceaver.assin.alerts

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.TitleEntity
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Entity(tableName = "alert",
        indices = [Index(value = ["baseTitleId", "quoteTitleId"])],
        foreignKeys = [
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("baseTitleId"),
                    onDelete = ForeignKey.CASCADE),
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("quoteTitleId"),
                    onDelete = ForeignKey.CASCADE)])
data class AlertEntity(//
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val type: AlertType,
        val active: Boolean,
        val baseTitleId: String? = null,
        val quoteTitleId: String? = null,
        val last: BigDecimal,
        val target: BigDecimal,
        val diff: BigDecimal? = null
) : Parcelable {
}
