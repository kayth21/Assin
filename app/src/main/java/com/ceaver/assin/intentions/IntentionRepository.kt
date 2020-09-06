package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.ceaver.assin.database.Database
import com.ceaver.assin.markets.TitleRepository
import org.greenrobot.eventbus.EventBus

object IntentionRepository {

    suspend fun loadIntentionById(id: Long): Intention {
        return getIntentionDao().loadIntentionById(id)
    }

    fun loadAllIntentions(): List<Intention> {
        return getIntentionDao().loadAllIntentions()
    }

    fun loadAllIntentionsObserved(): LiveData<List<Intention>> {
        val activeCryptoTitlesObserved = TitleRepository.loadActiveCryptoTitles()
        val intentionsObserved = getIntentionDao().loadAllIntentionsObserved()

        return MediatorLiveData<List<Intention>>().apply {
            fun update() {
                val titles = activeCryptoTitlesObserved.value ?: return
                val intentions = intentionsObserved.value ?: return

                value = intentions
            }

            addSource(activeCryptoTitlesObserved) { update() }
            addSource(intentionsObserved) { update() }

            update()
        }
    }

    suspend fun saveIntention(intention: Intention) {
        if (intention.id > 0)
            updateIntention(intention)
        else
            insertIntention(intention)
    }

    suspend fun insertIntention(intention: Intention) {
        getIntentionDao().insertIntention(intention)
        getEventbus().post(IntentionEvents.Insert())
    }

    suspend fun insertIntentions(intentions: List<Intention>) {
        getIntentionDao().insertIntentions(intentions)
        getEventbus().post(IntentionEvents.Insert())
    }

    suspend fun updateIntention(intention: Intention) {
        getIntentionDao().updateIntention(intention)
        getEventbus().post(IntentionEvents.Update())
    }

    suspend fun deleteIntention(intention: Intention) {
        getIntentionDao().deleteIntention(intention)
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