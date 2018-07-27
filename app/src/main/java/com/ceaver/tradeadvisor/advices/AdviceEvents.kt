package com.ceaver.tradeadvisor.advices

class AdviceEvents {
    data class Load(val advice: Advice)
    data class LoadAll(val advices: List<Advice>)
    class Update()
    class Insert()
    class Delete()
    class DeleteAll()
}