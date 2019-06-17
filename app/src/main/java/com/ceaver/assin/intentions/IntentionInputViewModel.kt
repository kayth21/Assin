package com.ceaver.assin.intentions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import com.ceaver.assin.threading.BackgroundThreadExecutor
import java.util.*

class IntentionInputViewModel : ViewModel() {

    val intention = MutableLiveData<Intention>()
    val symbols = MutableLiveData<List<Title>>()
    val dataReady = zipLiveData(intention, symbols)
    val status = SingleLiveEvent<IntentionInputStatus>()


    fun init(intentionId: Optional<Long>, symbol: Optional<String>, amount: Optional<Double>): IntentionInputViewModel {
        TitleRepository.loadAllTitlesAsync(false) { symbols.postValue(it) }
        if (intentionId.isPresent)
            IntentionRepository.loadIntentionAsync(intentionId.get(), false) { intention.postValue(it) }
        else
            BackgroundThreadExecutor.execute {
                val symbolTitle = TitleRepository.loadTitleBySymbol(symbol.orElse("BTC"))
                val referenceTitle = TitleRepository.loadTitleBySymbol(if (symbolTitle.symbol == "BTC") "USD" else "BTC")
                intention.postValue(Intention(0, IntentionType.SELL, symbolTitle, amount, referenceTitle, if (symbolTitle.symbol == "BTC") symbolTitle.priceUsd else symbolTitle.priceBtc))
            }
        return this
    }

    fun onSaveClick(type: IntentionType, buyTitle: Title, buyAmount: Optional<Double>, sellTitle: Title, sellAmount: Double, comment: String) {
        status.postValue(IntentionInputStatus.START_SAVE)
        IntentionRepository.saveIntentionAsync(intention.value!!.copy(type = type, title = buyTitle, amount = buyAmount, referenceTitle = sellTitle, referencePrice = sellAmount, comment = comment), true) {
            status.postValue(IntentionInputStatus.END_SAVE)
        }
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
}