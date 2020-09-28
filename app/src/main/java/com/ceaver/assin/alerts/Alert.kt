package com.ceaver.assin.alerts

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import kotlinx.android.parcel.Parcelize
import org.apache.commons.csv.CSVRecord
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
                    title = dto.title.toTitle(),
                    referenceTitle = dto.referenceTitle.toTitle(),
                    alertType = dto.alert.alertType,
                    source = dto.alert.source,
                    target = dto.alert.target
            )
        }

        suspend fun fromImport(record: CSVRecord): Alert {
            return Alert(
                    title = TitleRepository.loadBySymbol(record.get(0)),
                    referenceTitle = TitleRepository.loadBySymbol(record.get(1)),
                    alertType = AlertType.valueOf(record.get(2)),
                    source = record.get(3).toBigDecimal(),
                    target = record.get(4).toBigDecimal())
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

    fun toExport(): List<String> {
        return listOf(title.symbol, referenceTitle.symbol, alertType.name, source.toPlainString(), target.toPlainString())
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
