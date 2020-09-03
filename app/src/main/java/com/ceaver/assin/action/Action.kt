package com.ceaver.assin.action

import java.time.LocalDate

interface Action {
    fun getActionDate(): LocalDate
    fun getLeftImageResource(): Int
    fun getRightImageResource(): Int
    fun getTitleText(): String
    fun getDetailText(): String
    fun getActionType(): ActionType
    fun toActionEntity(): ActionEntity
    fun toExport(): List<String>
}