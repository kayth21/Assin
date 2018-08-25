package com.ceaver.assin.advices

import android.os.Handler
import android.os.Looper
import com.ceaver.adviceadvisor.advices.AdviceDao
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object AdviceRepository {

    fun loadAdvice(id: Long): Advice {
        return getAdviceDao().loadAdvice(id)
    }

    fun loadAdviceAsync(id: Long, callbackInMainThread: Boolean, callback: (Advice) -> Unit) {
        BackgroundThreadExecutor.execute {
            val advice = getAdviceDao().loadAdvice(id)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(advice) }
            else {
                callback.invoke(advice);
            }
        }
    }

    fun loadAdvicesOfTrade(tradeId: Long): List<Advice> {
        return getAdviceDao().loadAdvicesFromTrade(tradeId)
    }

    fun loadAllAdvices(): List<Advice> {
        return getAdviceDao().loadAllAdvices()
    }

    fun loadAllAdvicesAsync(callbackInMainThread: Boolean, callback: (List<Advice>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val advices = loadAllAdvices()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(advices) }
            else
                callback.invoke(advices)
        }
    }

    fun saveAdvice(advice: Advice) {
        if (advice.id > 0) updateAdvice(advice) else insertAdvice(advice)
    }

    fun saveAdviceAsync(advice: Advice) {
        if (advice.id > 0) updateAdviceAsync(advice) else insertAdviceAsync(advice)
    }

    fun insertAdvice(advice: Advice) {
        getAdviceDao().insertAdvice(advice); EventBus.getDefault().post(AdviceEvents.Insert())
    }

    fun insertAdviceAsync(advice: Advice) {
        BackgroundThreadExecutor.execute { insertAdvice(advice) }
    }

    fun updateAdvice(advice: Advice) {
        getAdviceDao().updateAdvice(advice); EventBus.getDefault().post(AdviceEvents.Update())
    }

    fun updateAdviceAsync(advice: Advice) {
        BackgroundThreadExecutor.execute { updateAdvice(advice) }
    }

    fun deleteAdvice(advice: Advice) {
        getAdviceDao().deleteAdvice(advice); EventBus.getDefault().post(AdviceEvents.Delete())
    }

    fun deleteAdviceAsync(advice: Advice) {
        BackgroundThreadExecutor.execute { deleteAdvice(advice) }
    }

    fun deleteAllAdvices() {
        getAdviceDao().deleteAllAdvices(); EventBus.getDefault().post(AdviceEvents.DeleteAll())
    }

    fun deleteAllAdvicesAsync() {
        BackgroundThreadExecutor.execute { deleteAllAdvices() }
    }

    private fun getAdviceDao(): AdviceDao {
        return getDatabase().adviceDao()
    }

    private fun getDatabase(): com.ceaver.assin.database.Database {
        return com.ceaver.assin.database.Database.getInstance()
    }
}