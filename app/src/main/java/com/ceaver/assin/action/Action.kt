package com.ceaver.assin.action

import com.ceaver.assin.common.Exportable
import java.time.LocalDate

interface Action : Exportable {
    val id: Long
    val date: LocalDate
    val comment: String?
    fun getLeftImageResource(): Int
    fun getRightImageResource(): Int
    fun getTitleText(): String
    fun getDetailText(): String
    fun getActionType(): ActionType
    fun toActionEntity(): ActionEntity
    override fun toExport(): List<String>
}