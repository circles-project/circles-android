package org.futo.circles.core.utils

import java.util.Calendar
import java.util.Date

object DateUtils {

    fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1: Calendar = Calendar.getInstance().apply { setTime(Date(date1)) }
        val cal2: Calendar = Calendar.getInstance().apply { setTime(Date(date2)) }
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }

    fun isToday(date: Long): Boolean {
        val now: Calendar =
            Calendar.getInstance().apply { setTime(Date(System.currentTimeMillis())) }
        val cal: Calendar = Calendar.getInstance().apply { setTime(Date(date)) }

        return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                now.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)
    }

    fun isCurrentYear(date: Long): Boolean {
        val now: Calendar =
            Calendar.getInstance().apply { setTime(Date(System.currentTimeMillis())) }
        val cal: Calendar = Calendar.getInstance().apply { setTime(Date(date)) }
        return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
    }

}