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

    fun init(intentionId: Optional<Long>): IntentionInputViewModel {
        TitleRepository.loadAllTitlesAsync(false) { symbols.postValue(it) }
        if (intentionId.isPresent)
            IntentionRepository.loadIntentionAsync(intentionId.get(), false) { intention.postValue(it) }
        else
            BackgroundThreadExecutor.execute {
                val btc = TitleRepository.loadTitleBySymbol("BTC")
                val usd = TitleRepository.loadTitleBySymbol("USD")
                intention.postValue(Intention(0, btc, 1.0, usd, 5000.0))
            }
        return this
    }

    fun onSaveClick(buyTitle: Title, buyAmount: Double, sellTitle: Title, sellAmount: Double, comment: String) {
        status.postValue(IntentionInputStatus.START_SAVE)
        IntentionRepository.saveIntentionAsync(intention.value!!.copy(buyTitle = buyTitle, buyAmount = buyAmount, sellTitle = sellTitle, sellAmount = sellAmount, comment = comment), true) {
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