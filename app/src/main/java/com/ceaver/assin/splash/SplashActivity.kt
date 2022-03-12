package com.ceaver.assin.splash


import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.MainActivity
import com.ceaver.assin.R
import com.ceaver.assin.databinding.SplashActivityBinding
import com.ceaver.assin.system.SystemRepository
import com.ceaver.assin.util.isConnected
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        modifyView()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        if (SystemRepository.isInitialized())
            startActivity(Intent(this, MainActivity::class.java))
        else {
            publishView()
            bindActions()
            loadMarketData()
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun modifyView() {
        supportActionBar?.hide()
    }

    private fun publishView() {
        setContentView(R.layout.splash_activity)
    }

    private fun bindActions() {
        binding.splashActivityRetryButton.setOnClickListener { binding.splashActivityRetryButton.visibility = INVISIBLE; loadMarketData() }
    }

    private fun loadMarketData() {
        if (isConnected()) {
            binding.splashActivityActionTextView.setText("Loading data...")
            AssinWorkers.completeUpdate()
        } else {
            binding.splashActivityActionTextView.setText("No internet connection available...")
            binding.splashActivityRetryButton.visibility = VISIBLE
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}