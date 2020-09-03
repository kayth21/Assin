package com.ceaver.assin.action

import java.time.LocalDate

interface IAction {
    fun getActionDate(): LocalDate
    fun getLeftImageResource(): Int
    fun getRightImageResource(): Int
    fun getTitleText(): String
    fun getDetailText(): String
    fun getActionType(): ActionType
    fun toAction(): Action
    fun toExport(): List<String>
}