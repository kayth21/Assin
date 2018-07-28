package com.ceaver.tradeadvisor.advices

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ceaver.adviceadvisor.advices.AdviceRepository
import com.ceaver.tradeadvisor.IntentKeys
import com.ceaver.tradeadvisor.MainActivity
import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.util.CalendarHelper
import kotlinx.android.synthetic.main.activity_advice_input.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.LocalDate

class AdviceInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice_input)

        adviceDateEditText.setKeyListener(null) // hack to disable user input
    }

    override fun onStart() {
        super.onStart()

        val adviceId = intent.getLongExtra(IntentKeys.ADVICE_ID, 0)

        if (adviceId > 0) AdviceRepository.loadAdvice(adviceId, { publishFields(it) })
    }

    private fun publishFields(advice: Advice) {
        adviceDateEditText.setText(CalendarHelper.convertDate(advice.adviceDate))
    }
}
