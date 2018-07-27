package com.ceaver.adviceadvisor.advices

import com.ceaver.tradeadvisor.database.Database
import com.ceaver.tradeadvisor.advices.Advice
import com.ceaver.tradeadvisor.advices.AdviceEvents
import com.ceaver.tradeadvisor.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object AdviceRepository {

    fun loadAllAdvices() {
        BackgroundThreadExecutor.execute { val advices = getAdviceDao().loadAllAdvices(); EventBus.getDefault().post(AdviceEvents.LoadAll(advices)) }
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

    fun loadAdvice(id: Long) {
        BackgroundThreadExecutor.execute { val advice =  getAdviceDao().loadAdvice(id); EventBus.getDefault().post(AdviceEvents.Load(advice)) }
    }

    private fun getAdviceDao(): AdviceDao {
        return getDatabase().adviceDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}