package com.ceaver.assin.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.R
import com.ceaver.assin.system.SystemRepository
import com.ceaver.assin.util.isConnected
import kotlinx.android.synthetic.main.splash_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (requireActivity() as AppCompatActivity?)!!.supportActionBar!!.hide()
        return inflater.inflate(R.layout.splash_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        if (SystemRepository.isInitialized())
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
        else {
            bindActions()
            loadMarketData()
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun bindActions() {
        splashFragmentRetryButton.setOnClickListener { splashFragmentRetryButton.visibility = View.INVISIBLE; loadMarketData() }
    }

    private fun loadMarketData() {
        if (isConnected()) {
            splashFragmentActionTextView.setText("Loading data...")
            AssinWorkers.completeUpdate()
        } else {
            splashFragmentActionTextView.setText("No internet connection available...")
            splashFragmentRetryButton.visibility = View.VISIBLE
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
    }
}