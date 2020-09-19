package com.ceaver.assin.intentions.input

import androidx.lifecycle.*
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionRepository
import com.ceaver.assin.intentions.IntentionType
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal

class IntentionInputViewModel(intention: Intention?, title: Title?, amount: BigDecimal?) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _intention = MutableLiveData<Intention>()
    private val _symbols = MutableLiveData<List<Title>>()
    val dataReady = zipLiveData(this._intention, _symbols)
    private val _status = SingleLiveEvent<IntentionInputStatus>()
    val intention: LiveData<Intention> get() = _intention
    val symbols: LiveData<List<Title>> get() = _symbols
    val status: LiveData<IntentionInputStatus> get() = _status

    init {
        viewModelScope.launch {
            val titles = TitleRepository.loadAllTitles()
            _symbols.postValue(titles)
            if (intention != null)
                _intention.postValue(intention)
            else {
                val symbolTitle = title ?: TitleRepository.loadTitleBySymbol("BTC")
                val referenceTitle = TitleRepository.loadTitleBySymbol(if (symbolTitle.symbol == "BTC") "USD" else "BTC")
                _intention.postValue(Intention(
                        0,
                        IntentionType.SELL,
                        symbolTitle,
                        amount,
                        referenceTitle,
                        if (symbolTitle.symbol == "BTC") symbolTitle.cryptoQuotes.price.toBigDecimal() else BigDecimal.ONE)) // TODO
            }
        }
    }

    fun onSaveClick(type: IntentionType, buyTitle: Title, buyAmount: BigDecimal?, sellTitle: Title, sellAmount: BigDecimal, comment: String?) {
        _status.postValue(IntentionInputStatus.START_SAVE)
        viewModelScope.launch {
            IntentionRepository.saveIntention(_intention.value!!.copy(type = type, title = buyTitle, amount = buyAmount, referenceTitle = sellTitle, referencePrice = sellAmount, comment = comment))
            _status.postValue(IntentionInputStatus.END_SAVE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    enum class IntentionInputStatus {
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

    class Factory(val intention: Intention?, val title: Title?, val amount: BigDecimal?) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return IntentionInputViewModel(intention, title, amount) as T
        }
    }

}