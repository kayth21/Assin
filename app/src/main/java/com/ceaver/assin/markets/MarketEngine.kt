package com.ceaver.assin.markets

import com.ceaver.assin.advices.AdviceEvents
import com.ceaver.assin.assets.Category
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.engine.EngineEvents
import com.ceaver.assin.engine.TradeAdviceEngine
import com.ceaver.assin.exchanges.Exchange
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

object MarketEngine {

    fun wakeup() {
        // hack because object is lazy initialized
    }

    init {
        EventBus.getDefault().register(this)
    }

    fun run() {
        Exchange.getExchanges(Symbol.BTC).first().update(Symbol.BTC)
        Symbol.values(Category.CRYPTO).filter { it != Symbol.BTC }.stream().forEach{ Exchange.getExchanges(it).first().update(it) }
        EventBus.getDefault().post(MarketEngineEvents.Loaded())
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: MarketEngineEvents.Run) {
        run()
    }
}