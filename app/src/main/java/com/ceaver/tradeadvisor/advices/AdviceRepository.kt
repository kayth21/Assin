package com.ceaver.adviceadvisor.advices

import com.ceaver.tradeadvisor.database.Database
import com.ceaver.tradeadvisor.advices.Advice

object AdviceRepository {

    fun loadAdvices(): List<Advice> {
        return getAdviceDao().loadAdvices()
    }

    fun saveAdvice(advice: Advice) {
        if (advice.id > 0) updateAdvice(advice) else insertAdvice(advice)
    }

    fun insertAdvice(advice: Advice) {
        getAdviceDao().insertAdvice(advice)
    }

    fun updateAdvice(advice: Advice) {
        getAdviceDao().updateAdvice(advice)
    }

    fun deleteAdvice(advice: Advice) {
        getAdviceDao().deleteAdvice(advice)
    }

    fun deleteAllAdvices() {
        getAdviceDao().deleteAllAdvices()
    }

    fun loadAdvice(id: Long): Advice {
        return getAdviceDao().loadAdvice(id)
    }

    private fun getAdviceDao(): AdviceDao {
        return getDatabase().adviceDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}