package com.ceaver.assin.intentions

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object IntentionRepository {

    fun loadIntentionById(id: Long): Intention {
        return getIntentionDao().loadIntentionById(id)
    }

    fun loadIntentionAsync(id: Long, callbackInMainThread: Boolean, callback: (Intention) -> Unit) {
        BackgroundThreadExecutor.execute {
            val intention = loadIntentionById(id)
            handleCallback(callbackInMainThread, callback, intention)
        }
    }

    fun loadAllIntentions(): List<Intention> {
        return getIntentionDao().loadAllIntentions()
    }

    fun loadAllIntentionsAsync(callbackInMainThread: Boolean, callback: (List<Intention>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val intentions = loadAllIntentions()
            handleCallback(callbackInMainThread, callback, intentions)
        }
    }

    fun saveIntention(intention: Intention) {
        if (intention.id > 0)
            updateIntention(intention)
        else
            insertIntention(intention)
    }

    fun saveIntentionAsync(intention: Intention, callbackInMainThread: Boolean, callback: () -> Unit) {
        if (intention.id > 0)
            updateIntentionAsync(intention, callbackInMainThread, callback)
        else
            insertIntentionAsync(intention, callbackInMainThread, callback)
    }

    fun insertIntention(intention: Intention) {
        getIntentionDao().insertIntention(intention)
        getEventbus().post(IntentionEvents.Insert())
    }

    fun insertIntentionAsync(intention: Intention, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertIntention(intention)
            handleCallback(callbackInMainThread, callback)
        }
    }

    fun insertIntentions(intentions: List<Intention>) {
        getIntentionDao().insertIntentions(intentions)
        getEventbus().post(IntentionEvents.Insert())
    }

    fun insertIntentionsAsync(intentions: List<Intention>, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertIntentions(intentions)
            handleCallback(callbackInMainThread, callback)
        }
    }

    fun updateIntention(intention: Intention) {
        getIntentionDao().updateIntention(intention)
        getEventbus().post(IntentionEvents.Update())
    }

    fun updateIntentionAsync(intention: Intention, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            updateIntention(intention)
            handleCallback(callbackInMainThread, callback)
        }
    }

    fun deleteIntention(intention: Intention) {
        getIntentionDao().deleteIntention(intention)
        getEventbus().post(IntentionEvents.Delete())
    }

    fun deleteIntentionAsync(intention: Intention) {
        BackgroundThreadExecutor.execute {
            deleteIntention(intention)
        }
    }

    fun deleteAllIntentions() {
        getIntentionDao().deleteAllIntentions()
        getEventbus().post(IntentionEvents.DeleteAll())
    }

    fun deleteAllIntentionsAsync(callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            deleteAllIntentions()
            handleCallback(callbackInMainThread, callback)
        }
    }

    private fun handleCallback(callbackInMainThread: Boolean, callback: () -> Unit) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post(callback)
        else
            callback.invoke()
    }

    private fun handleCallback(callbackInMainThread: Boolean, callback: (List<Intention>) -> Unit, intentions: List<Intention>) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post { callback.invoke(intentions) }
        else
            callback.invoke(intentions)
    }


    private fun handleCallback(callbackInMainThread: Boolean, callback: (Intention) -> Unit, intention: Intention) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post { callback.invoke(intention) }
        else
            callback.invoke(intention)
    }

    private fun getIntentionDao(): IntentionDao = getDatabase().intentionDao()

    private fun getDatabase(): Database = Database.getInstance()

    private fun getEventbus() = EventBus.getDefault()
}