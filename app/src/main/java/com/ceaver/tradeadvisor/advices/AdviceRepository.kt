package com.ceaver.adviceadvisor.advices

import com.ceaver.tradeadvisor.database.Database
import com.ceaver.tradeadvisor.advices.Advice
import com.ceaver.tradeadvisor.advices.AdviceEvents
import com.ceaver.tradeadvisor.threading.BackgroundThreadExecutor
import com.ceaver.tradeadvisor.trades.Trade
import org.greenrobot.eventbus.EventBus

object AdviceRepository {

    fun loadAdvice(id: Long, callback: (Advice) -> Unit) {
        BackgroundThreadExecutor.execute { val advice = getAdviceDao().loadAdvice(id); callback.invoke(advice) }
    }

    fun loadAllAdvices(callback: (List<Advice>) -> Unit) {
        BackgroundThreadExecutor.execute { val advices = getAdviceDao().loadAllAdvices(); callback.invoke(advices) }
    }

    fun saveAdvice(advice: Advice) {
        if (advice.id > 0) updateAdvice(advice) else insertAdvice(advice)
    }

    fun insertAdvice(advice: Advice) {
        BackgroundThreadExecutor.execute { getAdviceDao().insertAdvice(advice); EventBus.getDefault().post(AdviceEvents.Insert()) }
    }

    fun updateAdvice(advice: Advice) {
        BackgroundThreadExecutor.execute { getAdviceDao().updateAdvice(advice); EventBus.getDefault().post(AdviceEvents.Update()) }
    }

    fun deleteAdvice(advice: Advice) {
        BackgroundThreadExecutor.execute { getAdviceDao().deleteAdvice(advice); EventBus.getDefault().post(AdviceEvents.Delete()) }
    }

    fun deleteAllAdvices() {
        BackgroundThreadExecutor.execute { getAdviceDao().deleteAllAdvices(); EventBus.getDefault().post(AdviceEvents.DeleteAll()) }
    }

    private fun getAdviceDao(): AdviceDao {
        return getDatabase().adviceDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}