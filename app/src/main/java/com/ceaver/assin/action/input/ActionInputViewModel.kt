package com.ceaver.assin.action.input

import androidx.lifecycle.*
import com.ceaver.assin.action.ActionEntity
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.common.SingleMutableLiveData
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate

class ActionInputViewModel(actionEntity: ActionEntity?, title: Title?, actionType: ActionType) : ViewModel() {
    private val _action = MutableLiveData<ActionEntity>()
    private val _symbols = MutableLiveData<List<Title>>()
    val dataReady = zipLiveData(this._action, _symbols)
    private val _status = SingleMutableLiveData<ActionInputStatus>()
    val actionEntity: LiveData<ActionEntity> get() = _action
    val symbols: LiveData<List<Title>> get() = _symbols
    val status: LiveData<ActionInputStatus> get() = _status

    init {
        viewModelScope.launch {
            val titles = TitleRepository.loadAll()
            _symbols.postValue(titles)
        }
        when {
//            actionEntity != null -> this._action.postValue(actionEntity)
//            title != null -> when (actionType) {
//                ActionType.DEPOSIT -> this._action.postValue(ActionEntity(actionType = actionType, buyTitle = title))
//                ActionType.WITHDRAW -> this._action.postValue(ActionEntity(actionType = actionType, sellTitle = title))
//                else -> throw IllegalStateException()
//            }
            else -> this._action.postValue(ActionEntity(actionType = actionType))
        }
    }

    private fun saveAction(actionEntity: ActionEntity) {
//        viewModelScope.launch {
//            _status.value = ActionInputStatus.START_SAVE
//            if (actionEntity.id > 0) {
//                // TODO update... could be tricky when actions are "linked" to positions
////                ActionRepository.updateAction(actionEntity.toAction())
//                _status.value = ActionInputStatus.END_SAVE
//            } else
//                when (actionEntity.actionType) {
//                    ActionType.DEPOSIT -> {
//                        ActionRepository.insertDeposit(Deposit.fromAction(actionEntity))
//                        _status.value = ActionInputStatus.END_SAVE
//                    }
//                    ActionType.WITHDRAW -> {
//                        ActionRepository.insertWithdraw(Withdraw.fromAction(actionEntity))
//                        _status.value = ActionInputStatus.END_SAVE
//                    }
//                    ActionType.TRADE -> {
//                        ActionRepository.insertTrade(Trade.fromAction(actionEntity))
//                        _status.value = ActionInputStatus.END_SAVE
//                    }
//                }
//        }
    }

    fun onSaveTradeClick(buySymbol: Title, buyAmount: BigDecimal, sellSymbol: Title, sellAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
//        saveAction(_action.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, sellTitle = sellSymbol, sellAmount = sellAmount, actionDate = actionDate, comment = comment, valueCrypto = valueBtc, valueFiat = valueUsd))
    }

    fun onDepositClick(buySymbol: Title, buyAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
//        saveAction(_action.value!!.copy(buyTitle = buySymbol, buyAmount = buyAmount, actionDate = actionDate, comment = comment, valueCrypto = valueBtc, valueFiat = valueUsd))
    }

    fun onWithdrawClick(sellSymbol: Title, sellAmount: BigDecimal, actionDate: LocalDate, comment: String?, valueBtc: BigDecimal, valueUsd: BigDecimal) {
//        saveAction(_action.value!!.copy(sellTitle = sellSymbol, sellAmount = sellAmount, actionDate = actionDate, comment = comment, valueCrypto = valueBtc, valueFiat = valueUsd))
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

    class Factory(val actionEntity: ActionEntity?, val title: Title?, val actionType: ActionType) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ActionInputViewModel(actionEntity, title, actionType) as T
        }
    }
}
