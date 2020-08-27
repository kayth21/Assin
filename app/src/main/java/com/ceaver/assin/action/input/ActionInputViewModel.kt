package com.ceaver.assin.action.input

import androidx.lifecycle.*
import com.ceaver.assin.action.Action
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate

class ActionInputViewModel(action: Action?, title: Title?, actionType: ActionType) : ViewModel() {
    private val _action = MutableLiveData<Action>()
    private val _symbols = MutableLiveData<List<Title>>()
    val dataReady = zipLiveData(this._action, _symbols)
    private val _status = SingleLiveEvent<ActionInputStatus>()
    val action: LiveData<Action> get() = _action
    val symbols: LiveData<List<Title>> get() = _symbols
    val status: LiveData<ActionInputStatus> get() = _status

    init {
        viewModelScope.launch {
            val titles = TitleRepository.loadAllTitles()
            _symbols.postValue(titles)
        }
        when {
            action != null -> this._action.postValue(action)
            title != null -> when (actionType) {
                ActionType.DEPOSIT -> this._action.postValue(Action(actionType = actionType, buyTitle = title))
                ActionType.WITHDRAW -> this._action.postValue(Action(actionType = actionType, sellTitle = title))
                else -> throw IllegalStateException()
            }
            else -> this._action.postValue(Action(actionType = actionType))
        }
    }

    private fun saveAction(action: Action) {
        viewModelScope.launch {
            _status.value = ActionInputStatus.START_SAVE
            if (action.id > 0) {
                // TODO update... could be tricky when actions are "linked" to positions
                ActionRepository.updateAction(action)
                _status.value = ActionInputStatus.END_SAVE
            } else
                when (action.actionType) {
                    ActionType.DEPOSIT -> {
                        ActionRepository.insertDeposit(action)
                        _status.value = ActionInputStatus.END_SAVE
                    }
                    ActionType.WITHDRAW -> {
                        ActionRepository.insertWithdraw(action)
                        _status.value = ActionInputStatus.END_SAVE
                    }
                    ActionType.TRADE -> {
                        ActionRepository.insertTrade(action)
                        _status.value = ActionInputStatus.END_SAVE
                    }
                }
        }
    }

    fun onSaveTradeClick(buySymbol: Title, buyAmount: BigDecimal, sellSymbol: Title, sellAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
        saveAction(_action.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, sellTitle = sellSymbol, sellAmount = sellAmount, actionDate = actionDate, comment = comment, valueBtc = valueBtc, valueUsd = valueUsd))
    }

    fun onDepositClick(buySymbol: Title, buyAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
        saveAction(_action.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, actionDate = actionDate, comment = comment, valueBtc = valueBtc, valueUsd = valueUsd))
    }

    fun onWithdrawClick(sellSymbol: Title, sellAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
        saveAction(_action.value!!.copy(sellTitle = sellSymbol, sellAmount = sellAmount, actionDate = actionDate, comment = comment, valueBtc = valueBtc, valueUsd = valueUsd))
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

    class Factory(val action: Action?, val title: Title?, val actionType: ActionType) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ActionInputViewModel(action, title, actionType) as T
        }
    }
}
