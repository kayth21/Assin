package com.ceaver.assin.home

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.ceaver.assin.AssinWorkers
import java.util.concurrent.atomic.AtomicBoolean

class HomeViewModel : ViewModel() {
    val assinWorkerRunning: LiveData<Boolean> = SingleLiveEvent<Boolean>()
            .apply {

                fun update() {
                    val running = AssinWorkers.running.value ?: return
                    value = running
                }
                addSource(AssinWorkers.running) { update() }
                update()
            }
}

// TODO remove copy/paste
class SingleLiveEvent<T> : MediatorLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    companion object {
        private val TAG = "SingleLiveEvent"
    }
}