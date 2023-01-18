package org.futo.circles.feature.notifications

interface Clock {
    fun epochMillis(): Long
}

class DefaultClock : Clock {

    override fun epochMillis(): Long {
        return System.currentTimeMillis()
    }
}
