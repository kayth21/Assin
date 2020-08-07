package com.ceaver.assin.action.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceaver.assin.action.Action
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import java.math.BigDecimal
import java.time.LocalDate

class ActionInputViewModel : ViewModel() {

    val action = MutableLiveData<Action>()
    val symbols = MutableLiveData<List<Title>>()
    val dataReady = zipLiveData(action, symbols)
    val status = SingleLiveEvent<ActionInputStatus>()

    fun initTrade(action: Action?, title: Title?, actionType: ActionType): ActionInputViewModel {
        TitleRepository.loadAllTitlesAsync(false) { symbols.postValue(it) }
        when {
            action != null -> this.action.postValue(action)
            title != null -> when (actionType) {
                ActionType.DEPOSIT -> this.action.postValue(Action(actionType = actionType, buyTitle = title))
                ActionType.WITHDRAW -> this.action.postValue(Action(actionType = actionType, sellTitle = title))
                else -> throw IllegalStateException()
            }
            else -> this.action.postValue(Action(actionType = actionType))
        }
        return this
    }

    private fun saveAction(action: Action) {
        status.value = ActionInputStatus.START_SAVE
        if (action.id > 0) {
            // TODO update... could be tricky when actions are "linked" to positions
            ActionRepository.updateActionAsync(action, true) { status.value = ActionInputStatus.END_SAVE }
        } else
            when (action.actionType) {
                ActionType.DEPOSIT -> ActionRepository.insertDepositAsync(action, true) { status.value = ActionInputStatus.END_SAVE }
                ActionType.WITHDRAW -> ActionRepository.insertWithdrawAsync(action, true) { status.value = ActionInputStatus.END_SAVE }
                ActionType.TRADE -> ActionRepository.insertTradeAsync(action, true) { status.value = ActionInputStatus.END_SAVE }
            }
    }

    fun onSaveTradeClick(buySymbol: Title, buyAmount: BigDecimal, sellSymbol: Title, sellAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
        saveAction(action.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, sellTitle = sellSymbol, sellAmount = sellAmount, actionDate = actionDate, comment = comment, valueBtc = valueBtc, valueUsd = valueUsd))
    }

    fun onDepositClick(buySymbol: Title, buyAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
        saveAction(action.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, actionDate = actionDate, comment = comment, valueBtc = valueBtc, valueUsd = valueUsd))
    }

    fun onWithdrawClick(sellSymbol: Title, sellAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
        saveAction(action.value!!.copy(sellTitle = sellSymbol, sellAmount = sellAmount, actionDate = actionDate, comment = comment, valueBtc = valueBtc, valueUsd = valueUsd))
    }

    enum class ActionInputStatus {
        START_SAVE,
        END_SAVE
    }

    fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
        return MediatorLiveData<Pair<A, B>>().apply {
            var lastA: A? = null
            var lastB: B? = null

            fun update() {
                val localLastA = lastA
                val localLastB = lastB
                if (localLastA != null && localLastB != null)
                    this.value = Pair(localLastA, localLastB)
            }

            addSource(a) {
                lastA = it
                update()
            }
            addSource(b) {
                lastB = it
                update()
            }
        }
    }
}
