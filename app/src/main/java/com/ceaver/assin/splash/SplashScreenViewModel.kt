package com.ceaver.assin.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.ceaver.assin.common.SingleLiveEvent
import com.ceaver.assin.markets.TitleRepository

class SplashScreenViewModel : ViewModel() {
    private val hasTitles = SingleLiveEvent<Boolean>()

    fun init(splashScreenActivity: SplashScreenActivity, hasTitleObserver: Observer<Boolean>): SplashScreenViewModel {
        hasTitles.observe(splashScreenActivity, hasTitleObserver)
        TitleRepository.hasTitlesAsync(false) { hasTitles.postValue(it) }
        return this
    }
}