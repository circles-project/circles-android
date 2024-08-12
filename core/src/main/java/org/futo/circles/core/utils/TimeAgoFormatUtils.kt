package org.futo.circles.core.utils

import android.content.Context
import android.text.format.DateFormat
import org.futo.circles.core.R
import java.util.Date
import kotlin.math.abs

object TimeAgoFormatUtils {

    private var second: Int = 1000 // milliseconds
    private var minute: Int = 60
    private var hour: Int = minute * 60
    private var day: Int = hour * 24
    private var week: Int = day * 7
    private var month: Int = day * 30

    fun getTimeAgoString(context: Context, fromDate: Long): String {
        val ms2 = System.currentTimeMillis()
        // get difference in milliseconds
        val diff = ms2 - fromDate

        val diffInSec = abs((diff / (second)).toInt().toDouble()).toInt()

        return if (diffInSec < minute) {
            context.getString(R.string.now)
        } else if ((diffInSec / hour) < 1) {
            (diffInSec / minute).toString() + "m"
        } else if ((diffInSec / day) < 1) {
            (diffInSec / hour).toString() + "h"
        } else if ((diffInSec / week) < 1) {
            val days = (diffInSec / day)
            context.resources.getQuantityString(R.plurals.day_count_format, days, days)
        } else if ((diffInSec / month) < 1) {
            if ((diffInSec / week) <= 1) {
                (diffInSec / week).toString() + " " + context.getString(R.string.week)
            } else {
                getFullDate(fromDate)
            }
        } else {
            getFullDate(fromDate)
        }
    }

    private fun getFullDate(fromDate: Long): String =
        DateFormat.format("MMM dd, yyyy", Date(fromDate)).toString()

}