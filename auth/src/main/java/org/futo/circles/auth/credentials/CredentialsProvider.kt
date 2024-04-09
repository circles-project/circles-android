package org.futo.circles.auth.credentials

interface CredentialsProvider {

    fun getManager(): CredentialsManager?

}