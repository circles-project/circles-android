package org.futo.circles.core.update

interface AppUpdateProvider {

    fun getManager(): CirclesAppUpdateManager?

}