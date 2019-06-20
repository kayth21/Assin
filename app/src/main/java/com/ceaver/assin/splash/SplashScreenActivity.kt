package com.ceaver.assin.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.R
import com.ceaver.assin.StartActivity
import com.ceaver.assin.util.isConnected
import kotlinx.android.synthetic.main.splash_screen_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createViewModel(Observer { onHasTitle(it!!) })
        modifyView()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun createViewModel(hasTitleObserver: Observer<Boolean>): SplashScreenViewModel {
        return ViewModelProviders.of(this).get(SplashScreenViewModel::class.java).init(this, hasTitleObserver)
    }

    private fun modifyView() {
        supportActionBar?.hide()
    }

    private fun publishView() {
        setContentView(R.layout.splash_screen_activity)
    }

    private fun bindActions() {
        splashScreenActivityRetryButton.setOnClickListener { splashScreenActivityRetryButton.visibility = INVISIBLE; loadMarketData() }
    }

    private fun onHasTitle(hasTitle: Boolean) {
        if (hasTitle)
            startActivity(Intent(this, StartActivity::class.java))
        else {
            publishView()
            bindActions()
            loadMarketData()
        }
    }

    private fun loadMarketData() {
        if (isConnected()) {
            splashScreenActivityActionTextView.setText("Loading data...")
            AssinWorkers.completeUpdate()
        } else {
            splashScreenActivityActionTextView.setText("No internet connection available...")
            splashScreenActivityRetryButton.visibility = VISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        startActivity(Intent(this, StartActivity::class.java))
    }
}
