package com.ceaver.assin.intentions

import com.ceaver.assin.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

object IntentionRepository {

    suspend fun loadIntentionById(id: Long): Intention = withContext(Dispatchers.IO) {
        return@withContext getIntentionDao().loadIntentionById(id)
    }

    suspend fun loadAllIntentions(): List<Intention> = withContext(Dispatchers.IO) {
        return@withContext getIntentionDao().loadAllIntentions()
    }

    suspend fun saveIntention(intention: Intention) = withContext(Dispatchers.IO) {
        if (intention.id > 0)
            updateIntention(intention)
        else
            insertIntention(intention)
    }

    suspend fun insertIntention(intention: Intention) = withContext(Dispatchers.IO) {
        getIntentionDao().insertIntention(intention)
        getEventbus().post(IntentionEvents.Insert())
    }

    suspend fun insertIntentions(intentions: List<Intention>) = withContext(Dispatchers.IO) {
        getIntentionDao().insertIntentions(intentions)
        getEventbus().post(IntentionEvents.Insert())
    }

    suspend fun updateIntention(intention: Intention) = withContext(Dispatchers.IO) {
        getIntentionDao().updateIntention(intention)
        getEventbus().post(IntentionEvents.Update())
    }

    suspend fun deleteIntention(intention: Intention) = withContext(Dispatchers.IO) {
        getIntentionDao().deleteIntention(intention)
        getEventbus().post(IntentionEvents.Delete())
    }

    suspend fun deleteAllIntentions() = withContext(Dispatchers.IO) {
        getIntentionDao().deleteAllIntentions()
        getEventbus().post(IntentionEvents.DeleteAll())
    }

    private fun getIntentionDao(): IntentionDao = getDatabase().intentionDao()

    private fun getDatabase(): Database = Database.getInstance()

    private fun getEventbus() = EventBus.getDefault()
}