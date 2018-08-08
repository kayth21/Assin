package com.ceaver.assin.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import java.time.LocalDate
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    interface DatePickerFragementCallback {
        fun onDatePickerFragmentDateSelected(tag: String, date: LocalDate)
    }

    var callback : DatePickerFragementCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default values for the picker
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = LocalDate.of(year, month+1, dayOfMonth)
        callback!!.onDatePickerFragmentDateSelected(tag, date)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as DatePickerFragementCallback
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}