package org.futo.circles.auth.subscriptions

import android.content.Context
import org.futo.circles.auth.R

fun String.formatIsoPeriod(context: Context): String = toDurationNumberPairs()
    .joinToString(separator = " ") { (number, duration) ->
        context.resources.getQuantityString(
            when (duration) {
                "D" -> R.plurals.days
                "W" -> R.plurals.weeks
                "M" -> R.plurals.months
                "Y" -> R.plurals.years

                else -> R.plurals.days
            }, number, number
        )
    }

private fun String.toDurationNumberPairs() = removePrefix("P")
    .chunked(2)
    .map { it[0].toString().toInt() to it[1].toString() }