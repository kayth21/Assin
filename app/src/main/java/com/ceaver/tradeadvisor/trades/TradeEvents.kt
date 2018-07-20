package com.ceaver.tradeadvisor.trades

class TradeEvents {
    data class Load(val trade: Trade)
    data class LoadAll(val trades: List<Trade>)
    class Update()
    class Insert()
    class Delete()
    class DeleteAll()
}