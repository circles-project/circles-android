package com.futo.circles.io.service

import okhttp3.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface CirclesSignUpService {

    @POST("/_matrix/client/r0/register")
    suspend fun validateSignUpToken(
        @Path("type") type: String,
        @Path("token") token: String,
        @Path("session") session: String
    ): Response

}