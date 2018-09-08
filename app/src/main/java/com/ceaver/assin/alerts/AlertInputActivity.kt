package com.ceaver.assin.alerts

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceaver.assin.IntentKeys
import com.ceaver.assin.R
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.extensions.validateFields
import com.ceaver.assin.markets.MarketValuation
import com.ceaver.assin.markets.Title
import kotlinx.android.synthetic.main.activity_alert_input.*

class AlertInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_input)

        // TODO Start: Remove
        alertSymbolText.setText("Bitcoin (BTC)")
        alertSymbolText.setKeyListener(null) // hack to disable user input
        // TODO End
    }

    override fun onStart() {
        super.onStart()

        val alertId = intent.getLongExtra(IntentKeys.ALERT_ID, 0)

        if (alertId > 0)
            AlertRepository.loadAlertAsync(alertId, true) { publishFields(it); validateFields() }
        else {
            MarketValuation.load(Symbol.BTC, Symbol.USD).ifPresent { publishFields(it) }
            validateFields()
        }

        alertSaveButton.setOnClickListener {
            // TODO Replace with some generic code / better implementation
            if (alertSourceEditText.error != null || alertTargetEditText.error != null) {
                return@setOnClickListener
            }
            val alert = createAlert(alertId)
            AlertRepository.saveAlertAsync(alert)
            exitActivity()
        }
    }

    private fun publishFields(title: Title) {
        alertSourceEditText.setText(title.last.toString())
    }

    private fun validateFields() {
        alertSourceEditText.validateFields({ s -> (s.length >= 1) }, "Please enter amount")
        alertTargetEditText.validateFields({ s -> (s.length >= 1) }, "Please enter amount")
    }

    private fun publishFields(alert: Alert) {
        alertSourceEditText.setText(alert.source.toString())
        alertTargetEditText.setText(alert.target.toString())
    }

    private fun createAlert(alertId: Long): Alert {
        val source = alertSourceEditText.text.toString().toDouble()
        val target = alertTargetEditText.text.toString().toDouble()
        return Alert(alertId, Symbol.BTC, AlertType.RECURRING_STABLE, source, target, "")
    }

    private fun exitActivity() {
        val intent = Intent(this, AlertListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
