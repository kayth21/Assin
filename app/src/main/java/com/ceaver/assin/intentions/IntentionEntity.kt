package com.ceaver.assin.intentions

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ceaver.assin.markets.TitleEntity
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "intention",
        indices = [Index(value = ["baseTitleId", "quoteTitleId"])],
        foreignKeys = [
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("baseTitleId"),
                    onDelete = ForeignKey.CASCADE), // TODO
            ForeignKey(
                    entity = TitleEntity::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("quoteTitleId"),
                    onDelete = ForeignKey.CASCADE)]) // TODO
class IntentionEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val type: IntentionType,
        val active: Boolean = true,
        val quantity: BigDecimal? = null,
        val baseTitleId: String,
        val quoteTitleId: String,
        val target: BigDecimal,
        val status: IntentionStatus = IntentionStatus.WAIT,
        val snoozeNear: LocalDateTime = LocalDateTime.now(),
        val snoozeAct: LocalDateTime = LocalDateTime.now(),
        var comment: String? = null)
    : Parcelable