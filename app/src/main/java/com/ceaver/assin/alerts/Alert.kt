package com.ceaver.assin.alerts

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.ceaver.assin.markets.Title
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class Alert(//
        val id: Long = 0,
        val title: Title,
        val referenceTitle: Title,
        val alertType: AlertType,
        val source: BigDecimal,
        val target: BigDecimal) : Parcelable {

    fun isNew(): Boolean = this.id == 0L;

    companion object Factory {
        fun fromDto(dto: AlertDto): Alert {
            return Alert(
                    id = dto.alert.id,
                    title = dto.title,
                    referenceTitle = dto.referenceTitle,
                    alertType = dto.alert.alertType,
                    source = dto.alert.source,
                    target = dto.alert.target
            )
        }
    }

    fun toEntity(): AlertEntity {
        return AlertEntity(
                id = id,
                titleId = title.id,
                referenceTitleId = referenceTitle.id,
                alertType = alertType,
                source = source,
                target = target
        )
    }

    object Difference : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem == newItem
        }
    }
}
