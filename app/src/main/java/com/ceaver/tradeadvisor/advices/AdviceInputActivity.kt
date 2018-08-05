package com.ceaver.tradeadvisor.advices

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceaver.adviceadvisor.advices.AdviceRepository
import com.ceaver.tradeadvisor.IntentKeys
import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.util.CalendarHelper
import kotlinx.android.synthetic.main.activity_advice_input.*

class AdviceInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice_input)

        adviceDateEditText.setKeyListener(null) // hack to disable user input
    }

    override fun onStart() {
        super.onStart()

        val adviceId = intent.getLongExtra(IntentKeys.ADVICE_ID, 0)

        if (adviceId > 0)
            AdviceRepository.loadAdviceAsync(adviceId, true) { publishFields(it) }
    }

    private fun publishFields(advice: Advice) {
        adviceDateEditText.setText(CalendarHelper.convertDate(advice.adviceDate))
    }
}
