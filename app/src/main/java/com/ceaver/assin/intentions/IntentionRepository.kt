package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.database.Database
import org.greenrobot.eventbus.EventBus

object IntentionRepository {

    suspend fun loadIntentionById(id: Long): Intention {
        return getIntentionDao().loadIntentionById(id).toIntention()
    }

    fun loadAllIntentions(): List<Intention> {
        return getIntentionDao().loadAllIntentions().map { it.toIntention() }
    }

    fun loadAllIntentionsObserved(): LiveData<List<Intention>> {
        return Transformations.map(getIntentionDao().loadAllIntentionsObserved()) { it.map { it.toIntention() } }
    }

    suspend fun saveIntention(intention: Intention) {
        if (intention.id > 0)
            updateIntention(intention)
        else
            insertIntention(intention)
    }

    suspend fun insertIntention(intention: Intention) {
        getIntentionDao().insertIntention(intention.toIntentionEntity())
        getEventbus().post(IntentionEvents.Insert())
    }

    suspend fun insertIntentions(intentions: List<Intention>) {
        getIntentionDao().insertIntentions(intentions.map { it.toIntentionEntity() })
        getEventbus().post(IntentionEvents.Insert())
    }

    suspend fun updateIntention(intention: Intention) {
        getIntentionDao().updateIntention(intention.toIntentionEntity())
        getEventbus().post(IntentionEvents.Update())
    }

    suspend fun deleteIntention(intention: Intention) {
        getIntentionDao().deleteIntention(intention.toIntentionEntity())
        getEventbus().post(IntentionEvents.Delete())
    }

    suspend fun deleteAllIntentions() {
        getIntentionDao().deleteAllIntentions()
        getEventbus().post(IntentionEvents.DeleteAll())
    }

    private fun getIntentionDao(): IntentionDao = getDatabase().intentionDao()

    private fun getDatabase(): Database = Database.getInstance()

    private fun getEventbus() = EventBus.getDefault()
}